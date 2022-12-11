package com.varijon.tinies.SimplyGTS.command;

import java.util.List;
import java.util.UUID;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.command.PixelCommand;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.PermissionAPI;

public class GTSReloadCommand extends PixelCommand {

	private List aliases;
	
	public GTSReloadCommand(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher, "gtsreload", "/gtsreload", 4);   
	}

	
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void execute(CommandSource sender, String[] args) throws CommandException, CommandSyntaxException
	{
		ServerPlayerEntity player = sender.getPlayerOrException();
//		if(PermissionAPI.hasPermission(player,"simplygts.reload"))
//		{
			if(GTSDataManager.loadConfiguration())
			{
				player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Configuration reloaded succesfully"), UUID.randomUUID());						
			}
			else
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Failed to reload configuration"), UUID.randomUUID());
			}
			if(GTSDataManager.loadStorage())
			{
				player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Storage reloaded succesfully"), UUID.randomUUID());						
			}
			else
			{
				player.sendMessage(new StringTextComponent(TextFormatting.RED + "Failed to reload storage"), UUID.randomUUID());
			}
			return;
//		}
//		else
//		{
//			player.sendMessage(new StringTextComponent(TextFormatting.RED + "You don't have permission to use this command"), UUID.randomUUID());
//			return;
//		}
//		if(args.length == 2)
//		{
//			if(args[0].equals(""))
//			{
//				
//			}
//		}
//		if(args.length == 4)
//		{
//			if(args[0].equals("addevent"))
//			{
//				ConfigManager.getEventConfigList().add(new EventConfig(args[1], args[2], args[3], new ArrayList<EventPokemon>()));
//				ConfigManager
//				ConfigManager.loadConfig();
//			}
//		}
	}


}
