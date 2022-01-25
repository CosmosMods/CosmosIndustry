package com.tcn.cosmosindustry.processing.client.container.slot;

import com.tcn.cosmosindustry.core.recipe.GrinderRecipes;

import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotGrinder extends Slot {
	
	private Player player;
	private int amount;

	public SlotGrinder(Player entity_player, Container inventory, int index, int x_pos, int y_pos) {
		super(inventory, index, x_pos, y_pos);
		this.player = entity_player;
	}

	@Override
	public boolean mayPlace(ItemStack par1ItemStack) {
		return false;
	}

	@Override
	public ItemStack remove(int par1) {
		if (this.hasItem()) {
			this.amount += Math.min(par1, this.getItem().getCount());
		}
		return super.remove(par1);
	}

	@Override
	public void onTake(Player par1EntityPlayer, ItemStack par2ItemStack) {
		this.checkTakeAchievements(par2ItemStack);
		super.onTake(par1EntityPlayer, par2ItemStack);
	}

	@Override
	protected void onQuickCraft(ItemStack par1ItemStack, int par2) {
		this.amount += par2;
		this.checkTakeAchievements(par1ItemStack);
	}

	@Override
	protected void checkTakeAchievements(ItemStack stack) {
		stack.onCraftedBy(this.player.level, this.player, this.amount);
		
		if (!this.player.level.isClientSide) {
			int i = this.amount;
			float f = GrinderRecipes.getInstance().getRecipeExperienceValue(stack);
			
			if (f == 0.0F) {
				i = 0;
			} else if (f < 1.0F) {
				int j = Mth.floor(i * f);
				if ((j < Mth.ceil(i * f)) && ((float) Math.random() < i * f - j)) {
					j++;
				}
				i = j;
			}
			
			while (i > 0) {
				int j = ExperienceOrb.getExperienceValue(i);
				i -= j;
				this.player.level.addFreshEntity(new ExperienceOrb(this.player.level, this.player.position().x, this.player.position().y + 0.5D, this.player.position().z + 0.5D, j));
			}
		}
		this.amount = 0;
	}
}