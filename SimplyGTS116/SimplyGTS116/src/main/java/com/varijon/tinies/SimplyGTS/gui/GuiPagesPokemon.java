package com.varijon.tinies.SimplyGTS.gui;

import java.util.ArrayList;
import java.util.UUID;

import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.varijon.tinies.SimplyGTS.SimplyGTS;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.object.GTSListingPokemon;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;
import com.varijon.tinies.SimplyGTS.util.Util;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate.Builder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.server.permission.PermissionAPI;

public class GuiPagesPokemon 
{
	public static GooeyPage getPokemonMenu(ArrayList<GTSListingPokemon> lstGTSListing, ServerPlayerEntity player, int page)
	{
        ChestTemplate.Builder templateBuilder = ChestTemplate.builder(6);
        
        int slotColumnCount = 0;
        int slotRowCount = 0;
        
        for(int x = 0; x < lstGTSListing.size(); x++)
		{
			if(x >= (page-1) * 45 && x < page * 45)
			{
				GTSListingPokemon pokemonListing = lstGTSListing.get(x);
				if(pokemonListing.getListingStatus() != EnumListingStatus.Active)
				{
					continue;
				}
				GooeyButton itemButton;
				itemButton = GooeyButton.builder()
						.display(SpriteItemHelper.getPhoto(pokemonListing.createOrGetPokemonData()))
						.title(Util.getPokemonDisplayName((pokemonListing.createOrGetPokemonData())))
						.lore(PokemonListingDisplay.getPokemonListingDisplayList(pokemonListing.createOrGetPokemonData(), player.getUUID(),pokemonListing,false))
						.onClick((action) -> 
						{
							if(action.getButton().getDisplay() != null)
							{
								if(pokemonListing.getListingStatus() == EnumListingStatus.Active)
								{
									UIManager.closeUI(action.getPlayer());
									if(pokemonListing.getListingOwner().equals(action.getPlayer().getUUID()))
									{
										UIManager.openUIForcefully(action.getPlayer(), getCancelConfirmMenuPokemon((int) pokemonListing.getListingPrice(), pokemonListing.getListingID(), page, false));								
									}
									else
									{									
										UIManager.openUIForcefully(action.getPlayer(), getBuyConfirmMenuPokemon(action.getButton().getDisplay(), (int) pokemonListing.getListingPrice(), pokemonListing.getListingID(), page, action.getPlayer()));								
									}
								}
								else
								{
			        				UIManager.closeUI(action.getPlayer());
			        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
			        				UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));
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
        GooeyButton backPageButton = GooeyButton.builder()
                .display(new ItemStack(Items.ARROW))
                .title(TextFormatting.GOLD + "Click for page " + TextFormatting.YELLOW + (page - 1))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page-1));
        		})
                .build();

        GooeyButton switchToItemsButton = GooeyButton.builder()
                .display(new ItemStack(Items.COOKIE))
                .title(TextFormatting.GOLD + "Click to see Item Listings")
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), GuiPagesItems.getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), 1));
        		})
                .build();
        
        GooeyButton forwardPageButton = GooeyButton.builder()
                .display(new ItemStack(Items.ARROW))
                .title(TextFormatting.GOLD + "Click for page " + TextFormatting.YELLOW + (page + 1))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page+1));
        		})
                .build();
        if(page != 1)
        {
	        templateBuilder
	        	.set(5, 0, backPageButton)
	        	.set(5, 4, switchToItemsButton)  
	        	.set(5, 8, forwardPageButton);        	
        }
        else
        {
            templateBuilder
        		.set(5, 4, switchToItemsButton)
            	.set(5, 8, forwardPageButton);
        }
        
		ChestTemplate template = templateBuilder
                .build();

        
        GooeyPage pageBuilder = GooeyPage.builder()
                .title(TextFormatting.DARK_BLUE + "GTS Pokemon Listings")
                .template(template)
                .build();

        return pageBuilder;
	}
	
	public static GooeyPage getBuyConfirmMenuPokemon(ItemStack displayItem, int cost, UUID listingID, int page, ServerPlayerEntity player)
	{
		GooeyButton emptySlot = GooeyButton.builder()
                .display(new ItemStack(Blocks.WHITE_STAINED_GLASS_PANE,1))
                .title("")
                .build();
		
		GooeyButton confirmButton = GooeyButton.builder()
                .display(new ItemStack(Blocks.GREEN_STAINED_GLASS_PANE,1))
                .title(TextFormatting.GREEN + "Click here to buy for " + TextFormatting.RED + cost + TextFormatting.GREEN + "!")
                .onClick((action) -> 
        		{
        			GTSListingPokemon gtsListingPokemon = GTSDataManager.getListingPokemonData(listingID);
        			if(gtsListingPokemon == null)
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
    					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));
        				return;
        			}
        			if(gtsListingPokemon.getListingStatus() == EnumListingStatus.Active)
        			{
        				PlayerPartyStorage partyBuyer = StorageProxy.getParty(action.getPlayer());
        				

    					double oldBalBuyer = partyBuyer.getBalance().doubleValue();
        				
        				if(partyBuyer.hasBalance(cost))
        				{
        					partyBuyer.take(cost);
        					partyBuyer.updatePlayer();                					
        				}
        				else
        				{
        					UIManager.closeUI(action.getPlayer());
        					action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "You don't have enough money!"), UUID.randomUUID());
        					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));
        					return;
        				}


        				PlayerPartyStorage partyReceiver = StorageProxy.getParty(gtsListingPokemon.getListingOwner());       				

        				String sellerName = UsernameCache.getLastKnownUsername(partyReceiver.getPlayerUUID());

						if(sellerName == null)
						{
							sellerName = "Someone";
						}
						String buyerName = UsernameCache.getLastKnownUsername(partyBuyer.getPlayerUUID());

						if(buyerName == null)
						{
							buyerName = "Someone";
						}

        				double oldBalSeller = partyReceiver.getBalance().doubleValue();
        				partyReceiver.add(cost);
        				
    					SimplyGTS.logger.info(buyerName + " bought " + gtsListingPokemon.createOrGetPokemonData().getDisplayName() + " from " + sellerName + " for " + gtsListingPokemon.getListingPrice());               
    					SimplyGTS.logger.info(buyerName + " oldBal: " + oldBalBuyer + " newBal: " + partyBuyer.getBalance().doubleValue() + " -- " + sellerName + " oldBal: " + oldBalSeller + " newBal: " + partyReceiver.getBalance().doubleValue());

//        				if(partyReceiver.getPlayer() != null)
//        				{
//        					TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
//        					chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "] " + TextFormatting.GOLD + partyBuyer.getPlayer().getName() + TextFormatting.GREEN + " bought your "));
//        					chatTrans.append(Util.getHoverText(gtsListingPokemon.createOrGetPokemonData(), action.getPlayer()));
//        					chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + cost + TextFormatting.GREEN + "!" ));
//        					partyReceiver.getPlayer().sendMessage(chatTrans);	
//        				}
    					partyReceiver.updatePlayer();

        				UIManager.closeUI(action.getPlayer());

        				TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
        				chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " You bought "));
        				chatTrans.append(Util.getHoverText(gtsListingPokemon.createOrGetPokemonData(), action.getPlayer()));
        				chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + cost + TextFormatting.GREEN + "!" ));
        				action.getPlayer().sendMessage(chatTrans, UUID.randomUUID());	


        				partyBuyer.add(gtsListingPokemon.createOrGetPokemonData());
        				partyBuyer.setNeedsSaving();
        				gtsListingPokemon.setListingStatus(EnumListingStatus.Sold);
        				gtsListingPokemon.setListingBuyer(action.getPlayer().getUUID());
        				GTSDataManager.writeListingPokemonData(gtsListingPokemon);



        				UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));
        			}
        			else
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
        				UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));
        			}
        		})
                .build();

		GooeyButton cancelButton = GooeyButton.builder()
                .display(new ItemStack(Blocks.RED_STAINED_GLASS_PANE,1))
                .title(TextFormatting.RED + "Click here to cancel!")
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));
        		})
                .build();
		
		GooeyButton removeListingButton = GooeyButton.builder()
                .display(new ItemStack(Blocks.ORANGE_STAINED_GLASS_PANE,1))
                .title(TextFormatting.GREEN + "Click here to set listing to expired!")
                .onClick((action) -> 
        		{
        			GTSListingPokemon gtsListingPokemon = GTSDataManager.getListingPokemonData(listingID);
        			if(gtsListingPokemon == null)
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
    					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));
        				return;
        			}
        			if(gtsListingPokemon.getListingStatus() == EnumListingStatus.Active)
        			{
        				PlayerPartyStorage partyReceiver = StorageProxy.getParty(gtsListingPokemon.getListingOwner());  
        				if(partyReceiver.getPlayer() != null)
        				{
        					TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
        					chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.RED + " Your listing for "));
        					chatTrans.append(Util.getHoverText(gtsListingPokemon.createOrGetPokemonData(), action.getPlayer()));
        					chatTrans.append(new StringTextComponent(TextFormatting.RED + " was cancelled!" ));
        					action.getPlayer().sendMessage(chatTrans, UUID.randomUUID());	
        				}

        				UIManager.closeUI(action.getPlayer());

        				TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
        				chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " Listing for "));
        				chatTrans.append(Util.getHoverText(gtsListingPokemon.createOrGetPokemonData(), action.getPlayer()));
        				chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " cancelled!" ));
        				action.getPlayer().sendMessage(chatTrans, UUID.randomUUID());	

        				String sellerName = UsernameCache.getLastKnownUsername(gtsListingPokemon.getListingOwner());

						if(sellerName == null)
						{
							sellerName = "Someone";
						}
        				SimplyGTS.logger.info(action.getPlayer().getName().getString() + " removed " + gtsListingPokemon.createOrGetPokemonData().getDisplayName() + " from " + sellerName + " for " + gtsListingPokemon.getListingPrice());  
        				
        				gtsListingPokemon.setListingStatus(EnumListingStatus.Expired);
        				GTSDataManager.writeListingPokemonData(gtsListingPokemon);


        				UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));
        			}
        			else
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
        				UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));
        			}
        		})
                .build();

		GTSListingPokemon gtsListingPokemon = GTSDataManager.getListingPokemonData(listingID);
		GooeyButton itemToBuy = GooeyButton.builder()
                .display(SpriteItemHelper.getPhoto(gtsListingPokemon.createOrGetPokemonData()))
				.title(Util.getPokemonDisplayName((gtsListingPokemon.createOrGetPokemonData())))
				.lore(PokemonListingDisplay.getPokemonListingDisplayList(gtsListingPokemon.createOrGetPokemonData(), player.getUUID(),gtsListingPokemon,false))
                .build();
		

        Builder template = ChestTemplate.builder(3)
        		.fill(emptySlot)
        		.set(0, 4, itemToBuy)
        		.set(1, 2, confirmButton)
        		.set(1, 6, cancelButton);
		
        if(PermissionAPI.hasPermission(player,"simplygts.moderate"))
		{
	        template.set(2, 4, removeListingButton);
		}
		
        
        GooeyPage pageBuilder = GooeyPage.builder()
        		.title(TextFormatting.DARK_BLUE + "Buy this Pokemon?")
                .template(template.build())
                .build();

        return pageBuilder;
	}
	
	public static GooeyPage getCancelConfirmMenuPokemon(int cost, UUID listingID, int page, boolean isManage)
	{
		GooeyButton emptySlot = GooeyButton.builder()
                .display(new ItemStack(Blocks.WHITE_STAINED_GLASS_PANE,1))
                .title("")
                .build();
		
		GooeyButton confirmButton = GooeyButton.builder()
                .display(new ItemStack(Blocks.GREEN_STAINED_GLASS_PANE,1))
                .title(TextFormatting.GREEN + "Yes")
                .onClick((action) -> 
        		{
        			GTSListingPokemon gtsListingPokemon = GTSDataManager.getListingPokemonData(listingID);
        			if(gtsListingPokemon == null)
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
        				if(isManage)
        				{
	        				UIManager.openUIForcefully(action.getPlayer(), GuiPagesManage.getManageMenu(GTSDataManager.getAllListingsPlayer(action.getPlayer().getUUID()), action.getPlayer(), page));
        				}
        				else
        				{
        					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));        					
        				}
        				return;
        			}
        			
        			if(gtsListingPokemon.getListingStatus() == EnumListingStatus.Active)
        			{        				
        				PlayerPartyStorage partyOwner = StorageProxy.getParty(action.getPlayer());

        				UIManager.closeUI(action.getPlayer());

        				TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
        				chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " You cancelled your listing for "));
        				chatTrans.append(Util.getHoverText(gtsListingPokemon.createOrGetPokemonData(), action.getPlayer()));
        				chatTrans.append(new StringTextComponent(TextFormatting.GREEN + "!" ));
        				action.getPlayer().sendMessage(chatTrans, UUID.randomUUID());	

        				SimplyGTS.logger.info(action.getPlayer().getName().getString() + " cancelled " + gtsListingPokemon.createOrGetPokemonData().getDisplayName() + " for " + gtsListingPokemon.getListingPrice());     

        				
        				partyOwner.add(gtsListingPokemon.createOrGetPokemonData());
        				partyOwner.setNeedsSaving();
        				GTSDataManager.removeListingPokemonData(gtsListingPokemon);

        				if(isManage)
        				{
	        				UIManager.openUIForcefully(action.getPlayer(), GuiPagesManage.getManageMenu(GTSDataManager.getAllListingsPlayer(action.getPlayer().getUUID()), action.getPlayer(), page));
        				}
        				else
        				{
            				UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));        					
        				}
        			}
        			else
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
        				if(isManage)
        				{
	        				UIManager.openUIForcefully(action.getPlayer(), GuiPagesManage.getManageMenu(GTSDataManager.getAllListingsPlayer(action.getPlayer().getUUID()), action.getPlayer(), page));
        				}
        				else
        				{
        					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));        					
        				}
        			}
        			
        		})
                .build();

		GooeyButton cancelButton = GooeyButton.builder()
                .display(new ItemStack(Blocks.RED_STAINED_GLASS_PANE,1))
                .title(TextFormatting.RED + "No")
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
       				if(isManage)
    				{
        				UIManager.openUIForcefully(action.getPlayer(), GuiPagesManage.getManageMenu(GTSDataManager.getAllListingsPlayer(action.getPlayer().getUUID()), action.getPlayer(), page));
    				}
    				else
    				{
    					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page));        					
    				}
        		})
                .build();

		GTSListingPokemon gtsListingPokemon = GTSDataManager.getListingPokemonData(listingID);
		GooeyButton itemToBuy = GooeyButton.builder()
                .display(SpriteItemHelper.getPhoto(gtsListingPokemon.createOrGetPokemonData()))
				.title(Util.getPokemonDisplayName((gtsListingPokemon.createOrGetPokemonData())))
				.lore(PokemonListingDisplay.getPokemonListingDisplayList(gtsListingPokemon.createOrGetPokemonData(),  gtsListingPokemon.getListingOwner(),gtsListingPokemon,false))
                .build();
		
		
        ChestTemplate template = ChestTemplate.builder(3)
        		.fill(emptySlot)
        		.set(0, 4, itemToBuy)
        		.set(1, 2, confirmButton)
        		.set(1, 6, cancelButton)
                .build();

        
        GooeyPage pageBuilder = GooeyPage.builder()
        		.title(TextFormatting.DARK_BLUE + "Cancel listing?")
                .template(template)
                .build();

        return pageBuilder;
	}
}
