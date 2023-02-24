package com.varijon.tinies.SimplyGTS.gui;

import java.util.ArrayList;
import java.util.UUID;

import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.object.GTSListing;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;
import com.varijon.tinies.SimplyGTS.object.GTSListingPokemon;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;
import com.varijon.tinies.SimplyGTS.util.Util;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class GuiPagesManage 
{
	public static GooeyPage getManageMenu(ArrayList<GTSListing> lstGTSListing, ServerPlayerEntity player, int page)
	{
        ChestTemplate.Builder templateBuilder = ChestTemplate.builder(6);
        
        int slotColumnCount = 0;
        int slotRowCount = 0;
        
        for(int x = 0; x < lstGTSListing.size(); x++)
		{
			if(x >= (page-1) * 45 && x < page * 45)
			{
				if(lstGTSListing.get(x) instanceof GTSListingItem)
				{
					GTSListingItem itemListing = (GTSListingItem) lstGTSListing.get(x);
					if(itemListing.getListingStatus() != EnumListingStatus.Active)
					{
						continue;
					}
					GooeyButton itemButton;
					itemButton = GooeyButton.builder()
							.display(ItemListingDisplay.getItemListingDisplay(itemListing.createOrGetItemStack(), player.getUUID(),itemListing, false))
							.title(itemListing.createOrGetItemStack().getHoverName())
							.lore(ITextComponent.class,ItemListingDisplay.getItemListingDisplayList(itemListing.createOrGetItemStack(), player.getUUID(),itemListing, false))
							.onClick((action) -> 
							{
								if(action.getButton().getDisplay() != null)
								{
									if(itemListing.getListingStatus() == EnumListingStatus.Active)
									{
										UIManager.closeUI(action.getPlayer());
										UIManager.openUIForcefully(action.getPlayer(), GuiPagesItems.getCancelConfirmMenuItems((int) itemListing.getListingPrice(), itemListing.getListingID(), page, true));		
									}
									else
									{
				        				UIManager.closeUI(action.getPlayer());
				        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
				        				UIManager.openUIForcefully(action.getPlayer(), getManageMenu(GTSDataManager.getAllListingsPlayer(player.getUUID()), action.getPlayer(), page));
									}
								}
							})
							.build();

					templateBuilder.set(slotRowCount, slotColumnCount, itemButton);
					slotColumnCount++;
					if(slotColumnCount > 8)
					{
						slotRowCount++;
						slotColumnCount = 0;
					}
				}
				if(lstGTSListing.get(x) instanceof GTSListingPokemon)
				{
					GTSListingPokemon pokemonListing = (GTSListingPokemon) lstGTSListing.get(x);
					if(pokemonListing.getListingStatus() != EnumListingStatus.Active)
					{
						continue;
					}
					GooeyButton itemButton;
					itemButton = GooeyButton.builder()
							.display(SpriteItemHelper.getPhoto(pokemonListing.createOrGetPokemonData()))
							.title(Util.getPokemonDisplayName((pokemonListing.createOrGetPokemonData())))
							.lore(PokemonListingDisplay.getPokemonListingDisplayList(pokemonListing.createOrGetPokemonData(),  player.getUUID(),pokemonListing,false))
							.onClick((action) -> 
							{
								if(action.getButton().getDisplay() != null)
								{
									if(pokemonListing.getListingStatus() == EnumListingStatus.Active)
									{
										UIManager.closeUI(action.getPlayer());
										UIManager.openUIForcefully(action.getPlayer(), GuiPagesPokemon.getCancelConfirmMenuPokemon((int) pokemonListing.getListingPrice(), pokemonListing.getListingID(), page, true));		
									}
									else
									{
				        				UIManager.closeUI(action.getPlayer());
				        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
				        				UIManager.openUIForcefully(action.getPlayer(), getManageMenu(GTSDataManager.getAllListingsPlayer(player.getUUID()), action.getPlayer(), page));
									}
								}
							})
							.build();

					templateBuilder.set(slotRowCount, slotColumnCount, itemButton);
					slotColumnCount++;
					if(slotColumnCount > 8)
					{
						slotRowCount++;
						slotColumnCount = 0;
					}
				}
			}
		}
        GooeyButton backPageButton = GooeyButton.builder()
                .display(new ItemStack(Items.ARROW))
                .title(TextFormatting.GOLD + "Click for page " + TextFormatting.YELLOW + (page - 1))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(),getManageMenu(GTSDataManager.getAllListingsPlayer(player.getUUID()), action.getPlayer(), page-1));
        		})
                .build();
        
        GooeyButton forwardPageButton = GooeyButton.builder()
                .display(new ItemStack(Items.ARROW))
                .title(TextFormatting.GOLD + "Click for page " + TextFormatting.YELLOW + (page + 1))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getManageMenu(GTSDataManager.getAllListingsPlayer(player.getUUID()), action.getPlayer(), page+1));
        		})
                .build();
        
        ItemStack historyDisplay = new ItemStack(PixelmonItems.silver_hourglass);
        historyDisplay.getOrCreateTag().putString("tooltip", "");
    	
        GooeyButton switchToHistoryButton = GooeyButton.builder()
                .display(historyDisplay)
                .title(TextFormatting.GOLD + "Click to view history")
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), GuiPagesHistory.getHistoryMenu(GTSDataManager.getAllListingHistoryPlayer(player.getUUID()), action.getPlayer(), 1, null));
        		})
                .build();
        if(page != 1)
        {
	        templateBuilder
	        	.set(5, 0, backPageButton)
	        	.set(5, 8, forwardPageButton);        	
        }
        else
        {
            templateBuilder
            	.set(5, 8, forwardPageButton);
        }
        templateBuilder.set(5, 4, switchToHistoryButton);
        
		ChestTemplate template = templateBuilder
                .build();

        
        GooeyPage pageBuilder = GooeyPage.builder()
                .title(TextFormatting.DARK_BLUE + "Manage your GTS Listings")
                .template(template)
                .build();

        return pageBuilder;
	}
}
