package com.varijon.tinies.SimplyGTS.object;

import java.util.ArrayList;
import java.util.UUID;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;
import com.varijon.tinies.SimplyGTS.util.Util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class GTSListingHistory 
{
	int listingPrice;
	UUID listingOwner;
	UUID listingID;
	UUID listingBuyer;
	long listingEnd;
	String title;
	transient ItemStack displayItemStack;
	String displayItemNBT;

	public GTSListingHistory(GTSListing listing, long listingEnd, String displayItemNBT) 
	{
		this.listingPrice = listing.getListingPrice();
		this.listingOwner = listing.getListingOwner();
		this.listingID = listing.getListingID();
		this.listingBuyer = listing.getListingBuyer();
		this.listingEnd = listingEnd;
		this.displayItemNBT = displayItemNBT;
	}
	
	public ItemStack createOrGetDisplayItemStack()
	{
		if(displayItemStack != null)
		{
			return displayItemStack;
		}
		else
		{
			try {
				displayItemStack = ItemStack.of(JsonToNBT.parseTag(displayItemNBT));
			} catch (CommandSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return displayItemStack;
	}

	public int getListingPrice() {
		return listingPrice;
	}

	public void setListingPrice(int listingPrice) {
		this.listingPrice = listingPrice;
	}

	public UUID getListingOwner() {
		return listingOwner;
	}

	public void setListingOwner(UUID listingOwner) {
		this.listingOwner = listingOwner;
	}

	public UUID getListingID() {
		return listingID;
	}

	public void setListingID(UUID listingID) {
		this.listingID = listingID;
	}

	public UUID getListingBuyer() {
		return listingBuyer;
	}

	public void setListingBuyer(UUID listingBuyer) {
		this.listingBuyer = listingBuyer;
	}

	public long getListingEnd() {
		return listingEnd;
	}

	public void setListingEnd(long listingEnd) {
		this.listingEnd = listingEnd;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<ITextComponent> getLore() {
		return Util.getItemStackLore(createOrGetDisplayItemStack());
	}
}
