package com.tcn.cosmosindustry.processing.core.block;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityCharger;
import com.tcn.cosmoslibrary.common.nbt.CosmosBlockRemovableNBT;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockCharger extends CosmosBlockRemovableNBT implements EntityBlock {

	VoxelShape BASE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
	VoxelShape TOP = Block.box(1.0D, 12.0D, 1.0D, 15.0D, 14.0D, 15.0D);
	VoxelShape MIDDLE = Block.box(3.0D, 4.0D, 3.0D, 13.0D, 11.0D, 13.0D);

	VoxelShape MIDDLEBASE = Block.box(2.0D, 3.0D, 2.0D, 14.0D, 4.0D, 14.0D);
	VoxelShape MIDDLETOP = Block.box(2.0D, 11.0D, 2.0D, 14.0D, 12.0D, 14.0D);
	
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final IntegerProperty ENERGY = IntegerProperty.create("energy", 0, 14);
	
	public BlockCharger(Block.Properties properties) {
		super(properties);
		
		this.registerDefaultState(this.defaultBlockState().setValue(ENERGY, 0).setValue(FACING, Direction.NORTH));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityCharger(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, IndustryModBusManager.TILE_TYPE_CHARGER);
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityCharger> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityCharger::tick);
	}
	
	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) { 
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityCharger) {
			((BlockEntityCharger) tileEntity).attack(state, worldIn, pos, playerIn);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		super.use(state, worldIn, pos, playerIn, handIn, hit);
		
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityCharger) {
			return ((BlockEntityCharger) tileEntity).use(state, worldIn, pos, playerIn, handIn, hit);
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ENERGY, FACING);
	}
	
	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {	
		BlockEntityCharger tile = (BlockEntityCharger) worldIn.getBlockEntity(currentPos);
		
		return stateIn.setValue(ENERGY, tile.getEnergyScaled(14)).setValue(FACING, stateIn.getValue(FACING));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlockAndUpdate(pos, state.setValue(FACING, placer.getDirection().getOpposite()));
	}

	public BlockState rotate(BlockState stateIn, Rotation rotationIn) {
		return stateIn.setValue(FACING, rotationIn.rotate(stateIn.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState stateIn, Mirror mirrorIn) {
		return stateIn.rotate(mirrorIn.getRotation(stateIn.getValue(FACING)));
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockReader, BlockPos pos, CollisionContext context) {
		return Shapes.or(BASE, TOP, MIDDLE, MIDDLEBASE, MIDDLETOP);
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
}