package com.varijon.tinies.SimplyGTS.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;
import com.varijon.tinies.SimplyGTS.object.GTSListing;
import com.varijon.tinies.SimplyGTS.object.GTSListingHistory;
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
import net.minecraftforge.common.UsernameCache;

public class GuiPagesHistory 
{
	public static GooeyPage getHistoryMenu(ArrayList<GTSListingHistory> lstListingHistory, ServerPlayerEntity player, int page, UUID otherPlayer)
	{
        ChestTemplate.Builder templateBuilder = ChestTemplate.builder(6);
        
        int slotColumnCount = 0;
        int slotRowCount = 0;
        
        List<GTSListingHistory> lstSortedListingHistory = lstListingHistory.stream()
	      		  .sorted(Comparator.comparing(GTSListingHistory::getListingEnd).reversed())
	      		  .collect(Collectors.toList());
        
        for(int x = 0; x < lstSortedListingHistory.size(); x++)
		{
			if(x >= (page-1) * 45 && x < page * 45)
			{
				GTSListingHistory listingHistory = lstSortedListingHistory.get(x);
				GooeyButton itemButton;
				itemButton = GooeyButton.builder()
						.display(listingHistory.createOrGetDisplayItemStack())
						.title(listingHistory.createOrGetDisplayItemStack().getHoverName())
						.lore(ITextComponent.class, Util.replaceWithHistoryLines(Util.getItemStackLore(listingHistory.createOrGetDisplayItemStack()),listingHistory))
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
        GooeyButton backPageButton = GooeyButton.builder()
                .display(new ItemStack(Items.ARROW))
                .title(TextFormatting.GOLD + "Click for page " + TextFormatting.YELLOW + (page - 1))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(),getHistoryMenu(GTSDataManager.getAllListingHistoryPlayer(player.getUUID()), action.getPlayer(), page-1, otherPlayer));
        		})
                .build();
        
        GooeyButton forwardPageButton = GooeyButton.builder()
                .display(new ItemStack(Items.ARROW))
                .title(TextFormatting.GOLD + "Click for page " + TextFormatting.YELLOW + (page + 1))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getHistoryMenu(GTSDataManager.getAllListingHistoryPlayer(player.getUUID()), action.getPlayer(), page+1, otherPlayer));
        		})
                .build();
        ItemStack manageDisplay = new ItemStack(PixelmonItems.weakness_policy);
        manageDisplay.getOrCreateTag().putString("tooltip", "");
    	
        GooeyButton switchToManageButton = GooeyButton.builder()
                .display(manageDisplay)
                .title(TextFormatting.GOLD + "Click to manage listings")
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), GuiPagesManage.getManageMenu(GTSDataManager.getAllListingsPlayer(player.getUUID()), action.getPlayer(), 1));
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
        if(otherPlayer == null)
        {
            templateBuilder.set(5, 4, switchToManageButton);
        }
        
        
		ChestTemplate template = templateBuilder
                .build();
		
		String otherPlayerName = null;
		
		if(otherPlayer != null)
		{
			otherPlayerName = UsernameCache.getLastKnownUsername(otherPlayer);

			if(otherPlayerName == null)
			{
				otherPlayerName = "Someone";
			}
		}
        
        GooeyPage pageBuilder = GooeyPage.builder()
                .title(TextFormatting.DARK_BLUE + "GTS Sell History " + (otherPlayer == null ? "" : TextFormatting.DARK_GRAY + otherPlayerName))
                .template(template)
                .build();

        return pageBuilder;
	}
}
