package com.tcn.cosmosindustry.integration.jei;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.IndustryReference.JEI;
import com.tcn.cosmosindustry.core.crafting.GrinderRecipe;
import com.tcn.cosmosindustry.core.crafting.SeparatorRecipe;
import com.tcn.cosmosindustry.core.management.IndustryCraftingManager;
import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.processing.client.container.ContainerGrinder;
import com.tcn.cosmosindustry.processing.client.container.ContainerSeparator;
import com.tcn.cosmosindustry.processing.client.screen.ScreenGrinder;
import com.tcn.cosmosindustry.processing.client.screen.ScreenKiln;
import com.tcn.cosmosindustry.processing.client.screen.ScreenSeparator;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class IndustryJEIPlugin implements IModPlugin {

	@Nullable
	private IRecipeCategory<GrinderRecipe> grinderRecipeCategory;
	@Nullable
	private IRecipeCategory<SeparatorRecipe> separatorRecipeCategory;
	
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(CosmosIndustry.MOD_ID, "integration_jei");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		
		registration.addRecipeCategories(grinderRecipeCategory = new CategoryGrinder(guiHelper));
		registration.addRecipeCategories(separatorRecipeCategory = new CategorySeparator(guiHelper));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		IndustryRecipes recipes = new IndustryRecipes();
		
		registration.addRecipes(recipes.getRecipes(grinderRecipeCategory, IndustryCraftingManager.RECIPE_TYPE_GRINDING), JEI.GRINDER_UID);
		registration.addRecipes(recipes.getRecipes(separatorRecipeCategory, IndustryCraftingManager.RECIPE_TYPE_SEPARATING), JEI.SEPARATOR_UID);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(ContainerGrinder.class, JEI.GRINDER_UID, 0, 1, 6, 32);

		registration.addRecipeTransferHandler(ContainerSeparator.class, JEI.SEPARATOR_UID, 0, 1, 6, 32);
		
	}
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(IndustryModBusManager.BLOCK_GRINDER), JEI.GRINDER_UID);
		registration.addRecipeCatalyst(new ItemStack(IndustryModBusManager.BLOCK_SEPARATOR), JEI.SEPARATOR_UID);
		
		registration.addRecipeCatalyst(new ItemStack(IndustryModBusManager.BLOCK_KILN), VanillaRecipeCategoryUid.FURNACE);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(ScreenGrinder.class, 104, 38, 16, 16, JEI.GRINDER_UID);
		registration.addRecipeClickArea(ScreenSeparator.class, 104, 38, 16, 16, JEI.SEPARATOR_UID);
		
		registration.addRecipeClickArea(ScreenKiln.class, 104, 38, 16, 16, VanillaRecipeCategoryUid.FURNACE);
	}
}