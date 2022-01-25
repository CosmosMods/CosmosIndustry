package com.tcn.cosmosindustry.processing.client.container;

import com.tcn.cosmosindustry.core.management.IndustryCraftingManager;
import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.processing.client.container.slot.SlotSeparator;
import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.cosmoslibrary.client.container.slot.SlotUpgrade;
import com.tcn.cosmoslibrary.common.item.CosmosItemUpgradeEnergy;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerSeparator extends CosmosContainerMenuBlockEntity {
	
	public ContainerSeparator(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, new SimpleContainer(6), ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerSeparator(int indexIn, Inventory playerInventoryIn, Container tile, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(IndustryModBusManager.CONTAINER_TYPE_SEPARATOR, indexIn, playerInventoryIn, accessIn, posIn);
		
		//Input Slot
		this.addSlot(new Slot(tile, 0, 104, 18));
		
		//Output Slot
		this.addSlot(new SlotSeparator(player, tile, 1, 104, 58));

		//Secondary Output Slot
		this.addSlot(new SlotSeparator(player, tile, 2, 147, 50));

		/**@Upgradeslots*/
		this.addSlot(new SlotUpgrade(tile, 3, 56, 18, IndustryModBusManager.UPGRADE_SPEED));
		this.addSlot(new SlotUpgrade(tile, 4, 56, 38, IndustryModBusManager.UPGRADE_CAPACITY));
		this.addSlot(new SlotUpgrade(tile, 5, 56, 58, IndustryModBusManager.UPGRADE_EFFICIENCY));
		
		/**@Inventory*/
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlot(new Slot(playerInventoryIn, 9 + x + y * 9, 8 + x * 18, 95 + y * 18));
			}
		}
		
		/**@Actionbar*/
		for (int x = 0; x < 9; x++) {
			this.addSlot(new Slot(playerInventoryIn, x, 8 + x * 18, 153));
		}
	}

	@Override
	public void addSlotListener(ContainerListener listenerIn) {
		super.addSlotListener(listenerIn);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void removeSlotListener(ContainerListener listenerIn) {
		super.removeSlotListener(listenerIn);
	}

	@Override
	public void slotsChanged(Container inventoryIn) {
		super.slotsChanged(inventoryIn);
		this.broadcastChanges();
	}
	
	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(this.access, playerIn, IndustryModBusManager.BLOCK_SEPARATOR);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			if (indexIn >= 0 && indexIn < 6 ) {
				if (!this.moveItemStackTo(itemstack1, 5, this.slots.size() - 9, false)) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (indexIn >= 6 && indexIn < this.slots.size()) {
				if (itemstack.getItem() instanceof CosmosItemUpgradeEnergy) {
					if (!this.moveItemStackTo(itemstack1, 2, 6, false)) {
						if (indexIn < this.slots.size() - 9) {
							if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
								return ItemStack.EMPTY;
							}
						} else if (!this.moveItemStackTo(itemstack1, 5, this.slots.size() - 9, false)) {
							return ItemStack.EMPTY;
						}
					}
				}
				
				else if (this.getLevel().getRecipeManager().getRecipeFor(IndustryCraftingManager.RECIPE_TYPE_SEPARATING, new SimpleContainer(itemstack), getLevel()).isPresent()) {
					if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				
				else if (indexIn < this.slots.size() - 9) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
						return ItemStack.EMPTY;
					}
				} else {
					if (!this.moveItemStackTo(itemstack1, 6, this.slots.size() - 9, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
			
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return itemstack != null ? itemstack : ItemStack.EMPTY;
	}
	
}