package com.tcn.cosmosindustry.core.management;

import java.util.List;

import com.google.common.collect.Lists;
import com.tcn.cosmoslibrary.client.enums.EnumBERColour;
import com.tcn.cosmoslibrary.common.interfaces.IMultiRecipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class SynthesiserRecipeManager {

	private final boolean TEST_RECIPES_ENABLED = true;

	private static final SynthesiserRecipeManager INSTANCE = new SynthesiserRecipeManager();
	private final List<IMultiRecipe> recipes = Lists.<IMultiRecipe>newArrayList();

	public static SynthesiserRecipeManager getInstance() {
		return INSTANCE;
	}
	
	private SynthesiserRecipeManager() {
		this.addAllRecipes();
		
		if (this.TEST_RECIPES_ENABLED == true) {
			this.testRecipes();
		}
	}
	
	/**
	 * ONLY -2-, -4- or -8- inputs are allowed.
	 */
	public void addAllRecipes() {
		this.addRecipe(new ItemStack(IndustryModBusManager.UPGRADE_SPEED), new ItemStack(IndustryModBusManager.UPGRADE_BASE),
			160, EnumBERColour.YELLOW, new Object[] {
			new ItemStack(Blocks.GLOWSTONE), new ItemStack(Blocks.GLOWSTONE),
			new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(Blocks.REDSTONE_BLOCK),
			new ItemStack(IndustryModBusManager.ENERGY_WAFER), new ItemStack(IndustryModBusManager.ENERGY_WAFER),
			new ItemStack(IndustryModBusManager.CIRCUIT), new ItemStack(IndustryModBusManager.CIRCUIT)
		});
		
		this.addRecipe(new ItemStack(IndustryModBusManager.UPGRADE_FLUID_SPEED), new ItemStack(IndustryModBusManager.UPGRADE_SPEED),
			160, EnumBERColour.RED, new Object[] {
			new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(Blocks.REDSTONE_BLOCK),
			new ItemStack(Blocks.GLOWSTONE), new ItemStack(Blocks.GLOWSTONE),
			new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.WATER_BUCKET),
			new ItemStack(IndustryModBusManager.CIRCUIT), new ItemStack(IndustryModBusManager.CIRCUIT)
		});
		
		this.addRecipe(new ItemStack(IndustryModBusManager.CIRCUIT), new ItemStack(IndustryModBusManager.CIRCUIT_RAW), 
			120, EnumBERColour.PURPLE, new Object[] { 						
			new ItemStack(IndustryModBusManager.GOLD_DUST), new ItemStack(IndustryModBusManager.GOLD_DUST),
			new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE),
			new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE),
			new ItemStack(IndustryModBusManager.ENERGY_WAFER), new ItemStack(IndustryModBusManager.ENERGY_WAFER)
		});
		
		this.addRecipe(new ItemStack(IndustryModBusManager.CIRCUIT_ADVANCED), new ItemStack(IndustryModBusManager.CIRCUIT_ADVANCED_RAW),
			200, EnumBERColour.ORANGE, new Object[] { 
			new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(Blocks.REDSTONE_BLOCK),
			new ItemStack(IndustryModBusManager.CIRCUIT), new ItemStack(IndustryModBusManager.CIRCUIT),
			new ItemStack(Items.DIAMOND), new ItemStack(Items.DIAMOND),
			new ItemStack(IndustryModBusManager.ENERGY_WAFER), new ItemStack(IndustryModBusManager.ENERGY_WAFER)
		});
		
		this.addRecipe(new ItemStack(IndustryModBusManager.ENERGY_WAFER), new ItemStack(IndustryModBusManager.SILICON_WAFER),
			100, EnumBERColour.PURPLE, new Object[] { 
			new ItemStack(IndustryModBusManager.ENERGY_DUST), new ItemStack(IndustryModBusManager.ENERGY_DUST),
			new ItemStack(IndustryModBusManager.ENERGY_INGOT), new ItemStack(IndustryModBusManager.ENERGY_INGOT)
		});
		
		this.addRecipe(new ItemStack(IndustryModBusManager.SILICON_WAFER), new ItemStack(IndustryModBusManager.SILICON_INGOT),
			60, EnumBERColour.GRAY, new Object[] {
			new ItemStack(IndustryModBusManager.SILICON), new ItemStack(IndustryModBusManager.SILICON),
			new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE)
		});
		
		this.addRecipe(new ItemStack(IndustryModBusManager.ENERGY_INGOT), new ItemStack(Items.IRON_INGOT),
			80, EnumBERColour.PURPLE, new Object[] { 
			new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE),
			new ItemStack(IndustryModBusManager.ENERGY_DUST), new ItemStack(IndustryModBusManager.ENERGY_DUST)
		});
		
		this.addRecipe(new ItemStack(IndustryModBusManager.ENERGY_DUST), new ItemStack(Items.GLOWSTONE_DUST),
			40, EnumBERColour.PURPLE, new Object[] { 
			new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE),
			new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE)
		});
		this.addRecipe(new ItemStack(IndustryModBusManager.BLOCK_STRUCTURE), new ItemStack(Blocks.IRON_BLOCK),
			140, EnumBERColour.GRAY, new Object[] {
			new ItemStack(IndustryModBusManager.CIRCUIT), new ItemStack(IndustryModBusManager.CIRCUIT),
			new ItemStack(IndustryModBusManager.ENERGY_INGOT), new ItemStack(IndustryModBusManager.ENERGY_INGOT)
		});
	}

	public void testRecipes() {
		this.addRecipe(new ItemStack(Items.DIAMOND), new ItemStack(Items.COAL),
			60, EnumBERColour.GRAY, new Object[] {
			new ItemStack(Blocks.DIRT), new ItemStack(Blocks.DIRT)
		});
	}

	public void addRecipe(ItemStack output, ItemStack focus, Integer process_time, EnumBERColour colour, Object... inputs) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		if (inputs.length > 8) {
			throw new IllegalArgumentException("Invalid Synthesiser recipe: out of bounds exception [Recipe ingredient list is more than 8]!");
		}
		
		
		if (!(inputs.length == 2) && !(inputs.length == 4) && !(inputs.length == 8)) {
			throw new IllegalArgumentException("Invalid Synthesiser recipe: incorrect input count [Recipe ingredients list contains an incorrect number of ingredients]!");
		}
		
		for (Object object : inputs) {
			if (object instanceof ItemStack) {
				list.add(((ItemStack) object).copy());
			} else if (object instanceof Item) {
				list.add(new ItemStack((Item) object));
			} else if (object instanceof Block) {
				list.add(new ItemStack((Block) object));
			} else if (!(object instanceof Block)) {
				throw new IllegalArgumentException("Invalid Synthesiser recipe: unknown type [" + object.getClass().getName() + ", Unknown ingredient type]!");
			}
		}
		
		this.recipes.add(new SynthesiserRecipe(output, list, focus, process_time, colour));
	}

	public void addRecipe(IMultiRecipe recipe) {
		this.recipes.add(recipe);
	}

	public ItemStack findMatchingRecipe(List<ItemStack> list) {
		for (IMultiRecipe irecipe : this.recipes) {
			if (irecipe.matches(list)) {
				return irecipe.getOutput();
			}
		}
		return ItemStack.EMPTY;
	}

	public ItemStack findFocusStack(List<ItemStack> list) {
		for (IMultiRecipe irecipe : this.recipes) {
			if (irecipe.matches(list)) {
				return irecipe.getFocusStack();
			}
		}
		return ItemStack.EMPTY;
	}

	public NonNullList<ItemStack> getRemainingItems(List<ItemStack> list) {
		for (IMultiRecipe irecipe : this.recipes) {
			if (irecipe.matches(list)) {
				return irecipe.getRemainingItems(list);
			}
		}
		
		NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(list.size(), ItemStack.EMPTY);
		
		for (int i = 0; i < nonnulllist.size(); ++i) {
			nonnulllist.set(i, list.get(i));
		}
		return nonnulllist;
	}

	public List<IMultiRecipe> getRecipeList() {
		return this.recipes;
	}

	public Integer findProcessTime(List<ItemStack> list) {
		for (IMultiRecipe irecipe : this.recipes) {
			if (irecipe.matches(list)) {
				return irecipe.getProcessTime();
			}
		}
		return 0;
	}

	public EnumBERColour findColour(List<ItemStack> list) {
		for (IMultiRecipe irecipe : this.recipes) {
			if (irecipe.matches(list)) {
				return irecipe.getColour();
			}
		}
		return EnumBERColour.WHITE;
	}

	public class SynthesiserRecipe implements IMultiRecipe {

		/**
		 * ItemStack output of the recipe.
		 */
		private final ItemStack STACK_OUTPUT;

		/**
		 * List<ItemStack> of ingredients.
		 */
		public final List<ItemStack> INPUT_STACKS;

		/**
		 * ItemStack centre stack in the synthesiser.
		 */
		private final ItemStack STACK_FOCUS;

		/**
		 * int time it takes to complete the recipe.
		 */
		private final Integer PROCESS_TIME;

		/**
		 * {@link EnumTESRColour} colour of the beam.
		 */
		private final EnumBERColour COLOUR;

		public SynthesiserRecipe(ItemStack output, List<ItemStack> inputs, ItemStack focus, Integer process_time, EnumBERColour colour) {
			this.STACK_OUTPUT = output;
			this.INPUT_STACKS = inputs;
			this.STACK_FOCUS = focus;
			this.PROCESS_TIME = process_time;
			this.COLOUR = colour;
		}

		@Override
		public ItemStack getOutput() {
			return this.STACK_OUTPUT;
		}

		@Override
		public NonNullList<ItemStack> getRemainingItems(List<ItemStack> stack) {
			NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(stack.size(), ItemStack.EMPTY);

			for (int i = 0; i < nonnulllist.size(); ++i) {
				ItemStack itemstack = stack.get(i);
				nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
			}

			return nonnulllist;
		}

		/**
		 * Used to check if a recipe matches current center + pedestal stacks.
		 */
		@Override
		public boolean matches(List<ItemStack> stack) {
			List<ItemStack> list = Lists.newArrayList(this.INPUT_STACKS);

			for (int j = 0; j < stack.size(); ++j) {
				ItemStack itemstack = stack.get(j);

				if (!itemstack.isEmpty()) {
					boolean flag = false;

					for (ItemStack itemstack1 : list) {
						if (ItemStack.matches(itemstack, itemstack1)) {
							flag = true;
							list.remove(itemstack1);
							break;
						}
					}

					if (!flag) {
						return false;
					}
				}
			}
			
			return list.isEmpty();
		}

		@Override
		public ItemStack getResult() {
			return this.STACK_OUTPUT.copy();
		}

		@Override
		public int getRecipeSize() {
			return this.INPUT_STACKS.size();
		}

		@Override
		public List<ItemStack> getRecipeInput() {
			return this.INPUT_STACKS;
		}

		@Override
		public ItemStack getFocusStack() {
			return STACK_FOCUS;
		}

		@Override
		public Integer getProcessTime() {
			return PROCESS_TIME;
		}

		@Override
		public EnumBERColour getColour() {
			return COLOUR;
		}
	}
}