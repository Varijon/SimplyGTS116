package com.varijon.tinies.SimplyGTS.object;

import java.util.UUID;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class GTSListingItem extends GTSListing
{
	String itemAsNBT;
	transient ItemStack itemStack;
	public GTSListingItem(EnumListingType listingType, EnumListingStatus listingStatus, long listingStart,
			long listingEnd, int listingPrice, UUID listingOwner, UUID listingID, String itemAsNBT) 
	{
		super(listingType, listingStatus, listingStart, listingEnd, listingPrice, listingOwner, listingID, listingID);
		
		this.itemAsNBT = itemAsNBT;
	}
	
	public ItemStack createOrGetItemStack()
	{
		if(itemStack != null)
		{
			return itemStack;
		}
		else
		{
			try {
				itemStack = ItemStack.of(JsonToNBT.parseTag(itemAsNBT));
			} catch (CommandSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return itemStack;
	}
	
	public int getItemCount() 
	{
		return createOrGetItemStack().getCount();
	}
	public void setItemCount(int itemCount) {
		createOrGetItemStack().setCount(itemCount);
	}

	public String getItemName()
	{
		return createOrGetItemStack().getItem().getRegistryName().toString();
	}
	
	public String getItemHoverName()
	{
		return this.createOrGetItemStack().getHoverName().getString();
	}
	
}
