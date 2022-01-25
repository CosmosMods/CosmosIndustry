package com.tcn.cosmosindustry.processing.core.block;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiserStand;
import com.tcn.cosmoslibrary.common.block.CosmosEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockSynthesiserStand extends CosmosEntityBlock {
	VoxelShape BASE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 4.0D, 15.0D);
	VoxelShape MIDDLE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);
	VoxelShape TOP = Block.box(2.0D, 12.0D, 2.0D, 14.0D, 14.0D, 14.0D);
	
	public BlockSynthesiserStand(Block.Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntitySynthesiserStand(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, IndustryModBusManager.TILE_TYPE_SYNTHESISER_STAND);
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntitySynthesiserStand> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntitySynthesiserStand::tick);
	}
	
	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) { 
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntitySynthesiserStand) {
			((BlockEntitySynthesiserStand) tileEntity).attack(state, worldIn, pos, playerIn);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntitySynthesiserStand) {
			return ((BlockEntitySynthesiserStand) tileEntity).use(state, worldIn, pos, playerIn, handIn, hit);
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockReader, BlockPos pos, CollisionContext context) {
		return Shapes.or(BASE, MIDDLE, TOP);
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