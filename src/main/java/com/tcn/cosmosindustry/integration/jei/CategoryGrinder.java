package com.tcn.cosmosindustry.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmosindustry.IndustryReference.JEI;
import com.tcn.cosmosindustry.IndustryReference.RESOURCE.PROCESSING;
import com.tcn.cosmosindustry.core.crafting.GrinderRecipe;
import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmoslibrary.CosmosReference;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CategoryGrinder implements IRecipeCategory<GrinderRecipe> {

	private IGuiHelper helper;
	private final IDrawable background;
	
	private final IDrawable process;
	private final IDrawable stored;
	
	public CategoryGrinder(IGuiHelper helperIn) {
		this.helper = helperIn;
		
		this.background = this.helper.createDrawable(PROCESSING.GRINDER_LOC_GUI_JEI, 0, 0, 112, 66);

		IDrawableStatic process_draw = helper.createDrawable(PROCESSING.GRINDER_LOC_GUI, 176, 0, 16, 16);
		IDrawableStatic stored_draw = helper.createDrawable(CosmosReference.RESOURCE.BASE.UI_ENERGY_VERTICAL, 0, 0, 16, 60);
		
		this.process = this.helper.createAnimatedDrawable(process_draw, 100, IDrawableAnimated.StartDirection.TOP, false);
		this.stored = this.helper.createAnimatedDrawable(stored_draw, 200, IDrawableAnimated.StartDirection.TOP, true);
	}
	
	@Override
	public ResourceLocation getUid() {
		return JEI.GRINDER_UID;
	}

	@Override
	public Class<? extends GrinderRecipe> getRecipeClass() {
		return GrinderRecipe.class;
	}

	@Override
	public Component getTitle() {
		return ComponentHelper.locComp(ComponentColour.WHITE, false, "cosmosindustry.integration.jei.grinder_category");
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(IndustryModBusManager.BLOCK_GRINDER));
	}
	
	@Override
	public void draw(GrinderRecipe recipe, PoseStack stack, double mouseX, double mouseY) {
		this.process.draw(stack, 53, 25);
		
		CosmosUISystem.setTextureColour(ComponentColour.RED);
		this.stored.draw(stack, 29, 3);
	}
	
	@Override
	public void setIngredients(GrinderRecipe recipe, IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, recipe.input.getItems()[0]);
		ingredients.setOutputs(VanillaTypes.ITEM, recipe.getOutputs());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, GrinderRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		
		stacks.init(0, true, 52, 4);
		stacks.init(1, false, 52, 44);
		
		if (!(recipe.secondaryOutput.isEmpty())) {
			stacks.init(2, false, 86, 40);
		}
		
		stacks.set(ingredients);
	}
}