package com.tcn.cosmosindustry.core.recipe;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.tcn.cosmosindustry.CosmosIndustry;

import net.minecraft.world.item.ItemStack;

public class LaserCutterRecipes {
	
	private static final LaserCutterRecipes INSTANCE = new LaserCutterRecipes();
	
	private final Map<ItemStack, ItemStack> recipe_list = Maps.<ItemStack, ItemStack>newHashMap();
	private final Map<ItemStack, ItemStack> secondary_list = Maps.<ItemStack, ItemStack>newHashMap();
	private final Map<ItemStack, Float> experience_list = Maps.<ItemStack, Float>newHashMap();

	public static LaserCutterRecipes getInstance() {
		return INSTANCE;
	}

	private LaserCutterRecipes() { }

	public void addRecipe(ItemStack inputStack, ItemStack outputStack, ItemStack secondaryStack, float experience) {
		if (this.getRecipeResult(inputStack) != ItemStack.EMPTY) {
			CosmosIndustry.CONSOLE.debug("Ignored conflicting recipe: [Laser Cutting] {" + inputStack + " = " + outputStack + "}");
			return;
		}
		
		this.recipe_list.put(inputStack, outputStack);
		this.secondary_list.put(inputStack, secondaryStack);
		this.experience_list.put(outputStack, experience);
	}

	public ItemStack getRecipeResult(ItemStack inputStack) {
		for (Entry<ItemStack, ItemStack> entry : this.recipe_list.entrySet()) {
			if (this.compareItemStacks(inputStack, entry.getKey())) {
				return entry.getValue();
			}
		}
		return ItemStack.EMPTY;
	}
	
	public ItemStack getSecondaryResult(ItemStack inputStack) {
		for (Entry<ItemStack, ItemStack> entry : this.secondary_list.entrySet()) {
			if (this.compareItemStacks(inputStack, entry.getKey())) {
				return entry.getValue();
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
	
	public Map<ItemStack, ItemStack> getSecondaryList(){
		return this.secondary_list;
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