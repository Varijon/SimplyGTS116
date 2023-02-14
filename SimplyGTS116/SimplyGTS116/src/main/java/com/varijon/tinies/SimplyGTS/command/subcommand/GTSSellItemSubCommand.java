package com.varijon.tinies.SimplyGTS.command.subcommand;

import java.util.UUID;

import org.apache.commons.lang3.math.NumberUtils;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ServerNPCRegistry;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItemWithVariation;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopkeeperData;
import com.varijon.tinies.SimplyGTS.SimplyGTS;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;
import com.varijon.tinies.SimplyGTS.object.GTSListingPokemon;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;
import com.varijon.tinies.SimplyGTS.util.Util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;

public class GTSSellItemSubCommand 
{
	public static void handleSubCommand(ServerPlayerEntity player, String[] args)
	{
		if(args.length < 2)
		{	
			sendSubCommandUsage(player);
			return;
		}
		if(args.length > 3)
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
		int minimumPrice = Util.calculateMinimumPriceItem(itemToSell);
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
				sendSubCommandUsage(player);								
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
			sendSubCommandUsage(player);						
			return;
		}
		
		player.inventory.removeItem(player.getMainHandItem());
		
		//detect and send changes
		
		
		UUID listingUUID = UUID.randomUUID();
		GTSListingItem listingData = GTSDataManager.addListingItemsData(new GTSListingItem(EnumListingType.Item, EnumListingStatus.Active, System.currentTimeMillis(), System.currentTimeMillis() + Util.parsePeriod(GTSDataManager.getConfig().getListingDuration()), cost*itemToSell.getCount(), player.getUUID(), listingUUID, itemToSell.getItem().getRegistryName().toString(), itemToSell.getDamageValue(),itemToSell.hasTag() ? itemToSell.getTag().toString() : "", itemToSell.getCount(),ITextComponent.Serializer.toJson(itemToSell.getDisplayName())));
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
	
	static void sendSubCommandUsage(ServerPlayerEntity player)
	{
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell item [price]"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "The item in hand is what is sold"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "Leave out price to sell for half shopkeeper price"), UUID.randomUUID());	
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "Price is per item"), UUID.randomUUID());				
	}
}
