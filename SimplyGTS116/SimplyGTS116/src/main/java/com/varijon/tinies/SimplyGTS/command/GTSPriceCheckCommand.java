package com.varijon.tinies.SimplyGTS.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.command.PixelCommand;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItemWithVariation;
import com.varijon.tinies.SimplyGTS.SimplyGTS;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;
import com.varijon.tinies.SimplyGTS.object.GTSItemPriceHistory;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;
import com.varijon.tinies.SimplyGTS.object.GTSPriceHistoryList;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;
import com.varijon.tinies.SimplyGTS.util.Util;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.server.permission.PermissionAPI;

public class GTSPriceCheckCommand extends PixelCommand {

	
	public GTSPriceCheckCommand(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher, "pricecheck", "/pricecheck", 2);   
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void execute(CommandSource sender, String[] args) throws CommandException, CommandSyntaxException
	{
		if(sender.getPlayerOrException() instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity player = sender.getPlayerOrException();
			
			if(player.getMainHandItem() == null)
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Hand is empty!"), UUID.randomUUID());			
				return;
			}
			if(player.getMainHandItem().getItem() == Items.AIR)
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Hand is empty!"), UUID.randomUUID());			
				return;
			}
			
			ItemStack itemToSell = player.getMainHandItem();
			int averagePrice = -1;
			int shopBuyPrice = -1;
			int shopSellPrice = -1;
			int totalSold = -1;
			
			GTSPriceHistoryList priceHistoryList = GTSDataManager.getPriceHistoryList(itemToSell.getItem().getRegistryName().toString());
			if(priceHistoryList != null)
			{
				GTSItemPriceHistory priceHistory = priceHistoryList.getItemPriceHistory(itemToSell);
				if(priceHistory != null)
				{
					if(priceHistory.getAveragePrice() != -1)
					{
						averagePrice = priceHistory.getAveragePrice();
						totalSold = priceHistory.getNumberSold();
					}
				}
			}
			ShopItemWithVariation shopItem = Util.getShopItem(itemToSell);
			if(shopItem != null)
			{
				shopBuyPrice = shopItem.getBuyCost();
			}
			ShopItemWithVariation shopItem2 = Util.getSellPrice(itemToSell);
			if(shopItem2 != null)
			{
				shopSellPrice = shopItem2.getSellCost();
			}
			
			player.sendMessage(new StringTextComponent(TextFormatting.YELLOW + "Prices for: " + TextFormatting.GOLD + itemToSell.getHoverName().getString()), UUID.randomUUID());
			player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Buy from shop: " + (shopBuyPrice == -1 ? TextFormatting.GRAY + "No price" : TextFormatting.GOLD + "" + shopBuyPrice)), UUID.randomUUID());
			player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Sell to shop: " + (shopSellPrice == -1 ? TextFormatting.GRAY + "No price" : TextFormatting.GOLD + "" + shopSellPrice)), UUID.randomUUID());
			player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Average price: " + (averagePrice == -1 ? TextFormatting.GRAY + "No price" : TextFormatting.GOLD + "" + averagePrice)), UUID.randomUUID());
			player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Market sales: " + (totalSold == -1 ? TextFormatting.GRAY + "None" : TextFormatting.GOLD + "" + totalSold)), UUID.randomUUID());
			
		}
		return;
	}
	
	static void sendSubCommandUsage(ServerPlayerEntity player)
	{
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /pricecheck"), UUID.randomUUID());	
		player.sendMessage(new StringTextComponent(TextFormatting.RED + "Shows prices for held item"), UUID.randomUUID());
	}
}
