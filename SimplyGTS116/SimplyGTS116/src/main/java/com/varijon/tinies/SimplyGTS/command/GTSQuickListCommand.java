package com.varijon.tinies.SimplyGTS.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.command.PixelCommand;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItemWithVariation;
import com.varijon.tinies.SimplyGTS.SimplyGTS;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;
import com.varijon.tinies.SimplyGTS.object.GTSItemPriceHistory;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;
import com.varijon.tinies.SimplyGTS.object.GTSPriceHistoryList;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;
import com.varijon.tinies.SimplyGTS.util.Util;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.server.permission.PermissionAPI;

public class GTSQuickListCommand extends PixelCommand {

	private static final List<String> ALIASES = Lists.newArrayList(new String[] {"qs"});
	
	public GTSQuickListCommand(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher, "quicklist", "/quicklist", 2);   
	}

    @Override
    public List<String> getAliases() 
    {
        return ALIASES;
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
			if(args.length == 1 && args[0].equals("help"))
			{	
				sendSubCommandUsage(player);
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
			
			ItemStack itemToSell = player.getMainHandItem().copy();
			int cost = 0;
			
			if(args.length == 1)
			{
				if(!NumberUtils.isNumber(args[0]))
				{
					player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid price!"), UUID.randomUUID());
					sendSubCommandUsage(player);								
					return;
				}
				else
				{
					cost = Integer.parseInt(args[0]);
				}
			}
			else
			{
				GTSPriceHistoryList priceHistoryList = GTSDataManager.getPriceHistoryList(itemToSell.getItem().getRegistryName().toString());
				if(priceHistoryList != null)
				{
					GTSItemPriceHistory priceHistory = priceHistoryList.getItemPriceHistory(itemToSell);
					if(priceHistory != null)
					{
						if(priceHistory.getAveragePrice() != -1)
						{
							cost = priceHistory.getAveragePrice();							
						}
						else
						{
							ShopItemWithVariation shopItem = Util.getShopItem(itemToSell);
							if(shopItem == null)
							{
								player.sendMessage(new StringTextComponent(TextFormatting.RED + "No shop sells this item and nobody sold this item before!"), UUID.randomUUID());
								player.sendMessage(new StringTextComponent(TextFormatting.RED + "Please add a price manually"), UUID.randomUUID());
								sendSubCommandUsage(player);
								return;
							}
							player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "No price history found, defaulting to half shop price"), UUID.randomUUID());
							cost = shopItem.getBuyCost() / 2;
						}
					}
					else
					{
						ShopItemWithVariation shopItem = Util.getShopItem(itemToSell);
						if(shopItem == null)
						{
							player.sendMessage(new StringTextComponent(TextFormatting.RED + "No shop sells this item and nobody sold this item before!"), UUID.randomUUID());
							player.sendMessage(new StringTextComponent(TextFormatting.RED + "Please add a price manually"), UUID.randomUUID());
							sendSubCommandUsage(player);
							return;
						}
						player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "No price history found, defaulting to half shop price"), UUID.randomUUID());
						cost = shopItem.getBuyCost() / 2;
					}
				}
				else
				{
					ShopItemWithVariation shopItem = Util.getShopItem(itemToSell);
					if(shopItem == null)
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "No shop sells this item and nobody sold this item before!"), UUID.randomUUID());
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "Please add a price manually"), UUID.randomUUID());
						sendSubCommandUsage(player);
						return;
					}
					player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "No price history found, defaulting to half shop price"), UUID.randomUUID());
					cost = shopItem.getBuyCost() / 2;
				}
			}
			
			int minimumPrice = Util.calculateMinimumPriceItem(itemToSell);
			
			if(cost != 0 && cost < minimumPrice)
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Minimum price for this item is " + TextFormatting.GOLD + minimumPrice), UUID.randomUUID());
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Changing to minimum price"), UUID.randomUUID());
				cost = minimumPrice;
			}
			
			if(cost > GTSDataManager.getConfig().getMaxPrice() || cost < minimumPrice)
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid price, minimum is " + TextFormatting.GOLD + minimumPrice), UUID.randomUUID());
				sendSubCommandUsage(player);						
				return;
			}
			player.inventory.removeItem(player.getMainHandItem());
					
			UUID listingUUID = UUID.randomUUID();
//			GTSListingItem listingData = GTSDataManager.addListingItemsData(new GTSListingItem(EnumListingType.Item, EnumListingStatus.Active, System.currentTimeMillis(), System.currentTimeMillis() + Util.parsePeriod(GTSDataManager.getConfig().getListingDuration()), cost*itemToSell.getCount(), player.getUUID(), listingUUID, itemToSell.getItem().getRegistryName().toString(),itemToSell.hasTag() ? itemToSell.getTag().toString() : "", itemToSell.getCount(),ITextComponent.Serializer.toJson(itemToSell.getDisplayName())));
						
			GTSListingItem listingData = GTSDataManager.addListingItemsData(new GTSListingItem(EnumListingType.Item, EnumListingStatus.Active, System.currentTimeMillis(), System.currentTimeMillis() + Util.parsePeriod(GTSDataManager.getConfig().getListingDuration()), cost*itemToSell.getCount(), player.getUUID(), listingUUID, itemToSell.serializeNBT().toString()));
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
			for(ServerPlayerEntity targetPlayer : player.getServer().getPlayerList().getPlayers())
			{
				if(!targetPlayer.getName().getString().contains(player.getName().getString()))
				{
					targetPlayer.sendMessage(chatTrans2, UUID.randomUUID());	
				}
			}
			return;
			
		}
		return;
	}
	
	static void sendSubCommandUsage(ServerPlayerEntity player)
	{
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /quicklist [price]"), UUID.randomUUID());		
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "Price is optional, autocomplete for price shows amount"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "Defaults to price history, attemps shopkeeper if never sold"), UUID.randomUUID());		
	}



	@Override
	public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args, BlockPos pos) throws CommandSyntaxException 
	{
		if(args.length == 1)
		{
			int suggestedSellPrice = 0;
			
			ServerPlayerEntity player = sender.getPlayerOrException();
			if(player.getMainHandItem() == null)
			{
				return Collections.emptyList();
			}
			if(player.getMainHandItem().getItem() == Items.AIR)
			{
				return Collections.emptyList();
			}
			ItemStack itemToSell = player.getMainHandItem();
			
			GTSPriceHistoryList priceHistoryList = GTSDataManager.getPriceHistoryList(itemToSell.getItem().getRegistryName().toString());
			if(priceHistoryList != null)
			{
				GTSItemPriceHistory priceHistory = priceHistoryList.getItemPriceHistory(itemToSell);
				if(priceHistory != null)
				{
					if(priceHistory.getAveragePrice() != -1)
					{
						suggestedSellPrice = priceHistory.getAveragePrice();							
					}
					else
					{
						ShopItemWithVariation shopItem = Util.getShopItem(itemToSell);
						if(shopItem == null)
						{
							return Collections.emptyList();
						}
						suggestedSellPrice = shopItem.getBuyCost() / 2;
					}
				}
				else
				{
					ShopItemWithVariation shopItem = Util.getShopItem(itemToSell);
					if(shopItem == null)
					{
						return Collections.emptyList();
					}
					suggestedSellPrice = shopItem.getBuyCost() / 2;
				}
			}
			else
			{
				ShopItemWithVariation shopItem = Util.getShopItem(itemToSell);
				if(shopItem == null)
				{
					return Collections.emptyList();
				}
				suggestedSellPrice = shopItem.getBuyCost() / 2;
			}
			
			ArrayList<String> lstTabComplete = new ArrayList<>();
			lstTabComplete.add(suggestedSellPrice + "");

			return lstTabComplete;
		}
		
		return Collections.emptyList();
	}

}
