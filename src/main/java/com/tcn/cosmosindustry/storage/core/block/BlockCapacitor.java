package com.tcn.cosmosindustry.storage.core.block;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityCapacitor;
import com.tcn.cosmoslibrary.common.nbt.CosmosBlockRemovableNBT;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BlockCapacitor extends CosmosBlockRemovableNBT implements EntityBlock {

	public static final IntegerProperty DOWN = IntegerProperty.create("down", 0, 3);
	public static final IntegerProperty UP = IntegerProperty.create("up", 0, 3);
	public static final IntegerProperty NORTH = IntegerProperty.create("north", 0, 3);
	public static final IntegerProperty SOUTH = IntegerProperty.create("south", 0, 3);
	public static final IntegerProperty WEST = IntegerProperty.create("west", 0, 3);
	public static final IntegerProperty EAST = IntegerProperty.create("east", 0, 3);
	
	public BlockCapacitor(Block.Properties properties) {
		super(properties);
		
		this.registerDefaultState(this.defaultBlockState()
			.setValue(DOWN, 0).setValue(UP, 0).setValue(NORTH, 0)
			.setValue(SOUTH, 0).setValue(WEST, 0).setValue(EAST, 0)
		);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityCapacitor(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, IndustryModBusManager.BLOCK_ENTITY_TYPE_CAPACITOR);
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityCapacitor> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityCapacitor::tick);
	}
	
	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) { 
		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		
		if (blockEntity instanceof BlockEntityCapacitor) {
			((BlockEntityCapacitor) blockEntity).attack(state, worldIn, pos, playerIn);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		super.use(state, worldIn, pos, playerIn, handIn, hit);
		
		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		
		if (blockEntity instanceof BlockEntityCapacitor) {
			return ((BlockEntityCapacitor) blockEntity).use(state, worldIn, pos, playerIn, handIn, hit);
		}
		return InteractionResult.SUCCESS;
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
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {	
		BlockEntityCapacitor block = (BlockEntityCapacitor) worldIn.getBlockEntity(currentPos);
		
		return stateIn.setValue(DOWN, block.getSide(Direction.DOWN).getIndex()).setValue(UP, block.getSide(Direction.UP).getIndex()).setValue(NORTH, block.getSide(Direction.NORTH).getIndex())
				.setValue(SOUTH, block.getSide(Direction.SOUTH).getIndex()).setValue(WEST, block.getSide(Direction.WEST).getIndex()).setValue(EAST, block.getSide(Direction.EAST).getIndex());
	}
	
}