package com.varijon.tinies.SimplyGTS.storage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.object.GTSConfig;
import com.varijon.tinies.SimplyGTS.object.GTSItemPriceHistory;
import com.varijon.tinies.SimplyGTS.object.GTSListing;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;
import com.varijon.tinies.SimplyGTS.object.GTSListingPokemon;
import com.varijon.tinies.SimplyGTS.object.GTSPriceHistoryList;
import com.varijon.tinies.SimplyGTS.object.ItemConfigMinPrice;

public class GTSDataManager 
{
	static ArrayList<GTSListingPokemon> lstListingDataPokemon = new ArrayList<GTSListingPokemon>();
	static ArrayList<GTSListingItem> lstListingDataItems = new ArrayList<GTSListingItem>();
	static ArrayList<GTSPriceHistoryList> lstPriceHistory = new ArrayList<GTSPriceHistoryList>();
	static GTSConfig gtsConfig;
	
	public static boolean loadStorage()
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/SimplyGTS/listings/pokemon";
        String source2 = basefolder + "/config/SimplyGTS/listings/items";
        String source3 = basefolder + "/config/SimplyGTS/pricehistory";
		try
		{
			Gson gson = new Gson();
			
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			File dir2 = new File(source2);
			if(!dir2.exists())
			{
				dir2.mkdirs();
			}
			File dir3 = new File(source3);
			if(!dir3.exists())
			{
				dir3.mkdirs();
			}
			
			lstListingDataPokemon.clear();
			lstListingDataItems.clear();
			lstPriceHistory.clear();
			
			for(File file : dir.listFiles())
			{
				FileReader reader = new FileReader(file);
				
				GTSListingPokemon listingData = gson.fromJson(reader, GTSListingPokemon.class);
								
				lstListingDataPokemon.add(listingData);
				reader.close();
			}
			for(File file : dir2.listFiles())
			{
				FileReader reader = new FileReader(file);
				
				GTSListingItem listingData = gson.fromJson(reader, GTSListingItem.class);
								
				lstListingDataItems.add(listingData);
				reader.close();
			}
			for(File file : dir3.listFiles())
			{
				FileReader reader = new FileReader(file);
				
				GTSPriceHistoryList historyData = gson.fromJson(reader, GTSPriceHistoryList.class);
								
				lstPriceHistory.add(historyData);
				reader.close();
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public static boolean loadConfiguration()
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/SimplyGTS";
		try
		{
			Gson gson = new Gson();
			
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			
			writeConfigFile();
			
			for(File file : dir.listFiles())
			{
				if(!file.getName().equals("config.json"))
				{
					continue;
				}
				FileReader reader = new FileReader(file);
				
				GTSConfig gtsConfig = gson.fromJson(reader, GTSConfig.class);
								
				GTSDataManager.gtsConfig = gtsConfig;
				reader.close();
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public static void writeConfigFile()
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/SimplyGTS";
		
		try
		{
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			File config = new File(source + "/config.json");
			if(!config.exists())
			{
				ArrayList<ItemConfigMinPrice> lstExampleMinPrice = new ArrayList<>();
				lstExampleMinPrice.add(new ItemConfigMinPrice(PixelmonItems.destiny_knot.getRegistryName().toString(), 50000, ""));
				lstExampleMinPrice.add(new ItemConfigMinPrice(PixelmonItems.ever_stone.getRegistryName().toString(), 50000, ""));
				lstExampleMinPrice.add(new ItemConfigMinPrice(PixelmonItems.mint_adamant.getRegistryName().toString(), 50000, ""));
				
				GTSConfig gtsConfig = new GTSConfig(0, 0, 30000, 40000, 50000, 50000, 1000000, 50000, "3d", 8, 2.0, 0.20, 0.05,lstExampleMinPrice);
		
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
					
				FileWriter writer = new FileWriter(source + "/config.json");
				gson.toJson(gtsConfig, writer);
				writer.close();
			}
		}
			
		catch (Exception ex) 
		{
		    ex.printStackTrace();
		}
	}
	
	public static void writeListingPokemonData(GTSListingPokemon listingData)
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/SimplyGTS/listings/pokemon";
		
		try
		{
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
					
			FileWriter writer = new FileWriter(source + "/" + listingData.getListingID() + ".json");
			gson.toJson(listingData, writer);
			writer.close();
		}
			
		catch (Exception ex) 
		{
		    ex.printStackTrace();
		}
	}
	
	public static void writeListingItemsData(GTSListingItem listingData)
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/SimplyGTS/listings/items";
		
		try
		{
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
					
			FileWriter writer = new FileWriter(source + "/" + listingData.getListingID() + ".json");
			gson.toJson(listingData, writer);
			writer.close();
		}
			
		catch (Exception ex) 
		{
		    ex.printStackTrace();
		}
	}
	
	public static void writeHistoryData(GTSPriceHistoryList historyData)
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/SimplyGTS/pricehistory";
		
		try
		{
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
					
			FileWriter writer = new FileWriter(source + "/" + historyData.getItemName().replace(':', '_') + ".json");
			gson.toJson(historyData, writer);
			writer.close();
		}
			
		catch (Exception ex) 
		{
		    ex.printStackTrace();
		}
	}
	
	static void deleteGTSPokemonListing(GTSListingPokemon listingData)
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/SimplyGTS/listings/pokemon";
		
		try
		{
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			
			File deleteFile = new File(source + "/" + listingData.getListingID() + ".json");
			deleteFile.delete();
		}
			
		catch (Exception ex) 
		{
		    ex.printStackTrace();
		}
	}
	
	static void deleteGTSItemsListing(GTSListingItem listingData)
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/SimplyGTS/listings/items";
		
		try
		{
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			
			File deleteFile = new File(source + "/" + listingData.getListingID() + ".json");
			deleteFile.delete();
		}
			
		catch (Exception ex) 
		{
		    ex.printStackTrace();
		}
	}
	
	public static GTSListingPokemon getListingPokemonData(UUID listingID)
	{
		for(GTSListingPokemon listingData : lstListingDataPokemon)
		{
			if(listingData.getListingID().equals(listingID))
			{
				return listingData;
			}
		}
		return null;
	}
	
	public static GTSListingItem getListingItemsData(UUID listingID)
	{
		for(GTSListingItem listingData : lstListingDataItems)
		{
			if(listingData.getListingID().equals(listingID))
			{
				return listingData;
			}
		}
		return null;
	}
	
	public static GTSListing getListingDataEither(UUID listingID)
	{
		for(GTSListingItem listingData : lstListingDataItems)
		{
			if(listingData.getListingID().equals(listingID))
			{
				return listingData;
			}
		}
		for(GTSListingPokemon listingData : lstListingDataPokemon)
		{
			if(listingData.getListingID().equals(listingID))
			{
				return listingData;
			}
		}
		return null;
	}
	
	public static GTSPriceHistoryList getPriceHistoryList(String itemName)
	{
		for(GTSPriceHistoryList priceHistoryList : lstPriceHistory)
		{
			if(priceHistoryList.getItemName().equals(itemName))
			{
				return priceHistoryList;
			}
		}
		return null;
	}
	
	public static void removeListingPokemonData(GTSListingPokemon listingData)
	{
		deleteGTSPokemonListing(listingData);
		lstListingDataPokemon.remove(listingData);
	}
	public static void removeListingItemsData(GTSListingItem listingData)
	{
		deleteGTSItemsListing(listingData);
		lstListingDataItems.remove(listingData);
	}
	
	public static GTSListingPokemon addListingPokemonData(GTSListingPokemon listingData)
	{
		lstListingDataPokemon.add(listingData);
		return listingData;
	}
	public static GTSListingItem addListingItemsData(GTSListingItem listingData)
	{
		lstListingDataItems.add(listingData);
		return listingData;
	}
	
	public static GTSPriceHistoryList addHistoryData(GTSPriceHistoryList historyList)
	{
		lstPriceHistory.add(historyList);
		return historyList;
	}
	
	public static int getPlayerListingTotal(UUID player)
	{
		int listings = 0;
		for(GTSListingPokemon listingData : lstListingDataPokemon)
		{
			if(listingData.getListingOwner().equals(player))
			{
				listings++;
			}
		}
		for(GTSListingItem listingData : lstListingDataItems)
		{
			if(listingData.getListingOwner().equals(player))
			{
				listings++;
			}
		}
		return listings;
	}
	
	public static ArrayList<GTSListing> getAllListingsPlayer(UUID player)
	{
		ArrayList<GTSListing> lstAllListingsPlayer = new ArrayList<>();
		for(GTSListingPokemon listingData : lstListingDataPokemon)
		{
			if(listingData.getListingOwner().equals(player))
			{
				lstAllListingsPlayer.add(listingData);
			}
		}
		for(GTSListingItem listingData : lstListingDataItems)
		{
			if(listingData.getListingOwner().equals(player))
			{
				lstAllListingsPlayer.add(listingData);
			}
		}
		return lstAllListingsPlayer;
	}
//	public static void saveChangesToFile()
//	{
//		String basefolder = new File("").getAbsolutePath();
//        String source = basefolder + "/config/CatchEventReport";
//		
//		try
//		{
//			File dir = new File(source);
//			if(!dir.exists())
//			{
//				dir.mkdirs();
//			}
//			if(dir.listFiles().length == 0)
//			{
//				ArrayList<EventPokemon> lstEventPokemon = new ArrayList<EventPokemon>();
//				lstEventPokemon.add(new EventPokemon(EnumSpecies.Salandit, "winter", 10));
//				lstEventPokemon.add(new EventPokemon(EnumSpecies.Cutiefly, "winter", 20));
//				EventConfig event = new EventConfig("Example", "exampleTag", "Welcome to the Example event", lstEventPokemon);
//		
//				Gson gson = new GsonBuilder().setPrettyPrinting().create();
//					
//				FileWriter writer = new FileWriter(source + "/Example.json");
//				gson.toJson(event, writer);
//				writer.close();
//			}
//		}
//			
//		catch (Exception ex) 
//		{
//		    ex.printStackTrace();
//		}
//	}
	public static GTSConfig getConfig()
	{
		return gtsConfig;
	}
	
	public static ArrayList<GTSListingPokemon> getAllGTSPokemonListings()
	{
		return lstListingDataPokemon;
	}

	public static ArrayList<GTSListingItem> getAllGTSItemsListings()
	{
		return lstListingDataItems;
	}
	public static ArrayList<GTSListingPokemon> getGTSPokemonListings()
	{
		ArrayList<GTSListingPokemon> lstActive = new ArrayList<>();
		for(GTSListingPokemon gtsListingPokemon : lstListingDataPokemon)
		{
			if(gtsListingPokemon.getListingStatus() == EnumListingStatus.Active)
			{
				lstActive.add(gtsListingPokemon);				
			}
		}
		return lstActive;
	}
	public static ArrayList<GTSListingItem> getGTSItemsListings()
	{
		ArrayList<GTSListingItem> lstActive = new ArrayList<>();
		for(GTSListingItem gtsListingItem : lstListingDataItems)
		{
			if(gtsListingItem.getListingStatus() == EnumListingStatus.Active)
			{
				lstActive.add(gtsListingItem);				
			}
		}
		return lstActive;
	}
}
