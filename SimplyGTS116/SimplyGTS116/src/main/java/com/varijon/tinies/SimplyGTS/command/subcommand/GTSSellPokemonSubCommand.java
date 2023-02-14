package com.varijon.tinies.SimplyGTS.command.subcommand;

import java.util.UUID;

import org.apache.commons.lang3.math.NumberUtils;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.varijon.tinies.SimplyGTS.SimplyGTS;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;
import com.varijon.tinies.SimplyGTS.object.GTSListingPokemon;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;
import com.varijon.tinies.SimplyGTS.util.Util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;

public class GTSSellPokemonSubCommand 
{
	public static void handleSubCommand(ServerPlayerEntity player, String[] args)
	{
		if(args.length < 3)
		{
			sendSubCommandUsage(player);
			return;
		}
		if(args.length > 5)
		{
			sendSubCommandUsage(player);
			return;
		}
		if(GTSDataManager.getPlayerListingTotal(player.getUUID()) >= GTSDataManager.getConfig().getMaxPlayerListings())
		{
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "You cannot have more than " + GTSDataManager.getConfig().getMaxPlayerListings() + " listings!"), UUID.randomUUID());
			return;
		}
		if(!NumberUtils.isNumber(args[2]))
		{
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid slot!"), UUID.randomUUID());
			sendSubCommandUsage(player);					
			return;
		}
		int slot = Integer.parseInt(args[2]) - 1;
		if(slot < 0 || slot > 5)
		{
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid slot!"), UUID.randomUUID());
			sendSubCommandUsage(player);						
			return;
		}
		PlayerPartyStorage partySeller = StorageProxy.getParty(player);
		if(partySeller == null)
		{
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "Party is null, contact staff!"), UUID.randomUUID());
			return;
		}
		if(partySeller.countAblePokemon() < 2)
		{
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "You cannot sell your last able Pokemon!"), UUID.randomUUID());
			return;
		}
		
		BattleController bc = BattleRegistry.getBattle(player);
		if(bc != null)
		{
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "You cannot do this in battle!"), UUID.randomUUID());
			return;
		}
		
		Pokemon pokemon = partySeller.get(slot);
		if(pokemon == null)
		{
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "No Pokemon found!"), UUID.randomUUID());
			sendSubCommandUsage(player);
			return;
		}
		if(pokemon.isEgg())
		{
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "You cannot list eggs!"), UUID.randomUUID());		
			return;
		}
		boolean sellAsBreedable = false;
		boolean isUnbreedable = pokemon.isUnbreedable();
		int cost = 100;
		int minimumPrice = Util.calculateMinimumPricePokemon(pokemon, sellAsBreedable);
		if(args.length == 3)
		{
			cost = minimumPrice;
		}
		if(args.length == 4)
		{
			if(!NumberUtils.isNumber(args[3]))
			{
				if(args[3].equals("breedable"))
				{
					if(!isUnbreedable)
					{
						sellAsBreedable = true;
						minimumPrice = Util.calculateMinimumPricePokemon(pokemon, sellAsBreedable);
						cost = minimumPrice;
					}
					else
					{
						player.sendMessage(new StringTextComponent(TextFormatting.RED + "This Pokemon is not breedable!"), UUID.randomUUID());	
						return;
					}
				}
				else
				{
					player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid price!"), UUID.randomUUID());
					sendSubCommandUsage(player);					
					return;					
				}
			}
			else
			{
				cost = Integer.parseInt(args[3]);
			}
		}
		if(args.length == 5)
		{
			if(args[4].equals("breedable"))
			{
				if(!isUnbreedable)
				{
					sellAsBreedable = true;
					minimumPrice = Util.calculateMinimumPricePokemon(pokemon, sellAsBreedable);
				}
				else
				{
					player.sendMessage(new StringTextComponent(TextFormatting.RED + "This Pokemon is not breedable!"), UUID.randomUUID());	
					return;
				}
			}
			else
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid price!"), UUID.randomUUID());
				sendSubCommandUsage(player);					
				return;					
			}
			if(!NumberUtils.isNumber(args[3]))
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid price!"), UUID.randomUUID());
				sendSubCommandUsage(player);					
				return;						
			}
			else
			{
				cost = Integer.parseInt(args[3]);
			}
		}
		if(cost > GTSDataManager.getConfig().getMaxPrice() || cost < 100 || cost < minimumPrice)
		{
			if(sellAsBreedable)
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Minimum price for a breedable Pokemon with these stats " + TextFormatting.GOLD + minimumPrice), UUID.randomUUID());				
			}
			else
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Minimum price for a Pokemon with these stats " + TextFormatting.GOLD + minimumPrice), UUID.randomUUID());				
			}
			sendSubCommandUsage(player);				
			return;
		}
		partySeller.retrieveAll();
		CompoundNBT nbtData = pokemon.writeToNBT(new CompoundNBT());

		UUID listingUUID = UUID.randomUUID();
		GTSListingPokemon listingData = GTSDataManager.addListingPokemonData(new GTSListingPokemon(EnumListingType.Pokemon, EnumListingStatus.Active, System.currentTimeMillis(), System.currentTimeMillis() + Util.parsePeriod(GTSDataManager.getConfig().getListingDuration()), cost, player.getUUID(), listingUUID, nbtData.toString(), sellAsBreedable));
		SimplyGTS.logger.info(player.getName().getString() + " listed " + pokemon.getDisplayName() + " for " + listingData.getListingPrice());
		partySeller.set(slot, null);
		partySeller.setNeedsSaving();
		GTSDataManager.writeListingPokemonData(listingData);

		TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
		chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " You listed your "));
		
		TranslationTextComponent listedPokemonComponent = Util.getHoverText(listingData, player);
		chatTrans.append(listedPokemonComponent);
		listedPokemonComponent.setStyle(listedPokemonComponent.getStyle().withClickEvent(
				new ClickEvent(ClickEvent.Action.RUN_COMMAND, 
		        				"/gts showlisting " + listingUUID)
		        		));
		
		chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + cost + TextFormatting.GREEN + "!" ));
		player.sendMessage(chatTrans, UUID.randomUUID());	
		

		TranslationTextComponent chatTrans2 = new TranslationTextComponent("", new Object());
		chatTrans2.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "] " + TextFormatting.GOLD + player.getName().getString()+ TextFormatting.GREEN + " listed "));
		chatTrans2.append(listedPokemonComponent);
		chatTrans2.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + cost + TextFormatting.GREEN + "!" ));
		for(ServerPlayerEntity targetPlayer : player.getServer().getPlayerList().getPlayers())
		{
			if(!targetPlayer.getName().getString().contains(player.getName().getString()))
			{
				targetPlayer.sendMessage(chatTrans2, UUID.randomUUID());	
			}
		}
		
		return;
	}
	
	static void sendSubCommandUsage(ServerPlayerEntity player)
	{
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell pokemon <slot> [price] [breedable]"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "Leave out price to sell for minimum"), UUID.randomUUID());		
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "Breedable keeps the Pokemon breedable at " + GTSDataManager.getConfig().getBreedablePriceMultiplier() + "x minimum price"  ), UUID.randomUUID());		
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "This also takes " + ((int)(GTSDataManager.getConfig().getBreedablePokemonTax() * 100)) + "% as tax when sold"  ), UUID.randomUUID());	
	}
}
