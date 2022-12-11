package com.varijon.tinies.SimplyGTS.command;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.command.PixelCommand;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ServerNPCRegistry;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItemWithVariation;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopkeeperData;
import com.varijon.tinies.SimplyGTS.SimplyGTS;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;
import com.varijon.tinies.SimplyGTS.gui.GuiPagesItems;
import com.varijon.tinies.SimplyGTS.gui.GuiPagesManage;
import com.varijon.tinies.SimplyGTS.gui.GuiPagesPokemon;
import com.varijon.tinies.SimplyGTS.object.GTSListing;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;
import com.varijon.tinies.SimplyGTS.object.GTSListingPokemon;
import com.varijon.tinies.SimplyGTS.object.ItemConfigMinPrice;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;
import com.varijon.tinies.SimplyGTS.util.Util;

import ca.landonjw.gooeylibs2.api.UIManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.server.permission.PermissionAPI;

public class GTSCommand extends PixelCommand {

	private List aliases;
	private static final Pattern periodPattern = Pattern.compile("([0-9]+)([hdwmy])");
	
	public GTSCommand(CommandDispatcher<CommandSource> dispatcher)
	{
       super(dispatcher, "gts", "/gts", 2);   
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void execute(CommandSource sender, String[] args) throws CommandException, CommandSyntaxException
	{
		if(sender.getPlayerOrException() instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity player = sender.getPlayerOrException();
//			if(!PermissionAPI.hasPermission(player,"simplygts.gts"))
//			{
//				player.sendMessage(new StringTextComponent(TextFormatting.RED + "You don't have permission to use this command"), UUID.randomUUID());
//		        return;
//			}
			if(args.length == 0)
			{
				UIManager.openUIForcefully(player, GuiPagesPokemon.getPokemonMenu(GTSDataManager.getGTSPokemonListings(), player, 1));
				return;
			}
			if(args[0].equals("pokemon"))
			{
				UIManager.openUIForcefully(player, GuiPagesPokemon.getPokemonMenu(GTSDataManager.getGTSPokemonListings(), player, 1));
				return;
			}
			if(args[0].equals("manage"))
			{
				UIManager.openUIForcefully(player, GuiPagesManage.getManageMenu(GTSDataManager.getAllListingsPlayer(player.getUUID()),player, 1));
				return;
			}
			if(args[0].equals("items"))
			{
				//change to items
				UIManager.openUIForcefully(player, GuiPagesItems.getItemMenu(GTSDataManager.getGTSItemsListings(), player, 1));
				return;
			}
			if(args[0].equals("help"))
			{
				sendCommandOptionHelp(player);
				return;
			}
			if(args[0].equals("sell"))
			{
				if(args.length == 1)
				{
					player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell <pokemon/item>"), UUID.randomUUID());
					return;						
				}
				if(args[1].equals("pokemon"))
				{
					if(args.length < 3)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell <pokemon> <slot> [price]"), UUID.randomUUID());
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Leave out price to sell for minimum"), UUID.randomUUID());		
						return;
					}
					if(GTSDataManager.getPlayerListingTotal(player.getUUID()) >= GTSDataManager.getConfig().getMaxPlayerListings())
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "You cannot have more than " + GTSDataManager.getConfig().getMaxPlayerListings() + " listings!"), UUID.randomUUID());
						return;
					}
					if(!NumberUtils.isNumber(args[2]))
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid slot!"), UUID.randomUUID());
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell pokemon <slot> [price]"), UUID.randomUUID());	
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Leave out price to sell for minimum"), UUID.randomUUID());							
						return;
					}
					int slot = Integer.parseInt(args[2]) - 1;
					if(slot < 0 || slot > 5)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid slot!"), UUID.randomUUID());
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell pokemon <slot> [price]"), UUID.randomUUID());	
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Leave out price to sell for minimum"), UUID.randomUUID());							
						return;
					}
					PlayerPartyStorage partySeller = StorageProxy.getParty(player);
					if(partySeller == null)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Party is null, contact staff!"), UUID.randomUUID());
						return;
					}
					if(partySeller.countAblePokemon() < 2)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "You cannot sell your last able Pokemon!"), UUID.randomUUID());
						return;
					}
					
					BattleController bc = BattleRegistry.getBattle(player);
					if(bc != null)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "You cannot do this in battle!"), UUID.randomUUID());
						return;
					}
					
					Pokemon pokemon = partySeller.get(slot);
					if(pokemon == null)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "No Pokemon found!"), UUID.randomUUID());
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell pokemon <slot> [price]"), UUID.randomUUID());	
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Leave out price to sell for minimum"), UUID.randomUUID());	
						return;
					}
					int cost = 100;
					int minimumPrice = calculateMinimumPricePokemon(pokemon);
					if(args.length == 4)
					{
						if(!NumberUtils.isNumber(args[3]))
						{
							player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid price!"), UUID.randomUUID());
							player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell pokemon <slot> [price]"), UUID.randomUUID());	
							player.sendMessage(new StringTextComponent(TextFormatting.RED + "Leave out price to sell for minimum"), UUID.randomUUID());							
							return;
						}
						else
						{
							cost = Integer.parseInt(args[3]);
						}
					}
					else
					{
						cost = minimumPrice;
					}
					if(cost > GTSDataManager.getConfig().getMaxPrice() || cost < 100 || cost < minimumPrice)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Minimum price for a Pokemon with these stats " + TextFormatting.GOLD + minimumPrice), UUID.randomUUID());
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell pokemon <slot> [price]"), UUID.randomUUID());	
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Leave out price to sell for minimum"), UUID.randomUUID());							
						return;
					}
					partySeller.retrieveAll();
					CompoundNBT nbtData = pokemon.writeToNBT(new CompoundNBT());

					UUID listingUUID = UUID.randomUUID();
					GTSListingPokemon listingData = GTSDataManager.addListingPokemonData(new GTSListingPokemon(EnumListingType.Pokemon, EnumListingStatus.Active, System.currentTimeMillis(), System.currentTimeMillis() + parsePeriod(GTSDataManager.getConfig().getListingDuration()), cost, player.getUUID(), listingUUID, nbtData.toString()));
					SimplyGTS.logger.info(player.getName().getString() + " listed " + pokemon.getDisplayName() + " for " + listingData.getListingPrice());
					partySeller.set(slot, null);
					partySeller.setNeedsSaving();
					GTSDataManager.writeListingPokemonData(listingData);

					TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
					chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " You listed your "));
					
					TranslationTextComponent listedPokemonComponent = Util.getHoverText(pokemon, player);
					chatTrans.append(listedPokemonComponent);
					listedPokemonComponent.setStyle(listedPokemonComponent.getStyle().withClickEvent(
							new ClickEvent(ClickEvent.Action.RUN_COMMAND, 
					        				"/gts showlisting " + listingUUID)
					        		));
					
					chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + cost + TextFormatting.GREEN + "!" ));
					player.sendMessage(chatTrans, UUID.randomUUID());	
					

					TranslationTextComponent chatTrans2 = new TranslationTextComponent("", new Object());
					chatTrans2.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "] " + TextFormatting.GOLD + player.getName().getString()+ TextFormatting.GREEN + " listed "));
					chatTrans2.append(listedPokemonComponent);
					chatTrans2.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + cost + TextFormatting.GREEN + "!" ));
					for(ServerPlayerEntity targetPlayer : player.getServer().getPlayerList().getPlayers())
					{
						if(!targetPlayer.getName().getString().contains(player.getName().getString()))
						{
							targetPlayer.sendMessage(chatTrans2, UUID.randomUUID());	
						}
					}
					
					return;
				}
				if(args[1].equals("item"))
				{
					if(args.length < 2)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell <item> [price]"), UUID.randomUUID());
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Leave out price to sell for half shopkeeper price"), UUID.randomUUID());	
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Price is per item"), UUID.randomUUID());			
						return;
					}
					if(GTSDataManager.getPlayerListingTotal(player.getUUID()) >= GTSDataManager.getConfig().getMaxPlayerListings())
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "You cannot have more than " + GTSDataManager.getConfig().getMaxPlayerListings() + " listings!"), UUID.randomUUID());
						return;
					}
					if(player.getMainHandItem() == null)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Hand is empty!"), UUID.randomUUID());			
						return;
					}
					if(player.getMainHandItem().getItem() == Items.AIR)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Hand is empty!"), UUID.randomUUID());			
						return;
					}
					
					ItemStack itemToSell = player.getMainHandItem();
					int cost = 0;
					if(args.length == 2)
					{
						ShopItemWithVariation shopItem = null;
						for(ShopkeeperData shopData : ServerNPCRegistry.getEnglishShopkeepers())
						{
							for(ShopItemWithVariation shopItemVar : shopData.getItemList())
							{
								if(shopItemVar.getItemStack().getItem() == itemToSell.getItem())
								{
									if(itemToSell.getDamageValue() == shopItemVar.getItemStack().getDamageValue())
									{
										if(itemToSell.hasTag() && shopItemVar.getItemStack().hasTag())
										{
											if(itemToSell.getTag().contains("tm"))
											{
												if(itemToSell.getTag().getInt("tm") == shopItemVar.getItemStack().getTag().getInt("tm"))
												{
													shopItem = shopItemVar;
													break;
												}
											}
											if(itemToSell.getTag().equals(shopItemVar.getItemStack().getTag()))
											{
												shopItem = shopItemVar;		
												break;											
											}
										}
										else
										{
											shopItem = shopItemVar;
											break;
										}
									}
								}
							}
						}
						if(shopItem == null)
						{
							player.sendMessage(new StringTextComponent(TextFormatting.RED + "No shop sells this item!"), UUID.randomUUID());
							player.sendMessage(new StringTextComponent(TextFormatting.RED + "Please add a price"), UUID.randomUUID());
							return;
						}
						cost = shopItem.getBuyCost() / 2;
					}
					int minimumPrice = calculateMinimumPriceItem(itemToSell);
					if(cost != 0 && cost < minimumPrice)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Minimum price for this item is " + TextFormatting.GOLD + minimumPrice), UUID.randomUUID());
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Changing to minimum price"), UUID.randomUUID());
						cost = minimumPrice;
					}
					if(args.length == 3)
					{
						if(!NumberUtils.isNumber(args[2]))
						{
							player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid price!"), UUID.randomUUID());
							player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell item [price]"), UUID.randomUUID());	
							player.sendMessage(new StringTextComponent(TextFormatting.RED + "Leave out price to sell for half shopkeeper price"), UUID.randomUUID());	
							player.sendMessage(new StringTextComponent(TextFormatting.RED + "Price is per item"), UUID.randomUUID());									
							return;
						}
						else
						{
							cost = Integer.parseInt(args[2]);
						}
					}
					else
					{
						if(cost == 0)
						{
							cost = minimumPrice;
						}
					}
					if(cost > GTSDataManager.getConfig().getMaxPrice() || cost < minimumPrice)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid price, minimum is " + TextFormatting.GOLD + minimumPrice), UUID.randomUUID());
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell item [price]"), UUID.randomUUID());	
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Leave out price to sell for half shopkeeper price"), UUID.randomUUID());		
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Price is per item"), UUID.randomUUID());								
						return;
					}
					
					player.inventory.removeItem(player.getMainHandItem());
					
					//detect and send changes
					
					
					UUID listingUUID = UUID.randomUUID();
					GTSListingItem listingData = GTSDataManager.addListingItemsData(new GTSListingItem(EnumListingType.Item, EnumListingStatus.Active, System.currentTimeMillis(), System.currentTimeMillis() + parsePeriod(GTSDataManager.getConfig().getListingDuration()), cost*itemToSell.getCount(), player.getUUID(), listingUUID, itemToSell.getItem().getRegistryName().toString(), itemToSell.getDamageValue(),itemToSell.hasTag() ? itemToSell.getTag().toString() : "", itemToSell.getCount(),ITextComponent.Serializer.toJson(itemToSell.getDisplayName())));
					SimplyGTS.logger.info(player.getName().getString() + " listed " + itemToSell.getCount() + "x " + listingData.getItemName() + " for " + listingData.getListingPrice());
					GTSDataManager.writeListingItemsData(listingData);
					
					TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
					chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " You listed your " + TextFormatting.WHITE + itemToSell.getCount() + "x "));
					
					TranslationTextComponent listedItemComponent = (TranslationTextComponent) itemToSell.getDisplayName();
					listedItemComponent.setStyle(listedItemComponent.getStyle().withClickEvent(
							new ClickEvent(ClickEvent.Action.RUN_COMMAND, 
					        				"/gts showlisting " + listingUUID)
					        		));
					
					chatTrans.append(listedItemComponent);
					chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + cost*itemToSell.getCount() + TextFormatting.GREEN + "!" ));
					player.sendMessage(chatTrans, UUID.randomUUID());	
					

					TranslationTextComponent chatTrans2 = new TranslationTextComponent("", new Object());
					chatTrans2.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "] " + TextFormatting.GOLD + player.getName().getString()+ TextFormatting.GREEN + " listed " + TextFormatting.WHITE + itemToSell.getCount() + "x "));
					chatTrans2.append(listedItemComponent);
					chatTrans2.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + cost*itemToSell.getCount() + TextFormatting.GREEN + "!" ));
					for(ServerPlayerEntity targetPlayer : sender.getServer().getPlayerList().getPlayers())
					{
						if(!targetPlayer.getName().getString().contains(player.getName().getString()))
						{
							targetPlayer.sendMessage(chatTrans2, UUID.randomUUID());	
						}
					}
					
					return;
				}
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid subcommand! Use /gts help"), UUID.randomUUID());
				return;
			}
			if(args[0].equals("showlisting"))
			{
				if(args.length == 2)
				{
					UUID listingID = null;
					try 
					{
						listingID = UUID.fromString(args[1]);
					}
					catch(IllegalArgumentException ex)
					{
						
					}
					if(listingID == null)
					{
						return;
					}
					GTSListing gtsListing = GTSDataManager.getListingDataEither(listingID);
					if(gtsListing == null)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Listing sold or cancelled!"), UUID.randomUUID());
						return;
					}
					if(gtsListing.getListingStatus() == EnumListingStatus.Sold)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Listing sold or cancelled!"), UUID.randomUUID());
						return;
					}
					if(gtsListing.getListingStatus() == EnumListingStatus.Expired)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Listing sold or cancelled!"), UUID.randomUUID());
						return;
					}
					boolean isListingOwner = false;
					if(gtsListing.getListingOwner().toString().equals(player.getUUID().toString()))
					{
						isListingOwner = true;
					}
					if(gtsListing instanceof GTSListingPokemon)
					{
						GTSListingPokemon gtsListingPokemon = (GTSListingPokemon) gtsListing;
						UIManager.closeUI(player);
						UIManager.openUIForcefully(player, GuiPagesPokemon.getCancelConfirmMenuPokemon((int) gtsListingPokemon.getListingPrice(), gtsListingPokemon.getListingID(), 1, isListingOwner));	
						return;
					}
					if(gtsListing instanceof GTSListingItem)
					{
						GTSListingItem gtsListingItem = (GTSListingItem) gtsListing;
						UIManager.closeUI(player);
						UIManager.openUIForcefully(player, GuiPagesItems.getCancelConfirmMenuItems((int) gtsListingItem.getListingPrice(), gtsListingItem.getListingID(), 1, isListingOwner));	
					}
					return;						
				}
				return;
			}
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid subcommand! Use /gts help"), UUID.randomUUID());
			return;
			
		}
		return;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args, BlockPos pos) throws CommandSyntaxException 
	{
		if(args.length == 1)
		{
			ArrayList<String> lstTabComplete = new ArrayList<>();
			lstTabComplete.add("items");
			lstTabComplete.add("sell");
			lstTabComplete.add("pokemon");
			lstTabComplete.add("help");
			lstTabComplete.add("manage");

			return lstTabComplete;
		}
		if(args.length == 2)
		{
			ArrayList<String> lstTabComplete = new ArrayList<>();
			lstTabComplete.add("item");
			lstTabComplete.add("pokemon");

			return lstTabComplete;
		}
		
		return Collections.emptyList();
	}
	
	private void sendCommandOptionHelp(ServerPlayerEntity player)
	{

		player.sendMessage(new StringTextComponent(TextFormatting.YELLOW + "GTS Command Options:"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts " + TextFormatting.GOLD + "- Open Pokemon GTS window"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts pokemon " + TextFormatting.GOLD + "- Open Pokemon GTS window"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts items " + TextFormatting.GOLD + "- Open Items GTS window"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts manage " + TextFormatting.GOLD + "- Manage your GTS listings"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts help " + TextFormatting.GOLD + "- Show this command help"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts sell item/pokemon " + TextFormatting.GOLD + "- Sell items or Pokemon, price per item"), UUID.randomUUID());
	}
	
	
	int calculateMinimumPriceItem(ItemStack item)
	{
		int finalPrice = 0;
		
		for(ItemConfigMinPrice itemConfig : GTSDataManager.getConfig().getLstMinItemPrices())
		{
			if(itemConfig.getItemName().equals(item.getItem().getRegistryName().toString()))
			{
				if(item.hasTag())
				{
					if(!item.getTag().toString().equals(itemConfig.getItemNBT()))
					{
						continue;
					}
					if(itemConfig.getItemMeta() == -1)
					{
						finalPrice += itemConfig.getMinPrice();
					}
					if(itemConfig.getItemMeta() == item.getDamageValue())
					{
						finalPrice += itemConfig.getMinPrice();
					}
				}
				else
				{
					if(itemConfig.getItemMeta() == -1)
					{
						finalPrice += itemConfig.getMinPrice();
					}
					if(itemConfig.getItemMeta() == item.getDamageValue())
					{
						finalPrice += itemConfig.getMinPrice();
					}
				}
			}
		}
//		ShopItemWithVariation shopItem = null;
//		for(ShopkeeperData shopData : ServerNPCRegistry.getEnglishShopkeepers())
//		{
//			for(ShopItemWithVariation shopItemVar : shopData.getItemList())
//			{
//				if(shopItemVar.getItemStack().getItem() == item.getItem())
//				{
//					if(item.getMetadata() == shopItemVar.getItemStack().getMetadata())
//					{
//						if(item.hasTagCompound() && shopItemVar.getItemStack().hasTagCompound())
//						{
//							if(item.getTagCompound().hasKey("tm"))
//							{
//								if(item.getTagCompound().getInteger("tm") == shopItemVar.getItemStack().getTagCompound().getInteger("tm"))
//								{
//									shopItem = shopItemVar;
//									break;
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		if(shopItem != null)
//		{
//			finalPrice += shopItem.getBuyCost() / 4;
//		}
		return finalPrice;
	}
	int calculateMinimumPricePokemon(Pokemon pokemon)
	{
		int finalPrice = 0;
		if(Util.checkForHiddenAbility(pokemon))
		{
			finalPrice+= GTSDataManager.getConfig().getMinHAPrice();
		}
		int pixelmonIVHP = pokemon.getStats().getIVs().getStat(BattleStatsType.HP);
		int pixelmonIVAttack = pokemon.getStats().getIVs().getStat(BattleStatsType.ATTACK);
		int pixelmonIVDefense = pokemon.getStats().getIVs().getStat(BattleStatsType.DEFENSE);
		int pixelmonIVSpAtt = pokemon.getStats().getIVs().getStat(BattleStatsType.SPECIAL_ATTACK);
		int pixelmonIVSpDef = pokemon.getStats().getIVs().getStat(BattleStatsType.SPECIAL_DEFENSE);
		int pixelmonIVSpeed = pokemon.getStats().getIVs().getStat(BattleStatsType.SPEED);
		
		int maxIVCount = 0;
		if(pixelmonIVHP == 31)
		{
			maxIVCount++;
		}
		if(pixelmonIVAttack == 31)
		{
			maxIVCount++;
		}
		if(pixelmonIVDefense == 31)
		{
			maxIVCount++;
		}
		if(pixelmonIVSpAtt == 31)
		{
			maxIVCount++;
		}
		if(pixelmonIVSpDef == 31)
		{
			maxIVCount++;
		}
		if(pixelmonIVSpeed == 31)
		{
			maxIVCount++;
		}
		if(maxIVCount > 2)
		{
			switch(maxIVCount)
			{
				case(3):
					finalPrice+= GTSDataManager.getConfig().getMinPrice3IV();
					break;
				case(4):
					finalPrice+= GTSDataManager.getConfig().getMinPrice4IV();
					break;
				case(5):
					finalPrice+= GTSDataManager.getConfig().getMinPrice5IV();
					break;
				case(6):
					finalPrice+= GTSDataManager.getConfig().getMinPrice6IV();
					break;
			}
		}
		if(finalPrice == 0)
		{
			finalPrice = 100;
		}
		
		return finalPrice;
	}
	
	public static long parsePeriod(String period)
	{
	    period = period.toLowerCase(Locale.ENGLISH);
	    Matcher matcher = periodPattern.matcher(period);
	    Instant instant=Instant.EPOCH;
	    while(matcher.find()){
	        int num = Integer.parseInt(matcher.group(1));
	        String typ = matcher.group(2);
	        switch (typ) {
	        	case "m":
	        		instant=instant.plus(Duration.ofMinutes(num));
	        		break;
	            case "h":
	                instant=instant.plus(Duration.ofHours(num));
	                break;
	            case "d":
	                instant=instant.plus(Duration.ofDays(num));
	                break;
	            case "w":
	                instant=instant.plus(Period.ofWeeks(num));
	                break;
	        }
	    }
	    return instant.toEpochMilli();
	}
}
