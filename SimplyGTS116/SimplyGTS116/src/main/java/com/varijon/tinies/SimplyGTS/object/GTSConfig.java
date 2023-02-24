package com.varijon.tinies.SimplyGTS.object;

import java.util.ArrayList;

public class GTSConfig 
{
	int minPrice1IV;
	int minPrice2IV;
	int minPrice3IV;
	int minPrice4IV;
	int minPrice5IV;
	int minPrice6IV;
	int minHAPrice;
	int maxPrice;
	double breedablePriceMultiplier;
	double breedablePokemonTax;
	double generalTax;
	String listingDuration;
	int maxPlayerListings;
	int daysToKeepHistory;
	ArrayList<ItemConfigMinPrice> lstMinItemPrices;
	
	public GTSConfig(int minPrice1IV, int minPrice2IV, int minPrice3IV, int minPrice4IV, int minPrice5IV,
			int minPrice6IV, int maxPrice, int minHAPrice, String listingDuration, int maxPlayerListings, int daysToKeepHistory, double breedablePriceMultiplier, double breedablePokemonTax, double generalTax,
			ArrayList<ItemConfigMinPrice> lstMinItemPrices) {
		super();
		this.minPrice1IV = minPrice1IV;
		this.minPrice2IV = minPrice2IV;
		this.minPrice3IV = minPrice3IV;
		this.minPrice4IV = minPrice4IV;
		this.minPrice5IV = minPrice5IV;
		this.minPrice6IV = minPrice6IV;
		this.minHAPrice = minHAPrice;
		this.maxPrice = maxPrice;
		this.breedablePriceMultiplier = breedablePriceMultiplier;
		this.breedablePokemonTax = breedablePokemonTax;
		this.generalTax = generalTax;
		this.listingDuration = listingDuration;
		this.maxPlayerListings = maxPlayerListings;
		this.daysToKeepHistory = daysToKeepHistory;
		this.lstMinItemPrices = lstMinItemPrices;
	}

	public int getMinPrice1IV() {
		return minPrice1IV;
	}

	public void setMinPrice1IV(int minPrice1IV) {
		this.minPrice1IV = minPrice1IV;
	}

	public int getMinPrice2IV() {
		return minPrice2IV;
	}

	public void setMinPrice2IV(int minPrice2IV) {
		this.minPrice2IV = minPrice2IV;
	}

	public int getMinPrice3IV() {
		return minPrice3IV;
	}

	public void setMinPrice3IV(int minPrice3IV) {
		this.minPrice3IV = minPrice3IV;
	}

	public int getMinPrice4IV() {
		return minPrice4IV;
	}

	public void setMinPrice4IV(int minPrice4IV) {
		this.minPrice4IV = minPrice4IV;
	}

	public int getMinPrice5IV() {
		return minPrice5IV;
	}

	public void setMinPrice5IV(int minPrice5IV) {
		this.minPrice5IV = minPrice5IV;
	}

	public int getMinPrice6IV() {
		return minPrice6IV;
	}

	public void setMinPrice6IV(int minPrice6IV) {
		this.minPrice6IV = minPrice6IV;
	}
	
	public int getMinHAPrice() {
		return minHAPrice;
	}

	public void setMinHAPrice(int minHAPrice) {
		this.minHAPrice = minHAPrice;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}

	public String getListingDuration() {
		return listingDuration;
	}

	public void setListingDuration(String listingDuration) {
		this.listingDuration = listingDuration;
	}

	public int getMaxPlayerListings() {
		return maxPlayerListings;
	}

	public void setMaxPlayerListings(int maxPlayerListings) {
		this.maxPlayerListings = maxPlayerListings;
	}

	public ArrayList<ItemConfigMinPrice> getLstMinItemPrices() {
		return lstMinItemPrices;
	}

	public void setLstMinItemPrices(ArrayList<ItemConfigMinPrice> lstMinItemPrices) {
		this.lstMinItemPrices = lstMinItemPrices;
	}

	public double getBreedablePriceMultiplier() {
		return breedablePriceMultiplier;
	}

	public void setBreedablePriceMultiplier(double breedablePriceMultiplier) {
		this.breedablePriceMultiplier = breedablePriceMultiplier;
	}

	public double getBreedablePokemonTax() {
		return breedablePokemonTax;
	}

	public void setBreedablePokemonTax(double breedablePokemonTax) {
		this.breedablePokemonTax = breedablePokemonTax;
	}

	public double getGeneralTax() {
		return generalTax;
	}

	public void setGeneralTax(double generalTax) {
		this.generalTax = generalTax;
	}

	public int getDaysToKeepHistory() {
		return daysToKeepHistory;
	}

	public void setDaysToKeepHistory(int daysToKeepHistory) {
		this.daysToKeepHistory = daysToKeepHistory;
	}
	
	
}
