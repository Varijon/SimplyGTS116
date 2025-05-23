package com.varijon.tinies.SimplyGTS.gui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.economy.BankAccount;
import com.pixelmonmod.pixelmon.api.economy.BankAccountProxy;
import com.pixelmonmod.pixelmon.api.item.JsonItemStack;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.storage.PartyStorage;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.varijon.tinies.SimplyGTS.SimplyGTS;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;
import com.varijon.tinies.SimplyGTS.enums.EnumSortingOption;
import com.varijon.tinies.SimplyGTS.object.GTSItemPriceHistory;
import com.varijon.tinies.SimplyGTS.object.GTSListingHistory;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;
import com.varijon.tinies.SimplyGTS.object.GTSPriceHistoryList;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;
import com.varijon.tinies.SimplyGTS.util.Util;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate.Builder;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipDisplayFlags;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.server.permission.PermissionAPI;

public class GuiPagesItems 
{
	public static GooeyPage getItemMenu(ArrayList<GTSListingItem> lstGTSListing, ServerPlayerEntity player, int page, EnumSortingOption sort)
	{
        ChestTemplate.Builder templateBuilder = ChestTemplate.builder(6);
        
        List<GTSListingItem> lstSortedGTSListing = null;
        switch(sort)
        {
	        case AZ:
		        lstSortedGTSListing = lstGTSListing.stream()
	      		  .sorted(Comparator.comparing(GTSListingItem::getItemHoverName))
	      		  .collect(Collectors.toList());
	        	break;
			case AZReversed:
		        lstSortedGTSListing = lstGTSListing.stream()
	      		  .sorted(Comparator.comparing(GTSListingItem::getItemHoverName).reversed())
	      		  .collect(Collectors.toList());
				break;
			case Duration:
		        lstSortedGTSListing = lstGTSListing.stream()
	      		  .sorted(Comparator.comparing(GTSListingItem::getListingTimeRemaining).reversed())
	      		  .collect(Collectors.toList());
				break;
			case DurationReversed:
		        lstSortedGTSListing = lstGTSListing.stream()
	      		  .sorted(Comparator.comparing(GTSListingItem::getListingTimeRemaining))
	      		  .collect(Collectors.toList());
				break;
			case None:
				lstSortedGTSListing = lstGTSListing;
				break;
			case Price:
		        lstSortedGTSListing = lstGTSListing.stream()
	      		  .sorted(Comparator.comparing(GTSListingItem::getListingPrice))
	      		  .collect(Collectors.toList());
				break;
			case PriceReversed:
		        lstSortedGTSListing = lstGTSListing.stream()
	      		  .sorted(Comparator.comparing(GTSListingItem::getListingPrice).reversed())
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
				GTSListingItem itemListing = lstSortedGTSListing.get(x);
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
									if(itemListing.getListingOwner().equals(action.getPlayer().getUUID()))
									{
										UIManager.openUIForcefully(action.getPlayer(), getCancelConfirmMenuItems((int) itemListing.getListingPrice(), itemListing.getListingID(), page, false));								
									}
									else
									{									
										UIManager.openUIForcefully(action.getPlayer(), getBuyConfirmMenuItems((int) itemListing.getListingPrice(), itemListing.getListingID(), page,action.getPlayer()));								
									}
								}
								else
								{
			        				UIManager.closeUI(action.getPlayer());
			        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
			        				UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, sort));
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
   					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page-1, sort));
        		})
                .build();

        GooeyButton switchToPokemonButton = GooeyButton.builder()
                .display(new ItemStack(PixelmonItems.poke_ball))
                .title(TextFormatting.GOLD + "Click to see Pokemon Listings")
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), GuiPagesPokemon.getPokemonMenu(GTSDataManager.getGTSPokemonListings(), action.getPlayer(), 1, sort));
        		})
                .build();
        
        GooeyButton forwardPageButton = GooeyButton.builder()
                .display(new ItemStack(Items.ARROW))
                .title(TextFormatting.GOLD + "Click for page " + TextFormatting.YELLOW + (page + 1))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page+1, sort));
        		})
                .build();               
        GooeyButton AZSortButton = GooeyButton.builder()
                .display(Util.getButtonDisplay(EnumSortingOption.AZ, sort))
                .title(TextFormatting.GOLD + Util.getSortTextAZ(sort))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, Util.getReverseSortAZ(sort)));
        		})
                .build();   
        GooeyButton DurationSortButton = GooeyButton.builder()
                .display(Util.getButtonDisplay(EnumSortingOption.Duration,sort))
                .title(TextFormatting.GOLD + Util.getSortTextDuration(sort))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, Util.getReverseSortDuration(sort)));
        		})
                .build();   
        GooeyButton PriceSortButton = GooeyButton.builder()
                .display(Util.getButtonDisplay(EnumSortingOption.Price,sort))
                .title(TextFormatting.GOLD + Util.getSortTextPrice(sort))
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, Util.getReverseSortPrice(sort)));
        		})
                .build();

        
        if(page != 1)
        {
	        templateBuilder
	        	.set(5, 0, backPageButton)
	        	.set(5, 1, PriceSortButton)
	        	.set(5, 2, DurationSortButton)
	        	.set(5, 3, AZSortButton)
	        	.set(5, 4, switchToPokemonButton)
	        	.set(5, 8, forwardPageButton);
        }
        else
        {
            templateBuilder
        		.set(5, 1, PriceSortButton)
	        	.set(5, 2, DurationSortButton)
        		.set(5, 3, AZSortButton)
        		.set(5, 4, switchToPokemonButton)
            	.set(5, 8, forwardPageButton);
        }
        
		ChestTemplate template = templateBuilder
                .build();

        
        GooeyPage pageBuilder = GooeyPage.builder()
                .title(TextFormatting.DARK_BLUE + "GTS Item Listings")
                .template(template)
                .build();

        return pageBuilder;
	}
	

	public static GooeyPage getBuyConfirmMenuItems(int cost, UUID listingID, int page, ServerPlayerEntity player)
	{
		GooeyButton emptySlot = GooeyButton.builder()
                .display(new ItemStack(Blocks.WHITE_STAINED_GLASS_PANE,1))
                .title("")
                .build();


		GTSListingItem gtsListingItemBuy = GTSDataManager.getListingItemsData(listingID);
		GooeyButton itemToBuy = GooeyButton.builder()
                .display(ItemListingDisplay.getItemListingDisplay(gtsListingItemBuy.createOrGetItemStack(), player.getUUID(),gtsListingItemBuy, false))
				.title(gtsListingItemBuy.createOrGetItemStack().getHoverName())
				.lore(ITextComponent.class,ItemListingDisplay.getItemListingDisplayList(gtsListingItemBuy.createOrGetItemStack(), player.getUUID(),gtsListingItemBuy, false))
                .build();
		
		GooeyButton confirmButton = GooeyButton.builder()
                .display(new ItemStack(Blocks.GREEN_STAINED_GLASS_PANE,1))
                .title(TextFormatting.GREEN + "Click here to buy for " + TextFormatting.RED + cost + TextFormatting.GREEN + "!")
                .onClick((action) -> 
        		{
        			GTSListingItem gtsListingItem = GTSDataManager.getListingItemsData(listingID);
        			if(gtsListingItem == null)
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
    					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, EnumSortingOption.None));
        				return;
        			}
        			if(gtsListingItem.getListingStatus() == EnumListingStatus.Active)
        			{
        				PlayerPartyStorage partyBuyer = StorageProxy.getParty(action.getPlayer());
        				BankAccount accountBuyer = BankAccountProxy.getBankAccount(action.getPlayer()).orElse(null);
        				
        				if(accountBuyer.hasBalance(cost))
        				{               					
        				}
        				else
        				{
        					UIManager.closeUI(action.getPlayer());
        					action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "You don't have enough money!"), UUID.randomUUID());
        					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, EnumSortingOption.None));
        					return;
        				}
        				
        			    if(!action.getPlayer().inventory.add(gtsListingItem.createOrGetItemStack().copy()))
            			{
            				UIManager.closeUI(action.getPlayer());
            				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Your inventory is full, make space first!"), UUID.randomUUID());
            			}
        				else
        				{
        					double oldBalBuyer = accountBuyer.getBalance().doubleValue();
        					accountBuyer.take(cost);
        					accountBuyer.updatePlayer(); 					
            				
            				PlayerPartyStorage partyReceiver = StorageProxy.getParty(gtsListingItem.getListingOwner());
            				BankAccount accountReceiver = BankAccountProxy.getBankAccount(gtsListingItem.getListingOwner()).orElse(null);
            				
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
            				accountReceiver.add(cost - ((int)(cost * GTSDataManager.getConfig().getGeneralTax())));
        					SimplyGTS.logger.info(buyerName + " bought " + gtsListingItem.getItemCount() + "x " + gtsListingItem.getItemName() + " from " + sellerName + " for " + gtsListingItem.getListingPrice());               
        					SimplyGTS.logger.info(buyerName + " oldBal: " + oldBalBuyer + " newBal: " + accountBuyer.getBalance().doubleValue() + " -- " + sellerName + " oldBal: " + oldBalSeller + " newBal: " + accountReceiver.getBalance().doubleValue());
//            				if(partyReceiver.getPlayer() != null)
//            				{
//            					TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
//            					chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "] " + TextFormatting.GOLD + partyBuyer.getPlayer().getName() + TextFormatting.GREEN + " bought your "+ TextFormatting.WHITE + gtsListingItem.getItemCount() + "x "));
//                				chatTrans.append(gtsListingItem.getTextComponent());
//            					chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + cost + TextFormatting.GREEN + "!" ));
//            					partyReceiver.getPlayer().sendMessage(chatTrans);	
//            				}
        					accountReceiver.updatePlayer();

            				UIManager.closeUI(action.getPlayer());

            				TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
            				chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " You bought " + TextFormatting.WHITE + gtsListingItem.getItemCount() + "x "));
            				chatTrans.append(gtsListingItem.createOrGetItemStack().getDisplayName());
            				chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " for " + TextFormatting.GOLD + cost + TextFormatting.GREEN + "!" ));
            				action.getPlayer().sendMessage(chatTrans, UUID.randomUUID());	

            				gtsListingItem.setListingStatus(EnumListingStatus.Sold);
            				gtsListingItem.setListingBuyer(action.getPlayer().getUUID());
            				GTSDataManager.writeListingItemsData(gtsListingItem);
            				
            				Util.registerPriceHistory(gtsListingItem);
            				
//                            .display(ItemListingDisplay.getItemListingDisplay(gtsListingItem.createOrGetItemStack(), gtsListingItem.getListingOwner(),gtsListingItem, true))
//            				.title(gtsListingItem.createOrGetItemStack().getHoverName())
//            				.lore(ItemListingDisplay.getItemListingDisplayList(gtsListingItem.createOrGetItemStack(), gtsListingItem.getListingOwner(),gtsListingItem, false))
            		
            				GTSDataManager.addListingHistoryData(new GTSListingHistory(gtsListingItem, System.currentTimeMillis(), itemToBuy.getDisplay().serializeNBT().toString()));

            				UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, EnumSortingOption.None));
        				}
        			}
        			else
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
        				UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, EnumSortingOption.None));
        			}
        		})
                .build();
		
		GooeyButton cancelButton = GooeyButton.builder()
                .display(new ItemStack(Blocks.RED_STAINED_GLASS_PANE,1))
                .title(TextFormatting.RED + "Click here to cancel!")
                .onClick((action) -> 
        		{
       				UIManager.closeUI(action.getPlayer());
   					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, EnumSortingOption.None));
        		})
                .build();
		
		GooeyButton removeListingButton = GooeyButton.builder()
                .display(new ItemStack(Blocks.ORANGE_STAINED_GLASS_PANE,1))
                .title(TextFormatting.GREEN + "Click here to set listing to expired!")
                .onClick((action) -> 
        		{
        			GTSListingItem gtsListingItem = GTSDataManager.getListingItemsData(listingID);
        			if(gtsListingItem == null)
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
    					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, EnumSortingOption.None));
        				return;
        			}
        			if(gtsListingItem.getListingStatus() == EnumListingStatus.Active)
        			{
//        				PlayerPartyStorage partyBuyer = Pixelmon.storageManager.getParty(action.getPlayer());
//        				if(partyBuyer.getMoney() >= cost)
//        				{
//        					partyBuyer.changeMoney(-cost);
//        					partyBuyer.updatePlayer(partyBuyer.getMoney());                					
//        				}
//        				else
//        				{
//        					UIManager.closeUI(action.getPlayer());
//        					action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "You don't have enough money!"));
//        					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page));
//        					return;
//        				}


        				PlayerPartyStorage partyReceiver = StorageProxy.getParty(gtsListingItem.getListingOwner());
//        				partyReceiver.changeMoney(cost);
        				
        				if(partyReceiver.getPlayer() != null)
        				{
        					TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
        					chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.RED + " Your listing for " + TextFormatting.WHITE + gtsListingItem.getItemCount() + "x "));
        					chatTrans.append(gtsListingItem.createOrGetItemStack().getDisplayName());
        					chatTrans.append(new StringTextComponent(TextFormatting.RED + " was cancelled!" ));
        					partyReceiver.getPlayer().sendMessage(chatTrans, UUID.randomUUID());	
        				}

        				UIManager.closeUI(action.getPlayer());

        				TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
        				chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " Listing for " + TextFormatting.WHITE + gtsListingItem.getItemCount() + "x "));
    					chatTrans.append(gtsListingItem.createOrGetItemStack().getDisplayName());
        				chatTrans.append(new StringTextComponent(TextFormatting.GREEN + " cancelled!" ));        
        				
        				String sellerName = UsernameCache.getLastKnownUsername(gtsListingItem.getListingOwner());

						if(sellerName == null)
						{
							sellerName = "Someone";
						}
        				SimplyGTS.logger.info(action.getPlayer().getName().getString() + " removed " + gtsListingItem.getItemCount() + "x " + gtsListingItem.getItemName() + " from " + sellerName + " for " + gtsListingItem.getListingPrice());     

        				action.getPlayer().sendMessage(chatTrans, UUID.randomUUID());	

        				gtsListingItem.setListingStatus(EnumListingStatus.Expired);
        				GTSDataManager.writeListingItemsData(gtsListingItem);

        				

        				UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, EnumSortingOption.None));
        			}
        			else
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
        				UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, EnumSortingOption.None));
        			}
        		})
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
        		.title(TextFormatting.DARK_BLUE + "Buy this item?")
                .template(template.build())
                .build();

        return pageBuilder;
	}
	
	public static GooeyPage getCancelConfirmMenuItems(int cost, UUID listingID, int page, boolean isManage)
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
        			GTSListingItem gtsListingItem = GTSDataManager.getListingItemsData(listingID);
        			if(gtsListingItem == null)
        			{
        				UIManager.closeUI(action.getPlayer());
        				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Listing is no longer available!"), UUID.randomUUID());
        				if(isManage)
        				{
            				UIManager.openUIForcefully(action.getPlayer(), GuiPagesManage.getManageMenu(GTSDataManager.getAllListingsPlayer(action.getPlayer().getUUID()), action.getPlayer(), page));
        				}
        				else
        				{
        					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, EnumSortingOption.None));        					
        				}
        				return;
        			}
        			if(gtsListingItem.getListingStatus() == EnumListingStatus.Active)
        			{   
        				if(!action.getPlayer().inventory.add(gtsListingItem.createOrGetItemStack().copy()))
            			{
            				UIManager.closeUI(action.getPlayer());
            				action.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "Your inventory is full, make space first!"), UUID.randomUUID());
            			}
        				else
        				{

            				UIManager.closeUI(action.getPlayer());

            				TranslationTextComponent chatTrans = new TranslationTextComponent("", new Object());
            				chatTrans.append(new StringTextComponent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "GTS" + TextFormatting.GRAY + "]" + TextFormatting.GREEN + " You cancelled your listing for " + TextFormatting.WHITE + gtsListingItem.getItemCount() + "x "));
            				chatTrans.append(gtsListingItem.createOrGetItemStack().getDisplayName());
            				chatTrans.append(new StringTextComponent(TextFormatting.GREEN + "!" ));
            				action.getPlayer().sendMessage(chatTrans, UUID.randomUUID());	       
            				
            				SimplyGTS.logger.info(action.getPlayer().getName().getString() + " cancelled " + gtsListingItem.getItemCount() + "x " + gtsListingItem.getItemName() + " for " + gtsListingItem.getListingPrice());     


            				GTSDataManager.removeListingItemsData(gtsListingItem);
        					
        				}

        				if(isManage)
        				{
            				UIManager.openUIForcefully(action.getPlayer(), GuiPagesManage.getManageMenu(GTSDataManager.getAllListingsPlayer(action.getPlayer().getUUID()), action.getPlayer(), page));
        				}
        				else
        				{
        					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, EnumSortingOption.None));        					
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
        					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, EnumSortingOption.None));        					
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
    					UIManager.openUIForcefully(action.getPlayer(), getItemMenu(GTSDataManager.getGTSItemsListings(), action.getPlayer(), page, EnumSortingOption.None));        					
    				}
        		})
                .build();

		GTSListingItem gtsListingItem = GTSDataManager.getListingItemsData(listingID);
		GooeyButton itemToBuy = GooeyButton.builder()
                .display(ItemListingDisplay.getItemListingDisplay(gtsListingItem.createOrGetItemStack(), gtsListingItem.getListingOwner(),gtsListingItem, true))
				.title(gtsListingItem.createOrGetItemStack().getHoverName())
				.lore(ITextComponent.class,ItemListingDisplay.getItemListingDisplayList(gtsListingItem.createOrGetItemStack(), gtsListingItem.getListingOwner(),gtsListingItem, false))
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
