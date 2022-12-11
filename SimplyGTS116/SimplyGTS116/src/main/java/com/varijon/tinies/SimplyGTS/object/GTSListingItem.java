package com.varijon.tinies.SimplyGTS.object;

import java.util.UUID;

import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class GTSListingItem extends GTSListing
{
	String itemName;
	String itemNBT;
	int itemCount;
	int itemMeta;
	String itemTextComponent;
	transient ItemStack itemStack;
	public GTSListingItem(EnumListingType listingType, EnumListingStatus listingStatus, long listingStart,
			long listingEnd, int listingPrice, UUID listingOwner, UUID listingID,String itemName, int itemMeta, String itemNBT, int itemCount, String itemTextComponent) 
	{
		super(listingType, listingStatus, listingStart, listingEnd, listingPrice, listingOwner, listingID, listingID);
		
		this.itemNBT = itemNBT;
		this.itemName = itemName;
		this.itemMeta = itemMeta;
		this.itemCount = itemCount;
		this.itemTextComponent = itemTextComponent;
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
				if(itemNBT.equals(""))
				{
					this.itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(itemName)));
					this.itemStack.setDamageValue(itemMeta);
					this.itemStack.setCount(this.itemCount);
				}
				else
				{
					this.itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(itemName)));
					this.itemStack.setTag(JsonToNBT.parseTag((itemNBT)));
					this.itemStack.setDamageValue(itemMeta);
					this.itemStack.setCount(this.itemCount);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return itemStack;
	}
	
	public ITextComponent getTextComponent()
	{
		return ITextComponent.Serializer.fromJson(this.itemTextComponent);
	}
	
	public String getItemNBT() {
		return itemNBT;
	}
	public void setItemNBT(String itemNBT) {
		this.itemNBT = itemNBT;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
//	public ItemStack getItemStack() {
//		return itemStack;
//	}
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getItemMeta() {
		return itemMeta;
	}

	public void setItemMeta(int itemMeta) {
		this.itemMeta = itemMeta;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}
	
	
	
}
