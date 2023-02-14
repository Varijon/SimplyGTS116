package com.varijon.tinies.SimplyGTS.handler;

import java.util.ArrayList;
import java.util.UUID;

import com.pixelmonmod.pixelmon.api.storage.PartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.varijon.tinies.SimplyGTS.SimplyGTS;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;
import com.varijon.tinies.SimplyGTS.object.GTSListingPokemon;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;
import com.varijon.tinies.SimplyGTS.util.Util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class GTSTicker 
{

	int tickCount = 0;
	MinecraftServer server;
	ArrayList<GTSListingPokemon> lstPokemonListingRemoval;
	ArrayList<GTSListingItem> lstItemListingRemoval;
	
	public GTSTicker() 
	{
		lstPokemonListingRemoval = new ArrayList<>();
		lstItemListingRemoval = new ArrayList<>();
	}
	
	@SubscribeEvent
	public void onWorldTick (TickEvent.WorldTickEvent event)
	{
		try
		{
			if(event.phase != TickEvent.Phase.END)
			{
				return;
			}
			if(event.world.dimension() != World.OVERWORLD)
			{
				return;
			}
			if(tickCount > 20)
			{
				server = event.world.getServer();
				for(GTSListingPokemon gtsListingPokemon : GTSDataManager.getAllGTSPokemonListings())
				{
					if(gtsListingPokemon.getListingStatus() == EnumListingStatus.Sold)
					{
						ServerPlayerEntity targetPlayer = server.getPlayerList().getPlayer(gtsListingPokemon.getListingOwner());
						if(targetPlayer != null)
						{
							String playerName = UsernameCache.getLastKnownUsername(gtsListingPokemon.getListingBuyer());

							if(playerName == null)
							{
								playerName = "Someone";
							}
							
							TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
        					chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "] " + TextFormatting.GOLD + playerName + TextFormatting.GREEN + " bought your "));
        					chatTrans.append(Util.getHoverText(gtsListingPokemon, targetPlayer));
        					if(gtsListingPokemon.isSoldAsBreedable())
        					{
                				double combinedTax = GTSDataManager.getConfig().getGeneralTax() + GTSDataManager.getConfig().getBreedablePokemonTax();
            					chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + (gtsListingPokemon.getListingPrice() - ((int)(gtsListingPokemon.getListingPrice() * combinedTax))) + TextFormatting.GREEN + "!" ));        						
        					}
        					else
        					{
            					chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + (gtsListingPokemon.getListingPrice() - ((int)(gtsListingPokemon.getListingPrice() * GTSDataManager.getConfig().getGeneralTax()))) + TextFormatting.GREEN + "!" ));        						
        					}
        					targetPlayer.sendMessage(chatTrans, UUID.randomUUID());	
							lstPokemonListingRemoval.add(gtsListingPokemon);
							continue;
						}
					}
					if(System.currentTimeMillis() > gtsListingPokemon.getListingEnd() || gtsListingPokemon.getListingStatus() == EnumListingStatus.Expired && gtsListingPokemon.getListingStatus() != EnumListingStatus.Sold)
					{
						gtsListingPokemon.setListingStatus(EnumListingStatus.Expired);
						ServerPlayerEntity targetPlayer = server.getPlayerList().getPlayer(gtsListingPokemon.getListingOwner());
						if(targetPlayer != null)
						{
	        				PartyStorage partyOwner = StorageProxy.getParty(targetPlayer);
	        				if(partyOwner == null)
	        				{
	        					continue;
	        				}
							TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
							chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " Your listing for "));
							chatTrans.append(Util.getHoverText(gtsListingPokemon, targetPlayer));
							chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " has expired!" ));
							targetPlayer.sendMessage(chatTrans, UUID.randomUUID());	
	        				        				
							partyOwner.add(gtsListingPokemon.createOrGetPokemonData());
							partyOwner.setNeedsSaving();
							
	        				SimplyGTS.logger.info(targetPlayer.getName() + " had their listing for " + gtsListingPokemon.createOrGetPokemonData().getDisplayName()  + " for " + gtsListingPokemon.getListingPrice() + " expire");     

							
							lstPokemonListingRemoval.add(gtsListingPokemon);
						}
					}
				}
				for(GTSListingPokemon gtsListingPokemon : lstPokemonListingRemoval)
				{
					GTSDataManager.removeListingPokemonData(gtsListingPokemon);
				}
				lstPokemonListingRemoval.clear();
				for(GTSListingItem gtsListingItem : GTSDataManager.getAllGTSItemsListings())
				{
					if(gtsListingItem.getListingStatus() == EnumListingStatus.Sold)
					{
						ServerPlayerEntity targetPlayer = server.getPlayerList().getPlayer(gtsListingItem.getListingOwner());
						if(targetPlayer != null)
						{
							String playerName = UsernameCache.getLastKnownUsername(gtsListingItem.getListingBuyer());

							if(playerName == null)
							{
								playerName = "Someone";
							}
							TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
        					chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "] " + TextFormatting.GOLD + playerName + TextFormatting.GREEN + " bought your "+ TextFormatting.WHITE + gtsListingItem.getItemCount() + "x "));
            				chatTrans.append(gtsListingItem.getTextComponent());
        					chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + (gtsListingItem.getListingPrice() - ((int)(gtsListingItem.getListingPrice() * GTSDataManager.getConfig().getGeneralTax()))) + TextFormatting.GREEN + "!" ));
        					targetPlayer.sendMessage(chatTrans, UUID.randomUUID());	
							lstItemListingRemoval.add(gtsListingItem);
							continue;
						}
					}
					if(System.currentTimeMillis() > gtsListingItem.getListingEnd() || gtsListingItem.getListingStatus() == EnumListingStatus.Expired && gtsListingItem.getListingStatus() != EnumListingStatus.Sold)
					{
						gtsListingItem.setListingStatus(EnumListingStatus.Expired);
						ServerPlayerEntity targetPlayer = server.getPlayerList().getPlayer(gtsListingItem.getListingOwner());
						if(targetPlayer != null)
						{
							if(targetPlayer.inventory.add(gtsListingItem.createOrGetItemStack()))
	            			{
								TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
								chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " Your listing for " + TextFormatting.WHITE + gtsListingItem.getItemCount() + "x "));
	            				chatTrans.append(gtsListingItem.getTextComponent());
								chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " has expired!" ));
								targetPlayer.sendMessage(chatTrans, UUID.randomUUID());
								
		        				SimplyGTS.logger.info(targetPlayer.getName() + " had their listing for " + gtsListingItem.getItemCount() + "x " + gtsListingItem.getItemName()  + " for " + gtsListingItem.getListingPrice() + " expire");     
								
								lstItemListingRemoval.add(gtsListingItem);
	            			} 
						}
					}
				}
				for(GTSListingItem gtsListingItem : lstItemListingRemoval)
				{
					GTSDataManager.removeListingItemsData(gtsListingItem);
				}
				lstItemListingRemoval.clear();
				tickCount = 0;
			}
			tickCount++;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
