package com.varijon.tinies.SimplyGTS.gui;

import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.pixelmonmod.pixelmon.entities.npcs.registry.ServerNPCRegistry;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItemWithVariation;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopkeeperData;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.util.Constants;

public class ItemListingDisplay 
{
	public static ItemStack getItemListingDisplay(ItemStack itemStack, UUID player, GTSListingItem gtsListingItem, boolean isCancel)
	{
		ItemStack displayItemStack = itemStack.copy();
		CompoundNBT itemNBT = displayItemStack.getTag();
		
		if(itemNBT == null)
		{
			itemNBT = new CompoundNBT();
		}
		if(itemNBT.isEmpty())
		{
			itemNBT.put("display", new CompoundNBT());
		}
		if(!itemNBT.contains("display"))
		{
			itemNBT.put("display", new CompoundNBT());			
		}
		CompoundNBT displayNBT = itemNBT.getCompound("display");
		ListNBT newLoreList = new ListNBT();
		ListNBT loreList = new ListNBT();
		if(displayNBT.contains("Lore"))
		{
			loreList = displayNBT.getList("Lore", Constants.NBT.TAG_STRING);
		}

		String playerName = UsernameCache.getLastKnownUsername(gtsListingItem.getListingOwner());

		if(playerName == null)
		{
			playerName = "Someone";
		}
		if(gtsListingItem.getListingOwner().equals(player) && !isCancel)
		{
			newLoreList.add(StringNBT.valueOf(TextFormatting.GREEN + "Click here to cancel listing!"));
		}
		else
		{
			newLoreList.add(StringNBT.valueOf(TextFormatting.RED + "Seller: " + TextFormatting.GOLD + playerName));
		}
		newLoreList.add(StringNBT.valueOf(TextFormatting.RED + "Price: " + TextFormatting.GOLD + gtsListingItem.getListingPrice()));
		newLoreList.add(StringNBT.valueOf(TextFormatting.RED + "Remaining: " + TextFormatting.GOLD + DurationFormatUtils.formatDuration(gtsListingItem.getListingTimeRemaining(),"dd'd 'HH'h 'mm'm 'ss's'", false)));
		int shopPrice = getShopPrice(itemStack);
		if(shopPrice != 0)
		{
			newLoreList.add(StringNBT.valueOf(TextFormatting.RED + "Shop Price: " + TextFormatting.GOLD + shopPrice * itemStack.getCount()));			
		}
		
		for(INBT loreString : loreList)
		{
			newLoreList.add(loreString);
		}
		displayNBT.put("Lore", newLoreList);
		itemNBT.put("display", displayNBT);
		displayItemStack.setTag(itemNBT);
		
		return displayItemStack;
	}
	
	public static ArrayList<String> getItemListingDisplayList(ItemStack itemStack, UUID player, GTSListingItem gtsListingItem, boolean isCancel)
	{
		ItemStack displayItemStack = itemStack.copy();
		CompoundNBT itemNBT = displayItemStack.getTag();
		
		if(itemNBT == null)
		{
			itemNBT = new CompoundNBT();
		}
		if(itemNBT.isEmpty())
		{
			itemNBT.put("display", new CompoundNBT());
		}
		if(!itemNBT.contains("display"))
		{
			itemNBT.put("display", new CompoundNBT());			
		}
		CompoundNBT displayNBT = itemNBT.getCompound("display");
		ArrayList<String> newLoreList = new ArrayList<String>();
		ListNBT loreList = new ListNBT();
		if(displayNBT.contains("Lore"))
		{
			loreList = displayNBT.getList("Lore", Constants.NBT.TAG_STRING);
		}

		String playerName = UsernameCache.getLastKnownUsername(gtsListingItem.getListingOwner());

		if(playerName == null)
		{
			playerName = "Someone";
		}
		if(gtsListingItem.getListingOwner().equals(player) && !isCancel)
		{
			newLoreList.add(TextFormatting.GREEN + "Click here to cancel listing!");
		}
		else
		{
			newLoreList.add(TextFormatting.RED + "Seller: " + TextFormatting.GOLD + playerName);
		}
		newLoreList.add(TextFormatting.RED + "Price: " + TextFormatting.GOLD + gtsListingItem.getListingPrice());
		newLoreList.add(TextFormatting.RED + "Remaining: " + TextFormatting.GOLD + DurationFormatUtils.formatDuration(gtsListingItem.getListingTimeRemaining(),"dd'd 'HH'h 'mm'm 'ss's'", false));
		int shopPrice = getShopPrice(itemStack);
		if(shopPrice != 0)
		{
			newLoreList.add(TextFormatting.RED + "Shop Price: " + TextFormatting.GOLD + shopPrice * itemStack.getCount());			
		}
		
		for(INBT loreString : loreList)
		{
			newLoreList.add(loreString.getAsString());
		}
		
		return newLoreList;
	}
	
	public static int getShopPrice(ItemStack itemToSell)
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
			return 0;
		}
		else
		{
			return shopItem.getBuyCost();
		}
	}
}
