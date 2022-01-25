package com.tcn.cosmosindustry.integration.jei;

import java.util.Collection;
import java.util.List;

import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

public class IndustryRecipes {
	private final RecipeManager recipeManager;

	public IndustryRecipes() {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel world = minecraft.level;
		this.recipeManager = world.getRecipeManager();
	}
	
	public <C extends Container, T extends Recipe<C>> List<T> getRecipes(IRecipeCategory<T> stationCategory, RecipeType<T> recipeType) {
		return (List<T>) getRecipes(recipeManager, recipeType);
	}
	
	private static <C extends Container, T extends Recipe<C>> Collection<T> getRecipes(RecipeManager recipeManager, RecipeType<T> recipeType) {
		List<T> recipes = recipeManager.getAllRecipesFor(recipeType);
		return (Collection<T>) recipes;
	}
}