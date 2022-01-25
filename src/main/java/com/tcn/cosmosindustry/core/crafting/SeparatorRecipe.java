package com.tcn.cosmosindustry.core.crafting;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.tcn.cosmosindustry.core.management.IndustryCraftingManager;
import com.tcn.cosmosindustry.core.management.IndustryModBusManager;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SeparatorRecipe implements Recipe<Container> {
	public final Ingredient input;
	public final ItemStack result;
	public final ItemStack secondaryOutput;
	private final ResourceLocation id;

	public SeparatorRecipe(ResourceLocation idIn, Ingredient inputIn, ItemStack resultIn, ItemStack secondaryOutputIn) {
		this.id = idIn;
		this.input = inputIn;
		this.result = resultIn;
		this.secondaryOutput = secondaryOutputIn;
	}

	@Override
	public boolean matches(Container containerIn, Level levelIn) {
		boolean flagInput = this.input.test(containerIn.getItem(0));
		
		return flagInput;
	}

	@Override
	public ItemStack assemble(Container containerIn) {
		ItemStack itemstack = this.result.copy();
		CompoundTag compoundtag = containerIn.getItem(0).getTag();
		
		if (compoundtag != null) {
			itemstack.setTag(compoundtag.copy());
		}

		return itemstack;
	}

	@Override
	public boolean canCraftInDimensions(int xIn, int yIn) {
		return xIn * yIn >= 2;
	}

	@Override
	public ItemStack getResultItem() {
		return this.result;
	}
	
	public ItemStack getSecondaryResultItem() {
		return this.secondaryOutput;
	}

	public boolean isInputIngredient(ItemStack stackIn) {
		return this.input.test(stackIn);
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(IndustryModBusManager.BLOCK_SEPARATOR);
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return IndustryCraftingManager.RECIPE_SERIALIZER_SEPARATING;
	}

	@Override
	public RecipeType<?> getType() {
		return IndustryCraftingManager.RECIPE_TYPE_SEPARATING;
	}
	
	@Override
	public boolean isIncomplete() {
		return Stream.of(this.input).anyMatch((ingredient) -> {
			return ingredient.getItems().length == 0;
		});
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(this.input);
	}
	
	public ArrayList<ItemStack> getOutputs() {
		ArrayList<ItemStack> array = new ArrayList<>();
		
		array.add(this.result);
		
		if (!this.secondaryOutput.isEmpty()) {
			array.add(this.secondaryOutput);
		}
		
		return array;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<SeparatorRecipe> {
		
		@Override
		public SeparatorRecipe fromJson(ResourceLocation idIn, JsonObject recipeIn) {
			Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(recipeIn, "input"));
			ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(recipeIn, "result"));
			
			ItemStack secondaryResult = ItemStack.EMPTY;
			
			if (recipeIn.has("secondaryResult")) {
				secondaryResult = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(recipeIn, "secondaryResult"));
			}
			
			return new SeparatorRecipe(idIn, input, result, secondaryResult);
		}

		@Override
		public SeparatorRecipe fromNetwork(ResourceLocation idIn, FriendlyByteBuf extraDataIn) {
			Ingredient input = Ingredient.fromNetwork(extraDataIn);
			ItemStack result = extraDataIn.readItem();
			ItemStack secondaryResult = extraDataIn.readItem();

			return new SeparatorRecipe(idIn, input, result, secondaryResult);
		}

		@Override
		public void toNetwork(FriendlyByteBuf extraDataIn, SeparatorRecipe recipeIn) {
			recipeIn.input.toNetwork(extraDataIn);
			extraDataIn.writeItem(recipeIn.result);
			extraDataIn.writeItem(recipeIn.secondaryOutput);
		}
	}
}