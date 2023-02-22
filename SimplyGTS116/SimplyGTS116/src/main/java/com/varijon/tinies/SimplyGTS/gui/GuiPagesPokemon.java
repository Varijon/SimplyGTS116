package com.varijon.tinies.SimplyGTS.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pixelmonmod.pixelmon.api.economy.BankAccount;
import com.pixelmonmod.pixelmon.api.economy.BankAccountProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.api.util.helpers.SpriteItemHelper;
import com.varijon.tinies.SimplyGTS.SimplyGTS;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumSortingOption;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;
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
	public static GooeyPage getPokemonMenu(ArrayList<GTSListingPokemon> lstGTSListing, ServerPlayerEntity player, int page, EnumSortingOption sort)
	{
        ChestTemplate.Builder templateBuilder = ChestTemplate.builder(6);
        
        List<GTSListingPokemon> lstSortedGTSListing = null;
        switch(sort)
        {
	        case AZ:
		        lstSortedGTSListing = lstGTSListing.stream()
	      		  .sorted(Comparator.comparing(GTSListingPokemon::getPokemonName))
	      		  .collect(Collectors.toList());
	        	break;
			case AZReversed:
		        lstSortedGTSListing = lstGTSListing.stream()
	      		  .sorted(Comparator.comparing(GTSListingPokemon::getPokemonName).reversed())
	      		  .collect(Collectors.toList());
				break;
			case Duration:
		        lstSortedGTSListing = lstGTSListing.stream()
	      		  .sorted(Comparator.comparing(GTSListingPokemon::getListingTimeRemaining).reversed())
	      		  .collect(Collectors.toList());
				break;
			case DurationReversed:
		        lstSortedGTSListing = lstGTSListing.stream()
	      		  .sorted(Comparator.comparing(GTSListingPokemon::getListingTimeRemaining))
	      		  .collect(Collectors.toList());
				break;
			case None:
				lstSortedGTSListing = lstGTSListing;
				break;
			case Price:
		        lstSortedGTSListing = lstGTSListing.stream()
	      		  .sorted(Comparator.comparing(GTSListingPokemon::getListingPrice))
	      		  .collect(Collectors.toList());
				break;
			case PriceReversed:
		        lstSortedGTSListing = lstGTSListing.stream()
	      		  .sorted(Comparator.comparing(GTSListingPokemon::getListingPrice).reversed())
	      		  .collect(Collectors.toList());
				break;
			default:
				break;
        		
        }
        
        int slotColumnCount = 0;
        int slotRowCount = 0;
        
        for(int x = 0; x < lstSortedGTSListing.size(); x++)
		{
			if(x >= (page-1) * 45 && x < page * 45)
			{
				GTSListingPokemon pokemonListing = lstSortedGTSListing.get(x);
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
										UIManager.openUIForcefully(action.getPlayer(), getBuyConfirmMenuPokemon((int) pokemonListing.getListingPrice(), pokemonListing.getListingID(), page, action.getPlayer()));								
									}
								}
								else
								{
			        				UIManager.closeUI(action.getPlayer());
			        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
			        				UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, sort));
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
   					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page-1, sort));
        		})
                .build();

        GooeyButton switchToItemsButton = GooeyButton.builder()
                .display(new ItemStack(Items.COOKIE))
                .title(TextFormatting.GOLD + "Click to see Item Listings")
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), GuiPagesItems.getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), 1, sort));
        		})
                .build();
        
        GooeyButton forwardPageButton = GooeyButton.builder()
                .display(new ItemStack(Items.ARROW))
                .title(TextFormatting.GOLD + "Click for page " + TextFormatting.YELLOW + (page + 1))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page+1, sort));
        		})
                .build();
        GooeyButton AZSortButton = GooeyButton.builder()
                .display(Util.getButtonDisplay(EnumSortingOption.AZ, sort))
                .title(TextFormatting.GOLD + Util.getSortTextAZ(sort))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, Util.getReverseSortAZ(sort)));
        		})
                .build();   
        GooeyButton DurationSortButton = GooeyButton.builder()
                .display(Util.getButtonDisplay(EnumSortingOption.Duration,sort))
                .title(TextFormatting.GOLD + Util.getSortTextDuration(sort))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, Util.getReverseSortDuration(sort)));
        		})
                .build();   
        GooeyButton PriceSortButton = GooeyButton.builder()
                .display(Util.getButtonDisplay(EnumSortingOption.Price,sort))
                .title(TextFormatting.GOLD + Util.getSortTextPrice(sort))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, Util.getReverseSortPrice(sort)));
        		})
                .build();
        
        if(page != 1)
        {
	        templateBuilder
	        	.set(5, 0, backPageButton)
	        	.set(5, 1, PriceSortButton)
	        	.set(5, 2, DurationSortButton)
	        	.set(5, 3, AZSortButton)
	        	.set(5, 4, switchToItemsButton)
	        	.set(5, 8, forwardPageButton);
        }
        else
        {
            templateBuilder
        		.set(5, 1, PriceSortButton)
	        	.set(5, 2, DurationSortButton)
        		.set(5, 3, AZSortButton)
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
	
	public static GooeyPage getBuyConfirmMenuPokemon(int cost, UUID listingID, int page, ServerPlayerEntity player)
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
    					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, EnumSortingOption.None));
        				return;
        			}
        			if(gtsListingPokemon.getListingStatus() == EnumListingStatus.Active)
        			{
        				PlayerPartyStorage partyBuyer = StorageProxy.getParty(action.getPlayer());
        				BankAccount accountBuyer = BankAccountProxy.getBankAccount(action.getPlayer()).orElse(null);

    					double oldBalBuyer = accountBuyer.getBalance().doubleValue();
        				
        				if(accountBuyer.hasBalance(cost))
        				{
        					accountBuyer.take(cost);
        					accountBuyer.updatePlayer();                					
        				}
        				else
        				{
        					UIManager.closeUI(action.getPlayer());
        					action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "You don't have enough money!"), UUID.randomUUID());
        					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, EnumSortingOption.None));
        					return;
        				}


        				PlayerPartyStorage partyReceiver = StorageProxy.getParty(gtsListingPokemon.getListingOwner());      
        				BankAccount accountReceiver = BankAccountProxy.getBankAccount(gtsListingPokemon.getListingOwner()).orElse(null); 				

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

        				double oldBalSeller = accountReceiver.getBalance().doubleValue();
        				double combinedTax = GTSDataManager.getConfig().getGeneralTax() + GTSDataManager.getConfig().getBreedablePokemonTax();
        				if(gtsListingPokemon.isSoldAsBreedable())
        				{
            				accountReceiver.add(cost - ((int)(cost * combinedTax)));        					
        				}
        				else
        				{
        					accountReceiver.add(cost - ((int)(cost * GTSDataManager.getConfig().getGeneralTax())));
        				}
        				
    					SimplyGTS.logger.info(buyerName + " bought " + gtsListingPokemon.createOrGetPokemonData().getDisplayName() + " from " + sellerName + " for " + gtsListingPokemon.getListingPrice());               
    					SimplyGTS.logger.info(buyerName + " oldBal: " + oldBalBuyer + " newBal: " + accountBuyer.getBalance().doubleValue() + " -- " + sellerName + " oldBal: " + oldBalSeller + " newBal: " + accountReceiver.getBalance().doubleValue());

//        				if(partyReceiver.getPlayer() != null)
//        				{
//        					TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
//        					chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "] " + TextFormatting.GOLD + partyBuyer.getPlayer().getName() + TextFormatting.GREEN + " bought your "));
//        					chatTrans.append(Util.getHoverText(gtsListingPokemon.createOrGetPokemonData(), action.getPlayer()));
//        					chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + cost + TextFormatting.GREEN + "!" ));
//        					partyReceiver.getPlayer().sendMessage(chatTrans);	
//        				}
    					accountReceiver.updatePlayer();

        				UIManager.closeUI(action.getPlayer());

        				TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
        				chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " You bought "));
        				chatTrans.append(Util.getHoverText(gtsListingPokemon, action.getPlayer()));
        				chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + cost + TextFormatting.GREEN + "!" ));
        				action.getPlayer().sendMessage(chatTrans, UUID.randomUUID());	

        				Pokemon pokemonToAdd = gtsListingPokemon.createOrGetPokemonData();
        				if(!gtsListingPokemon.isSoldAsBreedable())
        				{
            				pokemonToAdd.addFlag("unbreedable"); 
            				if(pokemonToAdd.hasFlag("gtsBreedable"))
            				{
            					pokemonToAdd.removeFlag("gtsBreedable");
            					pokemonToAdd.getPersistentData().remove("currentOwner");
            				}
        				}
        				else
        				{
        					pokemonToAdd.addFlag("gtsBreedable");
        					pokemonToAdd.getPersistentData().putString("currentOwner", partyBuyer.getPlayerUUID().toString());
        				}
        				partyBuyer.add(pokemonToAdd);
        				partyBuyer.setNeedsSaving();
        				gtsListingPokemon.setListingStatus(EnumListingStatus.Sold);
        				gtsListingPokemon.setListingBuyer(action.getPlayer().getUUID());
        				GTSDataManager.writeListingPokemonData(gtsListingPokemon);



        				UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, EnumSortingOption.None));
        			}
        			else
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
        				UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, EnumSortingOption.None));
        			}
        		})
                .build();

		GooeyButton cancelButton = GooeyButton.builder()
                .display(new ItemStack(Blocks.RED_STAINED_GLASS_PANE,1))
                .title(TextFormatting.RED + "Click here to cancel!")
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, EnumSortingOption.None));
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
    					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, EnumSortingOption.None));
        				return;
        			}
        			if(gtsListingPokemon.getListingStatus() == EnumListingStatus.Active)
        			{
        				PlayerPartyStorage partyReceiver = StorageProxy.getParty(gtsListingPokemon.getListingOwner());  
        				if(partyReceiver.getPlayer() != null)
        				{
        					TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
        					chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.RED + " Your listing for "));
        					chatTrans.append(Util.getHoverText(gtsListingPokemon, action.getPlayer()));
        					chatTrans.append(new StringTextComponent(TextFormatting.RED + " was cancelled!" ));
        					action.getPlayer().sendMessage(chatTrans, UUID.randomUUID());	
        				}

        				UIManager.closeUI(action.getPlayer());

        				TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
        				chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " Listing for "));
        				chatTrans.append(Util.getHoverText(gtsListingPokemon, action.getPlayer()));
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


        				UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, EnumSortingOption.None));
        			}
        			else
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
        				UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, EnumSortingOption.None));
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
        					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, EnumSortingOption.None));        					
        				}
        				return;
        			}
        			
        			if(gtsListingPokemon.getListingStatus() == EnumListingStatus.Active)
        			{        				
        				PlayerPartyStorage partyOwner = StorageProxy.getParty(action.getPlayer());

        				UIManager.closeUI(action.getPlayer());

        				TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
        				chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " You cancelled your listing for "));
        				chatTrans.append(Util.getHoverText(gtsListingPokemon, action.getPlayer()));
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
            				UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, EnumSortingOption.None));        					
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
        					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, EnumSortingOption.None));        					
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
    					UIManager.openUIForcefully(action.getPlayer(), getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), page, EnumSortingOption.None));        					
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
