package com.varijon.tinies.SimplyGTS.object;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class GTSItemPriceHistory 
{
	int numberSold;
	int totalSpent;
	transient ItemStack itemStack;
	String itemNBT;
	
	public GTSItemPriceHistory(int numberSold, int totalSpent, String itemNBT)
	{
		this.numberSold = numberSold;
		this.totalSpent = totalSpent;
		this.itemNBT = itemNBT;
	}
	

	public int getNumberSold() {
		return numberSold;
	}

	public void setNumberSold(int numberSold) {
		this.numberSold = numberSold;
	}

	public int getTotalSpent() {
		return totalSpent;
	}

	public void setTotalSpent(int totalSpent) {
		this.totalSpent = totalSpent;
	}

	public String getItemNBT() {
		return itemNBT;
	}

	public void setItemNBT(String itemNBT) {
		this.itemNBT = itemNBT;
	}
	
	public int getAveragePrice()
	{
		if(totalSpent == 0)
		{
			return -1;
		}
		if(numberSold == 0)
		{
			return -1;
		}
		return totalSpent/numberSold;
	}
	
}
