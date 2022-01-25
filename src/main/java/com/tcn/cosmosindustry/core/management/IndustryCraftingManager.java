package com.tcn.cosmosindustry.core.management;

import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.core.crafting.GrinderRecipe;
import com.tcn.cosmosindustry.core.crafting.SeparatorRecipe;
import com.tcn.cosmoslibrary.common.runtime.CosmosRuntimeHelper;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber(modid = CosmosIndustry.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class IndustryCraftingManager {

	public static final RecipeSerializer<GrinderRecipe> RECIPE_SERIALIZER_GRINDING = new GrinderRecipe.Serializer();
	public static final RecipeType<GrinderRecipe> RECIPE_TYPE_GRINDING = RecipeType.register(CosmosIndustry.MOD_ID + ":grinding");

	public static final RecipeSerializer<SeparatorRecipe> RECIPE_SERIALIZER_SEPARATING = new SeparatorRecipe.Serializer();
	public static final RecipeType<SeparatorRecipe> RECIPE_TYPE_SEPARATING = RecipeType.register(CosmosIndustry.MOD_ID + ":separating");

	@SubscribeEvent
	public static void onRecipeSerializerRegistry(final RegistryEvent.Register<RecipeSerializer<?>> event) {
		event.getRegistry().registerAll(
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "grinding", RECIPE_SERIALIZER_GRINDING),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "separating", RECIPE_SERIALIZER_SEPARATING)
		);
		
		CosmosIndustry.CONSOLE.startup("RecipeSerializer<?> Registration complete.");
	}
}