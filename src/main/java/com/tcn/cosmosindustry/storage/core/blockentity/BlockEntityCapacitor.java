package com.tcn.cosmosindustry.storage.core.blockentity;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.storage.client.container.ContainerCapacitor;
import com.tcn.cosmoslibrary.client.interfaces.IBlockEntityClientUpdated;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumSideState;
import com.tcn.cosmoslibrary.common.interfaces.IEnergyEntity;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntitySided;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
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

public class BlockEntityCapacitor extends BlockEntity implements IBlockInteract, Container, WorldlyContainer, MenuProvider, IEnergyEntity, IBlockEntitySided, IBlockEntityClientUpdated.Storage {

	private static final int[] SLOTS_TOP = new int[] { 0, 1 };
	private static final int[] SLOTS_BOTTOM = new int[] { 0, 1 };
	private static final int[] SLOTS_SIDES = new int[] { 0, 1 };
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
	
	private int energy_stored = 0;
	private int energy_capacity = IndustryReference.RESOURCE.STORAGE.CAPACITY;
	private int energy_max_receive = IndustryReference.RESOURCE.STORAGE.MAX_INPUT;
	private int energy_max_extract = IndustryReference.RESOURCE.STORAGE.MAX_OUTPUT;
	
	private EnumSideState[] SIDE_STATE_ARRAY = EnumSideState.getStandardArray();

	public BlockEntityCapacitor(BlockPos posIn, BlockState stateIn) {
		super(IndustryModBusManager.BLOCK_ENTITY_TYPE_CAPACITOR, posIn, stateIn);
	}

	public void sendUpdates(boolean update) {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			
			level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (!level.isClientSide) {
				level.setBlockAndUpdate(this.getBlockPos(), state.updateShape(Direction.DOWN, state, this.getLevel(), this.getBlockPos(), this.getBlockPos()));
			}
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems);

		compound.putInt("down", this.SIDE_STATE_ARRAY[0].getIndex());
		compound.putInt("up", this.SIDE_STATE_ARRAY[1].getIndex());
		compound.putInt("north", this.SIDE_STATE_ARRAY[2].getIndex());
		compound.putInt("south", this.SIDE_STATE_ARRAY[3].getIndex());
		compound.putInt("west", this.SIDE_STATE_ARRAY[4].getIndex());
		compound.putInt("east", this.SIDE_STATE_ARRAY[5].getIndex());
		
		compound.putInt("energy", this.energy_stored);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		
		this.inventoryItems = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems);

		this.setSide(Direction.values()[0], EnumSideState.getStateFromIndex(compound.getInt("down")), false);
		this.setSide(Direction.values()[1], EnumSideState.getStateFromIndex(compound.getInt("up")), false);
		this.setSide(Direction.values()[2], EnumSideState.getStateFromIndex(compound.getInt("north")), false);
		this.setSide(Direction.values()[3], EnumSideState.getStateFromIndex(compound.getInt("south")), false);
		this.setSide(Direction.values()[4], EnumSideState.getStateFromIndex(compound.getInt("west")), false);
		this.setSide(Direction.values()[5], EnumSideState.getStateFromIndex(compound.getInt("east")), false);
		
		this.energy_stored = compound.getInt("energy");
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
		this.sendUpdates(true);
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
	public void onLoad() { 
		this.sendUpdates(true);
	}
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityCapacitor entityIn) {
		entityIn.drainItem();
		entityIn.chargeItem();
		
		if (!levelIn.isClientSide()) {
			Arrays.stream(Direction.values()).parallel().forEach((d) -> {
				BlockEntity tile = entityIn.getLevel().getBlockEntity(entityIn.getBlockPos().offset(d.getNormal()));
				
				if (tile != null && !tile.isRemoved()) {
					EnumSideState state = entityIn.getSide(d);
					
					if (entityIn.hasEnergy()) {
						if (state == EnumSideState.INTERFACE_OUTPUT) {
							if (tile.getCapability(CapabilityEnergy.ENERGY, d).resolve().isPresent()) {
								LazyOptional<?> consumer = tile.getCapability(CapabilityEnergy.ENERGY, d);
								
								if (consumer.resolve().get() instanceof IEnergyStorage) {
									IEnergyStorage storage = (IEnergyStorage) consumer.resolve().get();
									
									if (storage.canReceive()) {
										int extract = entityIn.extractEnergy(d, entityIn.energy_max_extract, true);
										int actualExtract = storage.receiveEnergy(extract, true);
										
										if (actualExtract > 0) {
											entityIn.extractEnergy(d, storage.receiveEnergy(actualExtract, false), false);
										}
									}
								}
							}
						}
					}
				}
			});
		}
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (!playerIn.isShiftKeyDown()) {
			if (CosmosUtil.holdingWrench(playerIn)) {
				this.cycleSide(hit.getDirection(), playerIn, true);
				return InteractionResult.SUCCESS;
			}
		
			if (worldIn.isClientSide) {
				return InteractionResult.SUCCESS;
			} else {
				if (playerIn instanceof ServerPlayer) {
					NetworkHooks.openGui((ServerPlayer) playerIn, this, (packetBuffer) -> { packetBuffer.writeBlockPos(this.getBlockPos()); });
				}
			}
		}
		return InteractionResult.PASS;
	}

	public void drainItem() {
		if (!this.getItem(0).isEmpty()) {
			ItemStack stackIn = this.getItem(0);
			Item item = stackIn.getItem();
		
			if (item instanceof ICosmosEnergyItem) {
				ICosmosEnergyItem energyItem = (ICosmosEnergyItem) item;
				
				if (energyItem.hasEnergy(stackIn)) {
					if (energyItem.canExtractEnergy(stackIn)) {
						energyItem.extractEnergy(stackIn, this.receiveEnergy(Direction.DOWN, Math.min(this.getMaxReceive(), energyItem.getMaxExtract(stackIn)), false), false);
					}
				}
			}
		}
	}

	public void chargeItem() {
		if (!this.getItem(1).isEmpty()) {
			ItemStack stackIn = this.getItem(1);
			Item item = stackIn.getItem();
		
			if (item instanceof ICosmosEnergyItem) {
				ICosmosEnergyItem energyItem = (ICosmosEnergyItem) item;
				
				if (this.hasEnergy()) {
					if (energyItem.canReceiveEnergy(stackIn)) {
						energyItem.receiveEnergy(stackIn, this.extractEnergy(Direction.DOWN, Math.min(this.getMaxExtract(), energyItem.getMaxReceive(stackIn)), false), false);
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
		return ComponentHelper.locComp("cosmosindustry.gui.capacitor");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerCapacitor(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());

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
                return BlockEntityCapacitor.this.extractEnergy(directionIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntityCapacitor.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntityCapacitor.this.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BlockEntityCapacitor.this.receiveEnergy(directionIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntityCapacitor.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntityCapacitor.this.canExtract(directionIn);
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
			
			if (storedReceived > 0) {
				this.sendUpdates(true);
			}
		}
		
		return storedReceived;
	}

	@Override
	public int extractEnergy(Direction directionIn, int max_extract, boolean simulate) {
		int storedExtracted = Math.min(energy_stored, Math.min(this.energy_max_extract, max_extract));

		if (!simulate) {
			this.energy_stored -= storedExtracted;
			
			if (storedExtracted > 0) {
				this.sendUpdates(true);
			}
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
		if (this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED) || this.getSide(directionIn.getOpposite()).equals(EnumSideState.INTERFACE_INPUT)) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean canReceive(Direction directionIn) {
		if (this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED) || this.getSide(directionIn.getOpposite()).equals(EnumSideState.INTERFACE_OUTPUT)) {
			return false;
		}
		
		return true;
	}

	@Override
	public EnumSideState getSide(Direction facing) {
		return this.SIDE_STATE_ARRAY[facing.get3DDataValue()];
	}
	
	@Override
	public void setSide(Direction facing, EnumSideState value, boolean update) {
		this.SIDE_STATE_ARRAY[facing.get3DDataValue()] = value;
		
		if (update) {
			this.sendUpdates(true);
		}
	}

	@Override
	public void cycleSide(Direction facing, boolean update) {
		this.setSide(facing, this.getSide(facing).getNextState(), update);
		
		if (update) {
			this.sendUpdates(true);
		}
	}

	public void cycleSide(Direction facing, @Nullable Player playerIn, boolean update) {
		this.cycleSide(facing, update);
		
		if (playerIn != null) {
			CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.locComp(ComponentColour.CYAN, false, "cosmosindustry.channel.status.cycle_side").append(this.getSide(facing).getColouredComp()));
		}
	}

	@Override
	public EnumSideState[] getSideArray() {
		return this.SIDE_STATE_ARRAY;
	}

	@Override
	public void setSideArray(EnumSideState[] new_array, boolean update) {
		this.SIDE_STATE_ARRAY = new_array;
	}
	
	public void setStack(Direction facing, ItemStack stack) {
		switch(facing) {
			case UP:
				inventoryItems.set(2, stack);
				break;
			case DOWN:
				inventoryItems.set(3, stack);
				break;
			case NORTH:
				inventoryItems.set(6, stack);
				break;
			case SOUTH:
				inventoryItems.set(7, stack);
				break;
			case EAST:
				inventoryItems.set(4, stack);
				break;
			case WEST:
				inventoryItems.set(5, stack);
				break;
				default:
		}
	}

	@Override
	public boolean canConnect(Direction facing) {
		if (this.getSide(facing).equals(EnumSideState.DISABLED)) {
			return false;
		}
		
		return true;
	}
}