package com.tcn.cosmosindustry;

import com.tcn.cosmosindustry.core.management.CoreConfigurationManager;
import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.core.management.RecipeManager;
import com.tcn.cosmoslibrary.common.runtime.CosmosConsoleManager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CosmosIndustry.MOD_ID)
public class CosmosIndustry {
	
	//This must NEVER EVER CHANGE!
	public static final String MOD_ID = "cosmosindustry";

	public static final CosmosConsoleManager CONSOLE = new CosmosConsoleManager(CosmosIndustry.MOD_ID, true, true);//ConfigurationManager.getInstance().getDebugMessage(), ConfigurationManager.getInstance().getInfoMessage());
	
	public CosmosIndustry() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLCommonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLClientSetup);
		MinecraftForge.EVENT_BUS.register(this);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CoreConfigurationManager.spec, "cosmosindustry-common.toml");
	}

	public void onFMLCommonSetup(final FMLCommonSetupEvent event) {
		//CoreEventManager.registerOresForGeneration();
		
		//CoreNetworkManager.register();
		RecipeManager.initialization();
		
		CONSOLE.startup("CommonSetup complete.");
	}

	public void onFMLClientSetup(final FMLClientSetupEvent event) {
		final ModLoadingContext context = ModLoadingContext.get();
		
		IndustryModBusManager.registerClient(context);
		IndustryModBusManager.onFMLClientSetup(event);
		
		CONSOLE.startup("ClientSetup complete.");
	}
}