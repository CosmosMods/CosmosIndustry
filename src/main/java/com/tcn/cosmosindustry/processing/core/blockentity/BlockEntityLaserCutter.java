package com.tcn.cosmosindustry.processing.core.blockentity;

import java.util.Random;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.core.recipe.CompactorRecipes;
import com.tcn.cosmosindustry.processing.client.container.ContainerLaserCutter;
import com.tcn.cosmosindustry.processing.core.block.BlockLaserCutter;
import com.tcn.cosmoslibrary.client.interfaces.IBlockEntityClientUpdated;
import com.tcn.cosmoslibrary.common.interfaces.IEnergyEntity;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.NetworkHooks;

@SuppressWarnings("unused")
public class BlockEntityLaserCutter extends BlockEntity implements IBlockInteract, Container, WorldlyContainer, MenuProvider, IBlockEntityClientUpdated.Processing, IEnergyEntity {

	private static final int[] SLOTS_TOP = new int[] { 0 };
	private static final int[] SLOTS_BOTTOM = new int[] { 2, 1 };
	private static final int[] SLOTS_SIDES = new int[] { 1 };
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(6, ItemStack.EMPTY);

	private int update = 0;
	private int process_time;
	private int process_speed = IndustryReference.RESOURCE.PROCESSING.SPEED_RATE[0];
	
	private int energy_stored = 0;
	private int energy_capacity = IndustryReference.RESOURCE.PROCESSING.CAPACITY[0];
	private int energy_max_receive = IndustryReference.RESOURCE.PROCESSING.MAX_INPUT[0];
	private int rf_tick_rate = IndustryReference.RESOURCE.PROCESSING.RF_TICK_RATE[0];
	
	public BlockEntityLaserCutter(BlockPos posIn, BlockState stateIn) {
		super(IndustryModBusManager.TILE_TYPE_LASER_CUTTER, posIn, stateIn);
	}

	public void sendUpdates() {
		if (this.level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			
			level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (!level.isClientSide) {
				level.setBlockAndUpdate(this.getBlockPos(), state);
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems);
		
		compound.putInt("process_time", this.process_time);
		compound.putInt("process_speed", this.process_speed);

		compound.putInt("energy", this.energy_stored);
		compound.putInt("rf_rate", this.rf_tick_rate);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		
		this.inventoryItems = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems);
		
		this.process_time = compound.getInt("process_time");
		this.process_speed = compound.getInt("process_speed");
		
		this.energy_stored = compound.getInt("energy");
		this.rf_tick_rate = compound.getInt("rf_rate");
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
		this.sendUpdates();
	}

	@Override
	public void onLoad() { }
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityLaserCutter entityIn) {
		if (entityIn.canProcess() && entityIn.hasEnergy()) {
			entityIn.extractEnergy(Direction.DOWN, entityIn.rf_tick_rate, false);
			
			entityIn.process_time++;
			
			if (entityIn.process_time == entityIn.process_speed) {
				entityIn.process_time = 0;
				if (!entityIn.level.isClientSide) {
					entityIn.processItem();
				}
			}
			
		} else {
			entityIn.process_time = 0;
		}
		
		if (entityIn.process_time > 0) {
			entityIn.sendUpdates();
		}
		
		if (entityIn.canProcess() && entityIn.hasEnergy()) {
			BlockPos pos = entityIn.getBlockPos();
			Random rand = new Random();
			
			if (rand.nextDouble() < 0.1D) {
				entityIn.level.playLocalSound(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
			}
			
			entityIn.level.addParticle(ParticleTypes.FLAME, pos.getX() + 0.2, pos.getY() + 0.4, pos.getZ() + 0.2, 0.0D, 0.0D, 0.0D);
			entityIn.level.addParticle(ParticleTypes.FLAME, pos.getX() + 0.2, pos.getY() + 0.4, pos.getZ() + 0.8, 0.0D, 0.0D, 0.0D);
			entityIn.level.addParticle(ParticleTypes.FLAME, pos.getX() + 0.8, pos.getY() + 0.4, pos.getZ() + 0.2, 0.0D, 0.0D, 0.0D);
			entityIn.level.addParticle(ParticleTypes.FLAME, pos.getX() + 0.8, pos.getY() + 0.4, pos.getZ() + 0.8, 0.0D, 0.0D, 0.0D);
			
			entityIn.level.addParticle(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.2, 0.0D, 0.0D, 0.0D);
			entityIn.level.addParticle(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.8, 0.0D, 0.0D, 0.0D);
			entityIn.level.addParticle(ParticleTypes.FLAME, pos.getX() + 0.8, pos.getY() + 0.4, pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
			entityIn.level.addParticle(ParticleTypes.FLAME, pos.getX() + 0.2, pos.getY() + 0.4, pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
			
			entityIn.level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.2, pos.getY() + 0.4, pos.getZ() + 0.2, 0.0D, 0.0D, 0.0D);
			entityIn.level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.2, pos.getY() + 0.4, pos.getZ() + 0.8, 0.0D, 0.0D, 0.0D);
			entityIn.level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.8, pos.getY() + 0.4, pos.getZ() + 0.2, 0.0D, 0.0D, 0.0D);
			entityIn.level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.8, pos.getY() + 0.4, pos.getZ() + 0.8, 0.0D, 0.0D, 0.0D);
		}
		
		int i = entityIn.inventoryItems.get(3).getCount();
		entityIn.process_speed = IndustryReference.RESOURCE.PROCESSING.SPEED_RATE[i];
		
		int j = entityIn.inventoryItems.get(4).getCount();
		entityIn.energy_capacity = IndustryReference.RESOURCE.PROCESSING.CAPACITY[j];

		int k = entityIn.inventoryItems.get(5).getCount();
		entityIn.rf_tick_rate = IndustryReference.RESOURCE.PROCESSING.RF_TICK_RATE[i] - IndustryReference.RESOURCE.PROCESSING.RF_EFF_RATE[k];
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (playerIn instanceof ServerPlayer) {
			NetworkHooks.openGui((ServerPlayer) playerIn, this, (packetBuffer)->{ packetBuffer.writeBlockPos(this.getBlockPos()); });
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean canProcess() {
		if (this.inventoryItems.get(0).isEmpty()) {
			return false;
		} else {
			ItemStack itemstack = CompactorRecipes.getInstance().getRecipeResult((ItemStack) this.inventoryItems.get(0));

			if (itemstack.isEmpty()) {
				return false;
			} else {
				ItemStack itemstack1 = this.inventoryItems.get(2);
				
				if (itemstack1.isEmpty()) {
					return true;
				}
				
				if (itemstack1.getItem() != itemstack.getItem()) {
					return false;
				}
				
				int result = itemstack1.getCount() + itemstack.getCount();
				return result <= this.getMaxStackSize() && result <= itemstack1.getMaxStackSize();
			}
		}
	}

	@Override
	public boolean isProcessing() {
		return this.hasEnergy() && this.canProcess() && this.process_time > 0;
	}
	
	@Override
	public void processItem() {
		if (this.canProcess()) {
			ItemStack itemstack = this.inventoryItems.get(0);
			ItemStack itemstack1 = CompactorRecipes.getInstance().getRecipeResult(itemstack);
			ItemStack itemstack2 = this.inventoryItems.get(2);

			if (itemstack2.isEmpty()) {
				this.inventoryItems.set(2, itemstack1.copy());
			} else if (itemstack2.getItem() == itemstack1.getItem()) {
				itemstack2.grow(itemstack1.getCount());
			}
			
			itemstack.shrink(1);
		}
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
	public ItemStack getItem(int index) {
		return this.inventoryItems.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		this.setChanged();
		return ContainerHelper.removeItem(this.inventoryItems, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		this.setChanged();
		return ContainerHelper.takeItem(this.inventoryItems, index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.inventoryItems.set(index, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		
		this.setChanged();
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
		return side == Direction.DOWN ? SLOTS_BOTTOM : (side == Direction.UP ? SLOTS_TOP : SLOTS_SIDES);
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.locComp("cosmosindustry.gui.laser_cutter");
	}

	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerLaserCutter(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
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
		} else if (capability == CapabilityEnergy.ENERGY) {
			return this.createEnergyProxy(facing).cast();
		}
		
		return super.getCapability(capability, facing);
	}

	private LazyOptional<IEnergyStorage> createEnergyProxy(@Nullable Direction directionIn) {
        return LazyOptional.of(() -> new IEnergyStorage() {
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return BlockEntityLaserCutter.this.extractEnergy(directionIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntityLaserCutter.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntityLaserCutter.this.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BlockEntityLaserCutter.this.receiveEnergy(directionIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntityLaserCutter.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntityLaserCutter.this.canExtract(directionIn);
            }
        });
    }

	@Override
	public void setMaxTransfer(int maxTransfer) {
		this.setMaxReceive(maxTransfer);
	}

	@Override
	public void setMaxReceive(int max_receive) {
		this.energy_max_receive = max_receive;
	}

	@Override
	public int getMaxReceive() {
		return this.energy_max_receive;
	}

	@Override
	public void setEnergyStored(int stored) {
		this.energy_stored = stored;

		if (this.energy_stored > energy_capacity) {
			this.energy_stored = energy_capacity;
		} else if (this.energy_stored < 0) {
			this.energy_stored = 0;
		}
	}

	@Override
	public void modifyEnergyStored(int stored) {
		this.energy_stored += stored;

		if (this.energy_stored > this.energy_capacity) {
			this.energy_stored = energy_capacity;
		} else if (this.energy_stored < 0) {
			this.energy_stored = 0;
		}
	}

	@Override
	public int receiveEnergy(Direction directionIn, int max_receive, boolean simulate) {
		int storedReceived = Math.min(this.getMaxEnergyStored() - energy_stored, Math.min(this.energy_max_receive, max_receive));

		if (!simulate) {
			this.energy_stored += storedReceived;
		}

		this.sendUpdates();
		return storedReceived;
	}

	@Override
	public int extractEnergy(Direction directionIn, int max_extract, boolean simulate) {
		int storedExtracted = Math.min(energy_stored, Math.min(this.rf_tick_rate, max_extract));

		if (!simulate) {
			this.energy_stored -= storedExtracted;
		}

		this.sendUpdates();
		return storedExtracted;
	}

	@Override
	public int getEnergyStored() {
		return this.energy_stored;
	}

	@Override
	public int getMaxEnergyStored() {
		return this.energy_capacity;
	}

	@Override
	public boolean hasEnergy() {
		return this.energy_stored > 0;
	}

	@Override
	public int getEnergyScaled(int scale) {
		return ((this.getEnergyStored() / 100) * scale) / (this.getMaxEnergyStored() / 100);
	}

	@Override
	public boolean canExtract(Direction directionIn) {
		return false;
	}

	@Override
	public boolean canReceive(Direction directionIn) {
		BlockState state = this.level.getBlockState(this.getBlockPos());
		
		if (state.getBlock() instanceof BlockLaserCutter) {
			Direction facing = state.getValue(BlockLaserCutter.FACING).getOpposite();
			
			if (directionIn.equals(Direction.DOWN)) {
				return false;
			} else if (directionIn.equals(facing)) {
				return false;
			} else {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public int getProcessSpeed() {
		return this.process_speed;
	}

	@Override
	public int getProcessTime(int i) {
		if (i == 0) {
			return this.process_time;
		}
		return -1;
	}
	
	@Override
	public int getProcessProgressScaled(int scale) {
		return this.process_time * scale / this.process_speed;
	}

	@Override
	public void setMaxExtract(int maxExtract) {	}

	@Override
	public int getMaxExtract() {
		return 0;
	}

	@Override
	public int getRFTickRate() {
		return this.rf_tick_rate;
	}
}