package com.varijon.tinies.SimplyGTS.command.subcommand;

import java.util.UUID;

import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.gui.GuiPagesItems;
import com.varijon.tinies.SimplyGTS.gui.GuiPagesPokemon;
import com.varijon.tinies.SimplyGTS.object.GTSListing;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;
import com.varijon.tinies.SimplyGTS.object.GTSListingPokemon;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;

import ca.landonjw.gooeylibs2.api.UIManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class GTSShowListingSubcommand 
{
	public static void handleSubCommand(ServerPlayerEntity player, String[] args)
	{
		if(args.length == 2)
		{
			UUID listingID = null;
			try 
			{
				listingID = UUID.fromString(args[1]);
			}
			catch(IllegalArgumentException ex)
			{
				
			}
			if(listingID == null)
			{
				return;
			}
			GTSListing gtsListing = GTSDataManager.getListingDataEither(listingID);
			if(gtsListing == null)
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Listing sold or cancelled!"), UUID.randomUUID());
				return;
			}
			if(gtsListing.getListingStatus() == EnumListingStatus.Sold)
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Listing sold or cancelled!"), UUID.randomUUID());
				return;
			}
			if(gtsListing.getListingStatus() == EnumListingStatus.Expired)
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Listing sold or cancelled!"), UUID.randomUUID());
				return;
			}
			boolean isListingOwner = false;
			if(gtsListing.getListingOwner().toString().equals(player.getUUID().toString()))
			{
				isListingOwner = true;
			}
			if(gtsListing instanceof GTSListingPokemon)
			{
				GTSListingPokemon gtsListingPokemon = (GTSListingPokemon) gtsListing;
				UIManager.closeUI(player);
				if(isListingOwner)
				{
					UIManager.openUIForcefully(player, GuiPagesPokemon.getCancelConfirmMenuPokemon((int) gtsListingPokemon.getListingPrice(), gtsListingPokemon.getListingID(), 1, true));						
				}
				else
				{
					UIManager.openUIForcefully(player, GuiPagesPokemon.getBuyConfirmMenuPokemon((int) gtsListingPokemon.getListingPrice(), gtsListingPokemon.getListingID(), 1, player));							
				}
				return;
			}
			if(gtsListing instanceof GTSListingItem)
			{
				GTSListingItem gtsListingItem = (GTSListingItem) gtsListing;
				UIManager.closeUI(player);
				if(isListingOwner)
				{
					UIManager.openUIForcefully(player, GuiPagesItems.getCancelConfirmMenuItems((int) gtsListingItem.getListingPrice(), gtsListingItem.getListingID(), 1, true));						
				}
				else
				{										
					UIManager.openUIForcefully(player, GuiPagesItems.getBuyConfirmMenuItems((int) gtsListingItem.getListingPrice(), gtsListingItem.getListingID(), 1,player));													
				}
			}
			return;						
		}
	}
}
