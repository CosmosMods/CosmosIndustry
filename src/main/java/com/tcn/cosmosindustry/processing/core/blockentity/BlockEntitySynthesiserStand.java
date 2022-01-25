package com.tcn.cosmosindustry.processing.core.blockentity;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.processing.core.block.BlockSynthesiserStand;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class BlockEntitySynthesiserStand extends BlockEntity implements IBlockInteract, Container, WorldlyContainer, MenuProvider {

	private static final int[] SLOTS_ACC = { 0 };
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);

	public BlockEntitySynthesiserStand(BlockPos posIn, BlockState stateIn) {
		super(IndustryModBusManager.TILE_TYPE_SYNTHESISER_STAND, posIn, stateIn);
	}

	public void sendUpdates() {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			BlockSynthesiserStand block = (BlockSynthesiserStand) state.getBlock();
			
			level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (!level.isClientSide) {
				level.setBlockAndUpdate(this.getBlockPos(), block.defaultBlockState());
			}
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		
		this.inventoryItems = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems);
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
	}
	
	/**
	 * Retrieve the data to be stored. [TE > NBT] (WRITE)
	 */
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		
		this.saveAdditional(tag);
		
		return tag;
	}
	
	/**
	 * Actually sends the data to the server. [NBT > SER]
	 */
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	/**
	 * Method is called once packet has been received by the client. [SER > CLT]
	 */
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag tag_ = pkt.getTag();
		
		this.handleUpdateTag(tag_);
	}
	
	@Override
	public void onLoad() { }

	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntitySynthesiserStand entityIn) { }
	
	@Override
	public void attack(BlockState state, Level LevelIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if ((!CosmosUtil.handEmpty(playerIn)) && (this.getItem(0).isEmpty())) {
			ItemStack stack = playerIn.getItemInHand(handIn);

			levelIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2F, 0F, false);
			
			this.setItem(0, stack.copy());
			stack.shrink(1);
			return InteractionResult.SUCCESS;
		} 
		
		else if (!this.getItem(0).isEmpty()) {
			playerIn.addItem(this.getItem(0));
			this.setItem(0, ItemStack.EMPTY);
			levelIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2F, 2F, false);
			return InteractionResult.SUCCESS;
		} 
		return InteractionResult.FAIL;
	}

	@Override
	public void clearContent() { }

	@Override
	public boolean canPlaceItemThroughFace(int arg0, ItemStack arg1, Direction arg2) {
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int arg0, ItemStack arg1, Direction arg2) {
		return false;
	}

	@Override
	public int getContainerSize() {
		return this.inventoryItems.size();
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public ItemStack getItem(int index) {
		return this.inventoryItems.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		this.sendUpdates();
		return ContainerHelper.removeItem(this.inventoryItems, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		this.sendUpdates();
		return ContainerHelper.takeItem(this.inventoryItems, index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.inventoryItems.set(index, stack);
		
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		this.sendUpdates();
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.inventoryItems) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return SLOTS_ACC;
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.locComp("cosmosindustry.gui.synthesiser_stand");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		//return new ContainerCharger(idIn, playerInventoryIn, this, ILevelPosCallable.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
		return null;
	}

	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == Direction.UP)
				return handlers[0].cast();
			else if (facing == Direction.DOWN)
				return handlers[1].cast();
			else
				return handlers[2].cast();
		}
		
		return super.getCapability(capability, facing);
	}
	
}