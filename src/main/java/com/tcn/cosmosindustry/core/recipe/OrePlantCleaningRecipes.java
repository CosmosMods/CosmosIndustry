package com.tcn.cosmosindustry.core.recipe;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.tcn.cosmosindustry.CosmosIndustry;

import net.minecraft.world.item.ItemStack;

public class OrePlantCleaningRecipes {
	
	private static final OrePlantCleaningRecipes INSTANCE = new OrePlantCleaningRecipes();
	
	private final Map<ItemStack, ItemStack> recipe_list = Maps.<ItemStack, ItemStack>newHashMap();
	private final Map<ItemStack, Float> experience_list = Maps.<ItemStack, Float>newHashMap();

	public static OrePlantCleaningRecipes getInstance() {
		return INSTANCE;
	}

	private OrePlantCleaningRecipes() { }

	public void addRecipe(ItemStack inputStack, ItemStack outputStack, float experience) {
		if (this.getRecipeResult(inputStack) != ItemStack.EMPTY) {
			CosmosIndustry.CONSOLE.debug("Ignored conflicting recipe: [OrePlant : Cleaning] {" + inputStack + " = " + outputStack + "}");
			return;
		}
		this.recipe_list.put(inputStack, outputStack);
		this.experience_list.put(outputStack, Float.valueOf(experience));
	}

	public ItemStack getRecipeResult(ItemStack inputStack) {
		for (Entry<ItemStack, ItemStack> entry : this.recipe_list.entrySet()) {
			if (this.compareItemStacks(inputStack, (ItemStack) entry.getKey())) {
				return (ItemStack) entry.getValue();
			}
		}
		return ItemStack.EMPTY;
	}

	private boolean compareItemStacks(ItemStack inputStack, ItemStack outputStack) {
		return ItemStack.matches(inputStack, outputStack);
	}

	public Map<ItemStack, ItemStack> getRecipeList() {
		return this.recipe_list;
	}

	public float getRecipeExperienceValue(ItemStack outputStack) {
		for (Entry<ItemStack, Float> entry : this.experience_list.entrySet()) {
			if (this.compareItemStacks(outputStack, entry.getKey())) {
				return entry.getValue().floatValue();
			}
		}
		return 0.0F;
	}
}