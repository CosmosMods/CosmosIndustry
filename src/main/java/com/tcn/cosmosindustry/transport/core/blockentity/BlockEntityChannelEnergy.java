package com.tcn.cosmosindustry.transport.core.blockentity;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.transport.core.util.TransportUtil;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumChannelSideState;
import com.tcn.cosmoslibrary.common.enums.EnumConnectionType;
import com.tcn.cosmoslibrary.common.interfaces.IEnergyEntity;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityChannel;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityChannelSided;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityChannelType.IChannelEnergy;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class BlockEntityChannelEnergy extends BlockEntity implements IBlockInteract, IBlockEntityChannelSided, IChannelEnergy, IEnergyEntity, IBlockEntityChannel.Energy {

	private EnumChannelSideState[] SIDE_STATE_ARRAY = EnumChannelSideState.getStandardArray();
	
	private int energy_stored = 0;
	private int energy_capacity = IndustryReference.RESOURCE.TRANSPORT.ENERGY_CAPACITY;
	private int energy_max_receive = IndustryReference.RESOURCE.TRANSPORT.ENERGY_MAX_TRANSFER;
	private int energy_max_extract = IndustryReference.RESOURCE.TRANSPORT.ENERGY_MAX_TRANSFER;
	
	public Direction last_facing;
	public int last_rf_rate;

	public BlockEntityChannelEnergy(BlockPos posIn, BlockState stateIn) {
		super(IndustryModBusManager.BLOCK_ENTITY_TYPE_CHANNEL_ENERGY, posIn, stateIn);
	}
	
	@Override 
	public EnumChannelSideState getSide(Direction facing) {
		return this.SIDE_STATE_ARRAY[facing.get3DDataValue()];
	}
	
	@Override
	public void setSide(Direction facing, EnumChannelSideState side_state) {
		this.SIDE_STATE_ARRAY[facing.get3DDataValue()] = side_state;
		this.updateRenders();
	}
	
	@Override
	public EnumChannelSideState[] getSideArray() {
		return this.SIDE_STATE_ARRAY;
	}

	@Override
	public void setSideArray(EnumChannelSideState[] new_array) {
		this.SIDE_STATE_ARRAY = new_array;
	}
	
	public void cycleSide(Direction facing, @Nullable Player playerIn) {
		this.cycleSide(facing);
		
		if (playerIn != null) {
			CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.locComp(ComponentColour.CYAN, false, "cosmosindustry.channel.status.cycle_side").append(this.getSide(facing).getColouredComp()));
		}
	}

	@Override
	public void cycleSide(Direction facing) {
		this.setSide(facing, this.getSide(facing).getNextStateUser());
	}

	@Override
	public boolean canConnect(Direction facing) {
		EnumChannelSideState state = this.getSide(facing);
		
		if (state.equals(EnumChannelSideState.DISABLED)) {
			return false;
		}
		
		return true;
	}

	@Override
	public void updateRenders() {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			
			level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (!level.isClientSide) {
				level.setBlockAndUpdate(this.getBlockPos(), state.updateShape(Direction.DOWN, state, level, this.getBlockPos(), this.getBlockPos()));
			}
		}
	}
	
	@Override
	public Direction getLastFacing() {
		return this.last_facing;
	}
	
	@Override
	public void setLastFacing(Direction facing) {
		this.last_facing = facing;
	}

	@Override
	public int getLastRFRate() {
		return this.last_rf_rate;
	}

	@Override
	public void setLastRFRate(int value) { 
		this.last_rf_rate = value;
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		
		if (this.last_facing != null) {
			compound.putInt("last_facing", this.getLastFacing().get3DDataValue());
		}
		
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
		
		if (compound.contains("last_facing")) {
			this.setLastFacing(Direction.from3DDataValue(compound.getInt("last_facing")));
		}
		
		this.setSide(Direction.values()[0], EnumChannelSideState.getStateFromIndex(compound.getInt("down")));
		this.setSide(Direction.values()[1], EnumChannelSideState.getStateFromIndex(compound.getInt("up")));
		this.setSide(Direction.values()[2], EnumChannelSideState.getStateFromIndex(compound.getInt("north")));
		this.setSide(Direction.values()[3], EnumChannelSideState.getStateFromIndex(compound.getInt("south")));
		this.setSide(Direction.values()[4], EnumChannelSideState.getStateFromIndex(compound.getInt("west")));
		this.setSide(Direction.values()[5], EnumChannelSideState.getStateFromIndex(compound.getInt("east")));
		
		this.energy_stored = compound.getInt("energy");
	}

	/**
	 * Set the data once it has been received. [NBT > TE] (READ)
	 */
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
		
		//this.updateRenders();
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
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityChannelEnergy entityIn) {
		if (!levelIn.isClientSide()) {
			Arrays.stream(Direction.values()).parallel().forEach((d) -> {
				BlockEntity tile = entityIn.getLevel().getBlockEntity(entityIn.getBlockPos().offset(d.getNormal()));
				
				if (tile != null && !tile.isRemoved()) {
					EnumChannelSideState state = entityIn.getSide(d);
					
					if (entityIn.hasEnergy()) {
						if (state != EnumChannelSideState.INTERFACE_INPUT && state != EnumChannelSideState.DISABLED) {
							if (!(d.equals(entityIn.last_facing))) {
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
				}
			});
		}
	}
	
	@Override
	public EnumChannelSideState getStateForConnection(Direction facing) {
		return TransportUtil.getStateForConnection(facing, this.getBlockPos(), this.getLevel(), this);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (CosmosUtil.holdingWrench(playerIn) && !playerIn.isShiftKeyDown()) {
			Direction dir = TransportUtil.getDirectionFromHit(pos, hit);
			if (dir != null) {
				this.cycleSide(dir, playerIn);
			} else {
				this.cycleSide(hit.getDirection(), playerIn);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null) {
			if (capability == CapabilityEnergy.ENERGY) {
				return this.createEnergyProxy(facing).cast();
			}
		}
		
		return super.getCapability(capability, facing);
	}
	
	private LazyOptional<IEnergyStorage> createEnergyProxy(@Nullable Direction directionIn) {
        return LazyOptional.of(() -> new IEnergyStorage() {
        	
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return BlockEntityChannelEnergy.this.extractEnergy(directionIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntityChannelEnergy.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntityChannelEnergy.this.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BlockEntityChannelEnergy.this.receiveEnergy(directionIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntityChannelEnergy.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntityChannelEnergy.this.canExtract(directionIn);
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
		this.last_facing = directionIn.getOpposite();
		
		int storedReceived = Math.min(this.getMaxEnergyStored() - energy_stored, Math.min(this.energy_max_receive, max_receive));

		if (!simulate) {
			this.energy_stored += storedReceived;
		}
		return storedReceived;
	}

	@Override
	public int extractEnergy(Direction directionIn, int max_extract, boolean simulate) {
		int storedExtracted = Math.min(energy_stored, Math.min(this.energy_max_extract, max_extract));
		
		if (!simulate) {
			this.energy_stored -= storedExtracted;
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
	public boolean canExtract(Direction directionIn) {
		if (this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.DISABLED) || this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.INTERFACE_INPUT)) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean canReceive(Direction directionIn) {
		if (this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.DISABLED) || this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.INTERFACE_OUTPUT)) {
			return false;
		}
		
		return true;
	}

	@Override
	public int getEnergyScaled(int scale) {
		return 0;
	}

	@Override
	public EnumConnectionType getChannelType() {
		return EnumConnectionType.ENERGY;
	}
}