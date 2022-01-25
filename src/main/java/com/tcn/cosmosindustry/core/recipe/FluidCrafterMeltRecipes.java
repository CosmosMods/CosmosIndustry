package com.tcn.cosmosindustry.core.recipe;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.tcn.cosmosindustry.CosmosIndustry;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidCrafterMeltRecipes {
	
	private static final FluidCrafterMeltRecipes INSTANCE = new FluidCrafterMeltRecipes();
	
	private final Map<ItemStack, FluidStack> recipe_list = Maps.<ItemStack, FluidStack>newHashMap();

	public static FluidCrafterMeltRecipes getInstance() {
		return INSTANCE;
	}

	private FluidCrafterMeltRecipes() { }
	
	public void addRecipe(ItemStack input, FluidStack output) {
		if (getRecipeResult(input) != null) {
			CosmosIndustry.CONSOLE.debug("Ignored conflicting recipe: [FluidCrafter : Melt] {" + input + " = " + output + "}");
			return;
		}
		this.recipe_list.put(input, output);
	}

	public FluidStack getRecipeResult(ItemStack stack) {
		for (Entry<ItemStack, FluidStack> entry : this.recipe_list.entrySet()) {
			if (this.compareItemStacks(stack, (ItemStack) entry.getKey())) {
				return (FluidStack) entry.getValue();
			}
		}
		return null;
	}

	private boolean compareItemStacks(ItemStack inputStack, ItemStack outputStack) {
		return ItemStack.matches(inputStack, outputStack);
	}

	public Map<ItemStack, FluidStack> getRecipeList() {
		return this.recipe_list;
	}
}