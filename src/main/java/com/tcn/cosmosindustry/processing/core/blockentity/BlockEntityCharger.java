package com.tcn.cosmosindustry.processing.core.blockentity;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.processing.client.container.ContainerCharger;
import com.tcn.cosmoslibrary.client.interfaces.IBlockEntityClientUpdated;
import com.tcn.cosmoslibrary.common.enums.EnumEnergyState;
import com.tcn.cosmoslibrary.common.interfaces.IEnergyEntity;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.energy.interfaces.ICosmosEnergyItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.item.Item;
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

public class BlockEntityCharger extends BlockEntity implements IBlockInteract, Container, WorldlyContainer, MenuProvider, IBlockEntityClientUpdated.Charge, IEnergyEntity {
	
	private static final int[] SLOTS_TOP = new int[] { 0 };
	private static final int[] SLOTS_BOTTOM = new int[] { 2, 1 };
	private static final int[] SLOTS_SIDES = new int[] { 1 };
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(12, ItemStack.EMPTY);

	private int update = 0;
	private int energy_stored = 0;
	private int energy_capacity = IndustryReference.RESOURCE.PROCESSING.CAPACITY[0];
	private int energy_max_receive = IndustryReference.RESOURCE.PROCESSING.MAX_INPUT[0];
	private int energy_max_extract = IndustryReference.RESOURCE.PROCESSING.MAX_INPUT[0];
	private int chargeRate = 10000;
	private final int maxChargeRate = 20000;
	private EnumEnergyState energy_state = EnumEnergyState.FILL;
	
	public BlockEntityCharger(BlockPos posIn, BlockState stateIn) {
		super(IndustryModBusManager.TILE_TYPE_CHARGER, posIn, stateIn);
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
		
		compound.putInt("chargeRate", this.chargeRate);
		compound.putInt("energy_state", this.energy_state.getIndex());

		compound.putInt("energy", this.energy_stored);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		
		this.inventoryItems = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems);

		this.chargeRate = compound.getInt("chargeRate");
		this.energy_state = EnumEnergyState.getStateFromIndex(compound.getInt("energy_state"));

		this.energy_stored = compound.getInt("energy");
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
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityCharger entityIn) {
		if (entityIn.chargeRate > 0) {
			if (entityIn.energy_state.equals(EnumEnergyState.FILL)) {
				for (int i = 0; i < entityIn.inventoryItems.size() - 3; i++) {
					entityIn.chargeItem(i);
				}
			} else {
				for (int i = 0; i < entityIn.inventoryItems.size() - 3; i++) {
					entityIn.drainItem(i);
				}
			}
		}
		
		boolean flag = entityIn.update > 0;
		
		if (flag) {
			entityIn.update--;
		} else {
			entityIn.update = 100;
			entityIn.sendUpdates();
		}
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (playerIn instanceof ServerPlayer) {
			NetworkHooks.openGui((ServerPlayer) playerIn, this, (packetBuffer) -> { packetBuffer.writeBlockPos(this.getBlockPos()); });
		}
		return InteractionResult.SUCCESS;
	}
	
	public void chargeItem(int indexIn) {
		if (!this.getItem(indexIn).isEmpty()) {
			ItemStack stackIn = this.getItem(indexIn);
			Item item = stackIn.getItem();
		
			if (item instanceof ICosmosEnergyItem) {
				ICosmosEnergyItem energyItem = (ICosmosEnergyItem) item;
				
				if (this.chargeRate > 0) {
					if (this.hasEnergy()) {
						if (energyItem.canReceiveEnergy(stackIn)) {
							if (!this.level.isClientSide) {
								this.extractEnergy(Direction.DOWN, this.chargeRate, false);
							}
							
							energyItem.receiveEnergy(stackIn, this.extractEnergy(Direction.DOWN, this.chargeRate, true), false);
							
							this.sendUpdates();
						}
					}
				}
			}
		}
	}
	
	public void drainItem(int indexIn) {
		if (!this.getItem(indexIn).isEmpty()) {
			ItemStack stackIn = this.getItem(indexIn);
			Item item = stackIn.getItem();
		
			if (item instanceof ICosmosEnergyItem) {
				ICosmosEnergyItem energyItem = (ICosmosEnergyItem) item;
				
				if (this.chargeRate > 0) {
					if (energyItem.hasEnergy(stackIn)) {
						if (energyItem.canExtractEnergy(stackIn)) {
							if (!this.level.isClientSide) {
								this.receiveEnergy(Direction.DOWN, this.chargeRate, false);
							}

							energyItem.extractEnergy(stackIn, this.receiveEnergy(Direction.DOWN, this.chargeRate, true), false);
							
							this.sendUpdates();
						}
					}
				}
			}
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
		if (stack.getCount() > this.getContainerSize()) {
			stack.setCount(this.getContainerSize());
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
		return ComponentHelper.locComp("cosmosindustry.gui.charger");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerCharger(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
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
                return BlockEntityCharger.this.extractEnergy(directionIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntityCharger.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntityCharger.this.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BlockEntityCharger.this.receiveEnergy(directionIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntityCharger.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntityCharger.this.canExtract(directionIn);
            }
        });
    }

	@Override
	public void setMaxTransfer(int maxTransfer) {
		this.setMaxReceive(maxTransfer);
		this.setMaxExtract(maxTransfer);
	}

	@Override
	public void setMaxReceive(int maxReceive) {
		this.energy_max_receive = maxReceive;
	}
	@Override

	public void setMaxExtract(int maxExtract) {
		this.energy_max_extract = maxExtract;
	}

	@Override
	public int getMaxReceive() {
		return this.energy_max_receive;
	}

	@Override
	public int getMaxExtract() {
		return this.energy_max_extract;
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

		if (storedReceived > 0) {
			this.sendUpdates();
		}
		
		return storedReceived;
	}

	@Override
	public int extractEnergy(Direction directionIn, int max_extract, boolean simulate) {
		int storedExtracted = Math.min(energy_stored, Math.min(this.energy_max_extract, max_extract));
		
		if (!simulate) {
			this.energy_stored -= storedExtracted;
		}
		
		if (storedExtracted > 0) {
			this.sendUpdates();
		}
		
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
		if (directionIn.getOpposite().equals(Direction.DOWN)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int getChargeRate() {
		return this.chargeRate;
	}

	@Override
	public void setChargeRate(int chargeRate) {
		this.chargeRate = chargeRate;
	}

	@Override
	public void increaseChargeRate() {
		if (this.chargeRate < this.maxChargeRate) {
			this.chargeRate = this.chargeRate += 1000;
		}
		
		this.sendUpdates();
	}

	@Override
	public void decreaseChargeRate() {
		if (this.chargeRate > 0) {
			this.chargeRate = this.chargeRate -= 1000;
		}
		
		this.sendUpdates();
	}

	@Override
	public boolean canIncrease() {
		return this.chargeRate < this.maxChargeRate;
	}

	@Override
	public boolean canDecrease() {
		return this.chargeRate > 0;
	}
}