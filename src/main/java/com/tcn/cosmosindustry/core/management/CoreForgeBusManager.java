package com.tcn.cosmosindustry.core.management;

import com.tcn.cosmosindustry.CosmosIndustry;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CosmosIndustry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoreForgeBusManager {
	
	@SubscribeEvent
	public static void onServerAboutToStart(final ServerAboutToStartEvent event) {
		CosmosIndustry.CONSOLE.startup("[FMLServerAboutToStartEvent] Server about to start...");
	}

	@SubscribeEvent
	public static void onServerStarting(final ServerStartingEvent event) {
		CosmosIndustry.CONSOLE.startup("[FMLServerStartingEvent] Server starting...");
	}

	@SubscribeEvent
	public static void onServerStarted(final ServerStartedEvent event) {
		CosmosIndustry.CONSOLE.startup("[FMLServerStartedEvent] Server started...");
	}

	@SubscribeEvent
	public static void onServerStopping(final ServerStoppingEvent event) {
		CosmosIndustry.CONSOLE.shutdown("[FMLServerStoppingEvent] Server stopping...");
	}
}