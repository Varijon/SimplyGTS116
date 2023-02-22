package com.varijon.tinies.SimplyGTS.object;

import java.util.ArrayList;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;

public class GTSPriceHistoryList 
{
	ArrayList<GTSItemPriceHistory> lstItemHistories;
	String itemName;
	
	public GTSPriceHistoryList(ArrayList<GTSItemPriceHistory> lstItemHistories, String itemName)
	{
		this.lstItemHistories = lstItemHistories;
		this.itemName = itemName;
	}

	public ArrayList<GTSItemPriceHistory> getLstItemHistories() 
	{
		if(lstItemHistories == null)
		{
			lstItemHistories = new ArrayList<GTSItemPriceHistory>();
		}
		return lstItemHistories;
	}

	public void setLstItemHistories(ArrayList<GTSItemPriceHistory> lstItemHistories) {
		this.lstItemHistories = lstItemHistories;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public GTSItemPriceHistory getItemPriceHistory(ItemStack item)
	{
		for(GTSItemPriceHistory priceHistory : lstItemHistories)
		{
			if(item.getTag() == null)
			{
				return priceHistory;
			}
			else
			{
				try {
					if(item.getTag().equals(JsonToNBT.parseTag(priceHistory.itemNBT)))
					{
						return priceHistory;
					}
				} catch (CommandSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public GTSItemPriceHistory addPriceHistory(GTSItemPriceHistory gtsItemPriceHistory)
	{
		lstItemHistories.add(gtsItemPriceHistory);
		return gtsItemPriceHistory;
	}
}
