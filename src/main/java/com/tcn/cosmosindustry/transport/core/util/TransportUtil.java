package com.tcn.cosmosindustry.transport.core.util;

import com.tcn.cosmosindustry.IndustryReference.RESOURCE.TRANSPORT;
import com.tcn.cosmosindustry.transport.core.blockentity.BlockEntityChannelEnergy;
import com.tcn.cosmosindustry.transport.core.blockentity.BlockEntityChannelSurgeEnergy;
import com.tcn.cosmosindustry.transport.core.blockentity.BlockEntityChannelTransparentEnergy;
import com.tcn.cosmosindustry.transport.core.blockentity.BlockEntityChannelTransparentSurgeEnergy;
import com.tcn.cosmoslibrary.common.enums.EnumChannelSideState;
import com.tcn.cosmoslibrary.common.enums.EnumConnectionType;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityChannelSided;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TransportUtil {
	
	public static EnumChannelSideState getStateForConnection(Direction facing, BlockPos pos, Level world, IBlockEntityChannelSided tile) {
		BlockEntity tile_offset = world.getBlockEntity(pos.offset(facing.getNormal()));
		
		if (tile.getSide(facing).equals(EnumChannelSideState.DISABLED)) {
			return EnumChannelSideState.DISABLED;
		} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_INPUT)) {
			return EnumChannelSideState.INTERFACE_INPUT;
		} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_OUTPUT)) {
			return EnumChannelSideState.INTERFACE_OUTPUT;
		} 
		
		else if (tile_offset != null) {
			if (tile.getChannelType().equals(EnumConnectionType.ENERGY)) {
				if (tile_offset instanceof IBlockEntityChannelSided) {
					if (tile_offset instanceof BlockEntityChannelSurgeEnergy) {
						if (((BlockEntityChannelSurgeEnergy) tile_offset).getSide(facing.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
							return EnumChannelSideState.CABLE_NO_CONN;
						} else {
							return EnumChannelSideState.CABLE_OTHER;
						}
					} else if (tile_offset instanceof BlockEntityChannelTransparentSurgeEnergy) {
						if (((BlockEntityChannelTransparentSurgeEnergy) tile_offset).getSide(facing.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
							return EnumChannelSideState.CABLE_NO_CONN;
						} else {
							return EnumChannelSideState.CABLE_OTHER;
						}
					} else if (((IBlockEntityChannelSided) tile_offset).getSide(facing.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
						return EnumChannelSideState.CABLE_NO_CONN;
					} else {
						return EnumChannelSideState.CABLE;
					}
				}
				
				if (tile_offset.getCapability(CapabilityEnergy.ENERGY, facing).resolve().isPresent()) {
					LazyOptional<?> consumer = tile_offset.getCapability(CapabilityEnergy.ENERGY, facing);
		
					if (consumer.resolve().get() instanceof IEnergyStorage) {
						IEnergyStorage storage = (IEnergyStorage) consumer.resolve().get();
						
						if (storage.canReceive() || storage.canExtract()) {
							if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_NO_CONN)) {
								return EnumChannelSideState.INTERFACE_NO_CONN;
							} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_OUTPUT)) {
								return EnumChannelSideState.INTERFACE_OUTPUT;
							} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_INPUT)) {
								return EnumChannelSideState.INTERFACE_INPUT;
							} else {
								return EnumChannelSideState.INTERFACE_NO_CONN;
							}
						}
					}
				}
				return EnumChannelSideState.AIR;
			} else if (tile.getChannelType().equals(EnumConnectionType.FLUID)) {
				
			} else if (tile.getChannelType().equals(EnumConnectionType.ITEM)) {
				
			}
		}
		
		return EnumChannelSideState.AIR;
	}
	
	public static EnumChannelSideState getStateForConnectionSurge(Direction facing, BlockPos pos, Level world, IBlockEntityChannelSided tile) {
		BlockEntity tile_offset = world.getBlockEntity(pos.offset(facing.getNormal()));
		
		if (tile.getSide(facing).equals(EnumChannelSideState.DISABLED)) {
			return EnumChannelSideState.DISABLED;
		} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_INPUT)) {
			return EnumChannelSideState.INTERFACE_INPUT;
		} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_OUTPUT)) {
			return EnumChannelSideState.INTERFACE_OUTPUT;
		} else if (tile_offset != null) {
			if (tile_offset instanceof IBlockEntityChannelSided) {
				if (tile_offset instanceof BlockEntityChannelEnergy) {
					if (((BlockEntityChannelEnergy) tile_offset).getSide(facing.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
						return EnumChannelSideState.CABLE_NO_CONN;
					} else {
						return EnumChannelSideState.CABLE_OTHER;
					}
				} else if (tile_offset instanceof BlockEntityChannelTransparentEnergy) {
					if (((BlockEntityChannelTransparentEnergy) tile_offset).getSide(facing.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
						return EnumChannelSideState.CABLE_NO_CONN;
					} else {
						return EnumChannelSideState.CABLE_OTHER;
					}
				} else if (((IBlockEntityChannelSided) tile_offset).getSide(facing.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
					return EnumChannelSideState.CABLE_NO_CONN;
				} else {
					return EnumChannelSideState.CABLE;
				}
			}
			
			else if (tile_offset.getCapability(CapabilityEnergy.ENERGY, facing).resolve().isPresent()) {
				LazyOptional<?> consumer = tile_offset.getCapability(CapabilityEnergy.ENERGY, facing);
	
				if (consumer.resolve().get() instanceof IEnergyStorage) {
					IEnergyStorage storage = (IEnergyStorage) consumer.resolve().get();
	
					if (storage.canReceive() || storage.canExtract()) {
						if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_NO_CONN)) {
							return EnumChannelSideState.INTERFACE_NO_CONN;
						} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_OUTPUT)) {
							return EnumChannelSideState.INTERFACE_OUTPUT;
						} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_INPUT)) {
							return EnumChannelSideState.INTERFACE_INPUT;
						} else {
							return EnumChannelSideState.INTERFACE_NO_CONN;
						}
					}
				}
			}
		}
		return EnumChannelSideState.AIR;
	}
	
	public static Direction getDirectionFromHit(BlockPos pos, BlockHitResult hit) {
		for (Direction dir : Direction.values()) {
			if (CosmosUtil.isInBounds(TRANSPORT.BOUNDING_BOXES_INTERFACE[dir.get3DDataValue()], pos, hit.getLocation())) {
				return dir;
			}
		}
		return null;
	}
}