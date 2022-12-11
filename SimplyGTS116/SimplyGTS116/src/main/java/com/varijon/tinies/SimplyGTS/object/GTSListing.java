package com.varijon.tinies.SimplyGTS.object;

import java.util.UUID;

import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;

public abstract class GTSListing 
{
	EnumListingType listingType;
	EnumListingStatus listingStatus;
	long listingStart;
	long listingEnd;
	int listingPrice;
	UUID listingOwner;
	UUID listingID;
	UUID listingBuyer;
	
	public GTSListing(EnumListingType listingType, EnumListingStatus listingStatus, long listingStart, long listingEnd,
			int listingPrice, UUID listingOwner, UUID listingBuyer, UUID listingID) 
	{
		super();
		this.listingType = listingType;
		this.listingStatus = listingStatus;
		this.listingStart = listingStart;
		this.listingEnd = listingEnd;
		this.listingPrice = listingPrice;
		this.listingOwner = listingOwner;
		this.listingID = listingID;
		this.listingBuyer = listingBuyer;
	}

	public EnumListingType getListingType() {
		return listingType;
	}

	public void setListingType(EnumListingType listingType) {
		this.listingType = listingType;
	}

	public EnumListingStatus getListingStatus() {
		return listingStatus;
	}

	public void setListingStatus(EnumListingStatus listingStatus) {
		this.listingStatus = listingStatus;
	}

	public long getListingStart() {
		return listingStart;
	}

	public void setListingStart(long listingStart) {
		this.listingStart = listingStart;
	}

	public long getListingEnd() {
		return listingEnd;
	}

	public void setListingEnd(long listingEnd) {
		this.listingEnd = listingEnd;
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

	public long getListingTimeRemaining()
	{
		long returnedTime = listingEnd - System.currentTimeMillis();
		if(returnedTime < 1)
		{
			returnedTime = 0;
		}
		return returnedTime;
	}
	
}
