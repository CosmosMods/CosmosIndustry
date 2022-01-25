package com.tcn.cosmosindustry.processing.client.container;

import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;

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

public class ContainerSynthesiserStand extends CosmosContainerMenuBlockEntity {

	public ContainerSynthesiserStand(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, new SimpleContainer(6), ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerSynthesiserStand(int indexIn, Inventory playerInventoryIn, Container tile, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(IndustryModBusManager.CONTAINER_TYPE_SYNTHESISER_STAND, indexIn, playerInventoryIn, accessIn, posIn);
		
		/**@Inputslot / @OutputSlot*/
		this.addSlot(new Slot(tile, 0, 80, 60));
		
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
		//this.access.execute((worldIn, posIn) -> { this.clearContainer(playerIn, worldIn, this.craftSlots); });
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(this.access, playerIn, IndustryModBusManager.BLOCK_SYNTHESISER_STAND);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn >= 0 && indexIn < 10) {
				//this.access.execute((worldIn, posIn) -> { itemstack1.getItem().onCraftedBy(itemstack1, worldIn, playerIn); });
				
				if (itemstack.getItem() instanceof CosmosEnergyItem) {
					if (!this.moveItemStackTo(itemstack1, 0, 10, true)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (indexIn >= 10 && indexIn < 13) {
				if (!this.moveItemStackTo(itemstack1, 10, 13, false)) {
					if (indexIn < 37) {
						if (!this.moveItemStackTo(itemstack1, 34, 43, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.moveItemStackTo(itemstack1, 7, 34, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.moveItemStackTo(itemstack1, 7, 43, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
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

		return itemstack;
	}

}