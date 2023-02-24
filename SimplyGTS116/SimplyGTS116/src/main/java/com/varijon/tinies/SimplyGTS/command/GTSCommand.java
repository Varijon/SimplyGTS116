package com.varijon.tinies.SimplyGTS.command;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.command.PixelmonCommandUtils;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.command.PixelCommand;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ServerNPCRegistry;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItemWithVariation;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopkeeperData;
import com.varijon.tinies.SimplyGTS.SimplyGTS;
import com.varijon.tinies.SimplyGTS.command.subcommand.GTSSellItemSubCommand;
import com.varijon.tinies.SimplyGTS.command.subcommand.GTSSellPokemonSubCommand;
import com.varijon.tinies.SimplyGTS.command.subcommand.GTSShowListingSubcommand;
import com.varijon.tinies.SimplyGTS.enums.EnumListingStatus;
import com.varijon.tinies.SimplyGTS.enums.EnumListingType;
import com.varijon.tinies.SimplyGTS.enums.EnumSortingOption;
import com.varijon.tinies.SimplyGTS.gui.GuiPagesHistory;
import com.varijon.tinies.SimplyGTS.gui.GuiPagesItems;
import com.varijon.tinies.SimplyGTS.gui.GuiPagesManage;
import com.varijon.tinies.SimplyGTS.gui.GuiPagesPokemon;
import com.varijon.tinies.SimplyGTS.object.GTSListing;
import com.varijon.tinies.SimplyGTS.object.GTSListingItem;
import com.varijon.tinies.SimplyGTS.object.GTSListingPokemon;
import com.varijon.tinies.SimplyGTS.object.ItemConfigMinPrice;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;
import com.varijon.tinies.SimplyGTS.util.Util;

import ca.landonjw.gooeylibs2.api.UIManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.server.permission.PermissionAPI;

public class GTSCommand extends PixelCommand {

	private List aliases;
	
	public GTSCommand(CommandDispatcher<CommandSource> dispatcher)
	{
       super(dispatcher, "gts", "/gts", 2);   
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
//			if(!PermissionAPI.hasPermission(player,"simplygts.gts"))
//			{
//				player.sendMessage(new StringTextComponent(TextFormatting.RED + "You don't have permission to use this command"), UUID.randomUUID());
//		        return;
//			}
			if(args.length == 0)
			{
				UIManager.openUIForcefully(player, GuiPagesPokemon.getPokemonMenu(GTSDataManager.getGTSPokemonListings(), player, 1, EnumSortingOption.None));
				return;
			}
			if(args[0].equals("pokemon"))
			{
				UIManager.openUIForcefully(player, GuiPagesPokemon.getPokemonMenu(GTSDataManager.getGTSPokemonListings(), player, 1, EnumSortingOption.None));
				return;
			}
			if(args[0].equals("manage"))
			{
				UIManager.openUIForcefully(player, GuiPagesManage.getManageMenu(GTSDataManager.getAllListingsPlayer(player.getUUID()),player, 1));
				return;
			}
			if(args[0].equals("history"))
			{
				if(args.length == 2)
				{
					if(PermissionAPI.hasPermission(player,"simplygts.moderate"))
					{
						UUID otherPlayerUUID = null;
						
						ServerPlayerEntity otherPlayer = sender.getServer().getPlayerList().getPlayerByName(args[1]);
						if(otherPlayer == null)
						{
	        				GameProfile otherPlayerProfile = sender.getServer().getProfileCache().get(args[1]);	
	        				if(otherPlayerProfile == null)
	        				{
	        					player.sendMessage(new StringTextComponent(TextFormatting.RED + "Player not found!"), UUID.randomUUID());
	        					return;
	        				}
	        				otherPlayerUUID = otherPlayerProfile.getId();
						}
						else
						{
							otherPlayerUUID = otherPlayer.getUUID();
						}
        				
						UIManager.openUIForcefully(player, GuiPagesHistory.getHistoryMenu(GTSDataManager.getAllListingHistoryPlayer(otherPlayerUUID),player, 1,otherPlayerUUID));
						return;
					}
					else
					{
    					player.sendMessage(new StringTextComponent(TextFormatting.RED + "No permission to view other player history!"), UUID.randomUUID());	
    					return;
					}
				}
				UIManager.openUIForcefully(player, GuiPagesHistory.getHistoryMenu(GTSDataManager.getAllListingHistoryPlayer(player.getUUID()),player, 1, null));
				return;
			}
			if(args[0].equals("items"))
			{
				UIManager.openUIForcefully(player, GuiPagesItems.getItemMenu(GTSDataManager.getGTSItemsListings(), player, 1, EnumSortingOption.None));
				return;
			}
			if(args[0].equals("help"))
			{
				sendCommandOptionHelp(player);
				return;
			}
			if(args[0].equals("sell"))
			{
				if(args.length == 1)
				{
					player.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /gts sell <pokemon/item>"), UUID.randomUUID());
					return;						
				}
				if(args[1].equals("pokemon"))
				{
					GTSSellPokemonSubCommand.handleSubCommand(player, args);
					return;
				}
				if(args[1].equals("item"))
				{
					GTSSellItemSubCommand.handleSubCommand(player, args);
					return;
				}
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid subcommand! Use /gts help"), UUID.randomUUID());
				return;
			}
			if(args[0].equals("showlisting"))
			{
				GTSShowListingSubcommand.handleSubCommand(player, args);
				return;
			}
			player.sendMessage(new StringTextComponent(TextFormatting.RED + "Invalid subcommand! Use /gts help"), UUID.randomUUID());
			return;
			
		}
		return;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args, BlockPos pos) throws CommandSyntaxException 
	{
		if(args.length == 1)
		{
			ArrayList<String> lstTabComplete = new ArrayList<>();
			lstTabComplete.add("items");
			lstTabComplete.add("sell");
			lstTabComplete.add("pokemon");
			lstTabComplete.add("help");
			lstTabComplete.add("manage");
			lstTabComplete.add("history");

			return lstTabComplete;
		}
		if(args.length == 2 && args[0].equals("history"))
		{
			if(PermissionAPI.hasPermission(sender.getPlayerOrException(),"simplygts.moderate"))
			{
				return PixelmonCommandUtils.tabCompleteUsernames();
			}
		}
		if(args.length == 2 && args[0].equals("sell"))
		{
			ArrayList<String> lstTabComplete = new ArrayList<>();
			lstTabComplete.add("item");
			lstTabComplete.add("pokemon");

			return lstTabComplete;
		}
		if(args.length == 3 && args[0].equals("sell"))
		{
			ArrayList<String> lstTabComplete = new ArrayList<>();
			if(args[1].equals("item"))
			{
				lstTabComplete.add("price");				
			}
			if(args[1].equals("pokemon"))
			{
				lstTabComplete.add("1");
				lstTabComplete.add("2");
				lstTabComplete.add("3");
				lstTabComplete.add("4");
				lstTabComplete.add("5");
				lstTabComplete.add("6");
			}

			return lstTabComplete;
		}
		if(args.length == 4 && args[0].equals("sell"))
		{
			ArrayList<String> lstTabComplete = new ArrayList<>();
			if(args[1].equals("pokemon"))
			{
				lstTabComplete.add("price");
			}

			return lstTabComplete;
		}
		
		return Collections.emptyList();
	}
	
	private void sendCommandOptionHelp(ServerPlayerEntity player)
	{

		player.sendMessage(new StringTextComponent(TextFormatting.YELLOW + "GTS Command Options:"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts " + TextFormatting.GOLD + "- Open Pokemon GTS window"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts pokemon " + TextFormatting.GOLD + "- Open Pokemon GTS window"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts items " + TextFormatting.GOLD + "- Open Items GTS window"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts manage " + TextFormatting.GOLD + "- Manage your GTS listings"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts history " + TextFormatting.GOLD + "- View GTS listing history"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts help " + TextFormatting.GOLD + "- Show this command help"), UUID.randomUUID());
		player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "/gts sell item/pokemon " + TextFormatting.GOLD + "- Sell items or Pokemon, price per item"), UUID.randomUUID());
	}
	
}
