package com.tcn.cosmosindustry.core.management;

import com.tcn.cosmosindustry.core.recipe.CompactorRecipes;
import com.tcn.cosmosindustry.core.recipe.FluidCrafterCraftRecipes;
import com.tcn.cosmosindustry.core.recipe.FluidCrafterMeltRecipes;
import com.tcn.cosmosindustry.core.recipe.GrinderRecipes;
import com.tcn.cosmosindustry.core.recipe.OrePlantCleaningRecipes;
import com.tcn.cosmosindustry.core.recipe.OrePlantRefiningRecipes;
import com.tcn.cosmosindustry.core.recipe.SeparatorRecipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

public class RecipeManager {
		
	public static void initialization() {
		registerVanillaRecipes();
		registerModRecipes();
	}
	
	public static void registerVanillaRecipes() {
		/*GameRegistry.addSmelting(BlockHandler.BASE.BLOCK_ORE_COPPER, new ItemStack(CoreModBusManager.COPPER_INGOT), 1F);
		GameRegistry.addSmelting(CoreModBusManager.COPPER_DUST, new ItemStack(CoreModBusManager.COPPER_INGOT), 0.5F);
		GameRegistry.addSmelting(BlockHandler.BASE.BLOCK_ORE_TIN, new ItemStack(CoreModBusManager.TIN_INGOT), 1F);
		GameRegistry.addSmelting(CoreModBusManager.TIN_DUST, new ItemStack(CoreModBusManager.TIN_INGOT), 0.5F);
		GameRegistry.addSmelting(BlockHandler.BASE.BLOCK_ORE_SILVER, new ItemStack(CoreModBusManager.SILVER_INGOT), 1F);
		GameRegistry.addSmelting(CoreModBusManager.SILVER_DUST, new ItemStack(CoreModBusManager.SILVER_INGOT), 0.5F);
		
		GameRegistry.addSmelting(BlockHandler.BASE.BLOCK_ORE_SILICON, new ItemStack(CoreModBusManager.SILICON), 1F);
		GameRegistry.addSmelting(CoreModBusManager.BRONZE_DUST, new ItemStack(CoreModBusManager.BRONZE_INGOT), 0.5F);
		GameRegistry.addSmelting(CoreModBusManager.STEEL_INGOT_UNREFINED, new ItemStack(CoreModBusManager.STEEL_INGOT), 0.5F);
		GameRegistry.addSmelting(CoreModBusManager.IRON_DUST, new ItemStack(Items.IRON_INGOT), 0.5F);
		GameRegistry.addSmelting(CoreModBusManager.GOLD_DUST, new ItemStack(Items.GOLD_INGOT), 0.5F);
		
		GameRegistry.addSmelting(new ItemStack(Items.DYE, 1, 0), new ItemStack(CoreModBusManager.RUBBER), 1F);
		GameRegistry.addSmelting(new ItemStack(CoreModBusManager.SILICON), new ItemStack(CoreModBusManager.SILICON_REFINED), 0.5F);
		//GameRegistry.addSmelting(new ItemStack(CoreModBusManager.CIRCUIT_ONE_RAW), new ItemStack(CoreModBusManager.CIRCUIT_ONE), 1f);
		//GameRegistry.addSmelting(new ItemStack(CoreModBusManager.CIRCUIT_THREE_RAW), new ItemStack(CoreModBusManager.CIRCUIT_THREE), 1f);*/
	}
	
	public static void registerModRecipes() {
		registerGrinderRecipe(new ItemStack(Blocks.COPPER_BLOCK),     new ItemStack(IndustryModBusManager.COPPER_DUST, 2),    new ItemStack(IndustryModBusManager.STONE_DUST, 2), 1F);
		registerGrinderRecipe(new ItemStack(IndustryModBusManager.BLOCK_ORE_TIN),     new ItemStack(IndustryModBusManager.TIN_DUST, 2),    new ItemStack(IndustryModBusManager.STONE_DUST, 2), 1F);
		registerGrinderRecipe(new ItemStack(IndustryModBusManager.BLOCK_ORE_SILVER),  new ItemStack(IndustryModBusManager.SILVER_DUST, 2), new ItemStack(IndustryModBusManager.STONE_DUST, 2), 1F);
		registerGrinderRecipe(new ItemStack(IndustryModBusManager.BLOCK_ORE_SILICON), new ItemStack(IndustryModBusManager.SILICON, 4),     new ItemStack(IndustryModBusManager.STONE_DUST, 2), 1.5F);
		
		registerGrinderRecipe(new ItemStack(Blocks.IRON_ORE),    new ItemStack(IndustryModBusManager.IRON_DUST, 2), new ItemStack(IndustryModBusManager.STONE_DUST, 2), 1F);
		registerGrinderRecipe(new ItemStack(Blocks.GOLD_ORE),    new ItemStack(IndustryModBusManager.GOLD_DUST, 2), new ItemStack(IndustryModBusManager.STONE_DUST, 2), 1F);
		registerGrinderRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.GRAVEL, 2),         new ItemStack(IndustryModBusManager.STONE_DUST, 2), 1F);
		registerGrinderRecipe(new ItemStack(Blocks.GRAVEL),      new ItemStack(Blocks.SAND, 2),           new ItemStack(IndustryModBusManager.STONE_DUST, 2), 1F);
		
		registerGrinderRecipe(new ItemStack(Items.COPPER_INGOT), new ItemStack(IndustryModBusManager.COPPER_DUST),  ItemStack.EMPTY, 0.5F);
		registerGrinderRecipe(new ItemStack(IndustryModBusManager.TIN_INGOT),    new ItemStack(IndustryModBusManager.TIN_DUST),     ItemStack.EMPTY, 0.5F);
		registerGrinderRecipe(new ItemStack(IndustryModBusManager.SILVER_INGOT), new ItemStack(IndustryModBusManager.SILVER_DUST),  ItemStack.EMPTY, 1F);
		registerGrinderRecipe(new ItemStack(IndustryModBusManager.BRONZE_INGOT), new ItemStack(IndustryModBusManager.BRONZE_DUST),  ItemStack.EMPTY, 0.5F);
		registerGrinderRecipe(new ItemStack(Items.DIAMOND),            new ItemStack(IndustryModBusManager.DIAMOND_DUST), ItemStack.EMPTY, 1F);
	
		registerCompactorRecipe(new ItemStack(Items.COPPER_INGOT), new ItemStack(IndustryModBusManager.COPPER_PLATE), 1F);
		registerCompactorRecipe(new ItemStack(IndustryModBusManager.TIN_INGOT),    new ItemStack(IndustryModBusManager.TIN_PLATE), 1F);
		registerCompactorRecipe(new ItemStack(IndustryModBusManager.SILVER_INGOT), new ItemStack(IndustryModBusManager.SILVER_PLATE), 1F);
		registerCompactorRecipe(new ItemStack(IndustryModBusManager.BRONZE_INGOT), new ItemStack(IndustryModBusManager.BRONZE_PLATE), 1F);
		registerCompactorRecipe(new ItemStack(IndustryModBusManager.BRASS_INGOT),  new ItemStack(IndustryModBusManager.BRASS_PLATE), 1F);
		registerCompactorRecipe(new ItemStack(IndustryModBusManager.STEEL_INGOT),  new ItemStack(IndustryModBusManager.STEEL_PLATE), 1F);
		
		registerCompactorRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(IndustryModBusManager.IRON_PLATE), 1F);
		registerCompactorRecipe(new ItemStack(Items.GOLD_INGOT), new ItemStack(IndustryModBusManager.GOLD_PLATE), 1F);
		registerCompactorRecipe(new ItemStack(Items.DIAMOND),    new ItemStack(IndustryModBusManager.DIAMOND_PLATE), 1.5F);
	
		//registerSeparatorRecipe(new ItemStack(Blocks.COAL_BLOCK), new ItemStack(Blocks.DIAMOND_BLOCK), 1.0F);
		registerSeparatorRecipe(new ItemStack(Blocks.IRON_ORE), new ItemStack(IndustryModBusManager.IRON_DUST, 4), new ItemStack(Blocks.COBBLESTONE, 2), 0.5F);
		
		registerOrePlantCleanRecipe(new ItemStack(IndustryModBusManager.IRON_DUST),         new ItemStack(IndustryModBusManager.IRON_DUST_REFINE), 1F);
		registerOrePlantCleanRecipe(new ItemStack(IndustryModBusManager.GOLD_DUST),         new ItemStack(IndustryModBusManager.GOLD_DUST_REFINE), 1F);
		
		registerOrePlantRefineRecipe(new ItemStack(IndustryModBusManager.IRON_DUST_REFINE), new ItemStack(Items.IRON_INGOT), 1F);
		registerOrePlantRefineRecipe(new ItemStack(IndustryModBusManager.GOLD_DUST_REFINE), new ItemStack(Items.GOLD_INGOT), 1F);

		registerFluidCrafterMeltRecipe(new ItemStack(Blocks.DIRT),    new FluidStack(Fluids.WATER, 1000));
		//registerFluidCrafterMeltRecipe(new ItemStack(Items.REDSTONE), new FluidStack(FluidRegistry.getFluid("energized_redstone"), 500));
	}
	
	public static void registerGrinderRecipe(ItemStack input, ItemStack output, ItemStack second, float xp) {
		GrinderRecipes.getInstance().addRecipe(input, output, second, xp);
	}

	public static void registerSeparatorRecipe(ItemStack input, ItemStack output, ItemStack secondary, float xp) {
		SeparatorRecipes.getInstance().addRecipe(input, output, secondary, xp);
	}
	
	public static void registerCompactorRecipe(ItemStack input, ItemStack output, float xp) {
		CompactorRecipes.getInstance().addRecipe(input, output, xp);
	}
	
	public static void registerOrePlantCleanRecipe(ItemStack input, ItemStack output, float xp) {
		OrePlantCleaningRecipes.getInstance().addRecipe(input, output, xp);
	}
	
	public static void registerOrePlantRefineRecipe(ItemStack input, ItemStack output, float xp) {
		OrePlantRefiningRecipes.getInstance().addRecipe(input, output, xp);
	}
	
	public static void registerFluidCrafterMeltRecipe(ItemStack input, FluidStack output) {
		FluidCrafterMeltRecipes.getInstance().addRecipe(input, output);
	}

	public static void registerFluidCrafterCraftRecipe(ItemStack input, ItemStack output, Fluid fluid, float xp) {
		FluidCrafterCraftRecipes.getInstance().addRecipe(input, output, fluid, xp);
	}
}