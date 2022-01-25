package com.tcn.cosmosindustry.transport.core.block;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.IndustryReference.RESOURCE.TRANSPORT;
import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.transport.core.blockentity.BlockEntityChannelTransparentEnergy;
import com.tcn.cosmoslibrary.common.block.CosmosBlockRemovable;
import com.tcn.cosmoslibrary.common.enums.EnumChannelSideState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class BlockChannelTransparentEnergy extends CosmosBlockRemovable implements EntityBlock {
	
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	
	protected static final VoxelShape[] BOUNDING_BOXES = IndustryReference.RESOURCE.TRANSPORT.BOUNDING_BOXES_STANDARD;
	
	public BlockChannelTransparentEnergy(Block.Properties properties) {
		super(properties);
		
		this.registerDefaultState(this.defaultBlockState().setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false)
				.setValue(WEST, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityChannelTransparentEnergy(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, IndustryModBusManager.BLOCK_ENTITY_TYPE_CHANNEL_TRANSPARENT_ENERGY);
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityChannelTransparentEnergy> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityChannelTransparentEnergy::tick);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST);
	}
	
	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) { 
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityChannelTransparentEnergy) {
			((BlockEntityChannelTransparentEnergy) tileEntity).attack(state, worldIn, pos, playerIn);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		super.use(state, worldIn, pos, playerIn, handIn, hit);
		
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityChannelTransparentEnergy) {
			return ((BlockEntityChannelTransparentEnergy) tileEntity).use(state, worldIn, pos, playerIn, handIn, hit);
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		for	(Direction c : Direction.values()) {
			BlockState newState = this.updateShape(state, c, worldIn.getBlockState(pos.relative(c)), worldIn, pos, pos.relative(c));
			
			worldIn.sendBlockUpdated(pos, state, newState, 3);
			worldIn.setBlockAndUpdate(pos, newState);
		}
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {	
		BlockEntity tile_this = worldIn.getBlockEntity(currentPos);
		
		EnumChannelSideState[] side_array = EnumChannelSideState.getStandardArray();
		
		if (tile_this != null && tile_this instanceof BlockEntityChannelTransparentEnergy) {
			side_array = ((BlockEntityChannelTransparentEnergy) tile_this).getSideArray();
		}
		
		// Order is: [D-U-N-S-W-E]
		boolean[] bool_array = new boolean[] { false, false, false, false, false, false };
		
		for (Direction c : Direction.values()) {
			int i = c.get3DDataValue();
			
			if (side_array[i].equals(EnumChannelSideState.DISABLED)) {
				bool_array[i] = false;
			} else if (side_array[i].equals(EnumChannelSideState.INTERFACE_INPUT) || side_array[i].equals(EnumChannelSideState.INTERFACE_OUTPUT)) {
				bool_array[i] = true;
			}else {
				bool_array[i] = this.connectsTo(worldIn, currentPos, c);
			}
		}
		
		return stateIn.setValue(DOWN, bool_array[0]).setValue(UP, bool_array[1]).setValue(NORTH, bool_array[2]).setValue(SOUTH, bool_array[3]).setValue(WEST, bool_array[4]).setValue(EAST, bool_array[5]);
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockReader, BlockPos pos, CollisionContext context) {
		VoxelShape[] shapes = new VoxelShape[] { BOUNDING_BOXES[0], Shapes.empty(), Shapes.empty(), Shapes.empty(), Shapes.empty(), Shapes.empty(), Shapes.empty() };
		VoxelShape[] shapesInterface = new VoxelShape[] { Shapes.empty(), Shapes.empty(), Shapes.empty(), Shapes.empty(), Shapes.empty(), Shapes.empty() };
		
		BlockEntity entity = blockReader.getBlockEntity(pos);
		
		for (Direction dir : Direction.values()) {
			if (entity instanceof BlockEntityChannelTransparentEnergy) {
				BlockEntityChannelTransparentEnergy blockEntity = (BlockEntityChannelTransparentEnergy) entity;
				EnumChannelSideState state = blockEntity.getStateForConnection(dir);
				
				Collection<Property<?>> props = blockState.getProperties();
				ArrayList<Property<?>> propArray = new ArrayList<>(props);
				
				for (int i = 0; i < propArray.size(); i++) {
					Property<?> rawProp = propArray.get(i);
					
					if(rawProp instanceof BooleanProperty) {
						BooleanProperty prop = (BooleanProperty) rawProp;
						String propName = prop.getName();
						
						if (dir.getName().equals(propName)) {
							if (blockState.getValue(prop).booleanValue()) {
								shapes[dir.get3DDataValue() + 1] = BOUNDING_BOXES[dir.get3DDataValue() + 1];

							}
						}
					}
				}
				
				if (state.isInterface() || state.equals(EnumChannelSideState.DISABLED)) {
					shapesInterface[dir.get3DDataValue()] = TRANSPORT.BOUNDING_BOXES_INTERFACE[dir.get3DDataValue()];
				}
			}
		}
		
		return Shapes.or(shapes[0], shapes[1], shapes[2], shapes[3], shapes[4], shapes[5], shapes[6], 
				shapesInterface[0], shapesInterface[1], shapesInterface[2], shapesInterface[3], shapesInterface[4], shapesInterface[5]);
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockReader, BlockPos pos) {
		return this.getShape(blockState, blockReader, pos, CollisionContext.empty());
	}

	@Override
	public VoxelShape getVisualShape(BlockState blockState, BlockGetter blockReader, BlockPos pos, CollisionContext context) {
		return this.getShape(blockState, blockReader, pos, context);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockReader, BlockPos pos, CollisionContext context) {
		return this.getShape(blockState, blockReader, pos, context);
	}

	public boolean connectsTo(LevelAccessor world, BlockPos pos, Direction dir) {
		BlockEntity tile_other = world.getBlockEntity(pos.relative(dir));
		
		if (tile_other != null) {
			if (tile_other.getCapability(CapabilityEnergy.ENERGY, dir).resolve().isPresent()) {
				LazyOptional<?> consumer = tile_other.getCapability(CapabilityEnergy.ENERGY, dir);
	
				if (consumer.resolve().get() instanceof IEnergyStorage) {
					IEnergyStorage storage = (IEnergyStorage) consumer.resolve().get();
	
					if (storage.canReceive() || storage.canExtract()) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
}