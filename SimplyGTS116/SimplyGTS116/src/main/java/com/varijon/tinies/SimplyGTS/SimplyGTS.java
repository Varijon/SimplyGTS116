package com.varijon.tinies.SimplyGTS;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;
import com.varijon.tinies.SimplyGTS.command.GTSCommand;
import com.varijon.tinies.SimplyGTS.command.GTSReloadCommand;
import com.varijon.tinies.SimplyGTS.handler.GTSTicker;
import com.varijon.tinies.SimplyGTS.storage.GTSDataManager;

import net.minecraft.command.CommandSource;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

@Mod("simplygts")
public class SimplyGTS
{
	public static String MODID = "modid";
	public static String VERSION = "version";
	public static Logger logger = LogManager.getLogger();
	
	public SimplyGTS()
	{
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
		
	}
	
	@SubscribeEvent
    public void setup(FMLCommonSetupEvent event) 
    {
		GTSDataManager.loadConfiguration();
		GTSDataManager.loadStorage();
		MinecraftForge.EVENT_BUS.register(new GTSTicker());
	}
	
	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event)
	{
		PermissionAPI.registerNode("simplygts.moderate", DefaultPermissionLevel.OP, "Allow removing of other player listings");
//		PermissionAPI.registerNode("simplygts.reload", DefaultPermissionLevel.OP, "Reload GTS");
//		PermissionAPI.registerNode("simplygts.gts", DefaultPermissionLevel.OP, "Use the GTS");
	}

	@SubscribeEvent
	public void registerCommands(RegisterCommandsEvent event)
	{
		new GTSCommand(event.getDispatcher());
		new GTSReloadCommand(event.getDispatcher());
	}
}