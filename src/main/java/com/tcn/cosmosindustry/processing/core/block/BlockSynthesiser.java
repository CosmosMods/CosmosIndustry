package com.tcn.cosmosindustry.processing.core.block;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiser;
import com.tcn.cosmoslibrary.common.nbt.CosmosBlockRemovableNBT;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockSynthesiser extends CosmosBlockRemovableNBT implements EntityBlock {
	VoxelShape BASE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
	VoxelShape MIDDLE = Block.box(2.0F, 5.0F, 2.0F, 14.0F, 13.0F, 14.0F);
	VoxelShape TOP = Block.box(1.0D, 13.0D, 1.0D, 15.0D, 16.0D, 15.0D);
	
	public static final IntegerProperty ENERGY = IntegerProperty.create("energy", 0, 14);
	
	public BlockSynthesiser(Block.Properties properties) {
		super(properties);
		
		this.registerDefaultState(this.defaultBlockState().setValue(ENERGY, 0));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntitySynthesiser(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, IndustryModBusManager.TILE_TYPE_SYNTHESISER);
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntitySynthesiser> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntitySynthesiser::tick);
	}
	
	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) { 
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntitySynthesiser) {
			((BlockEntitySynthesiser) tileEntity).attack(state, worldIn, pos, playerIn);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		super.use(state, worldIn, pos, playerIn, handIn, hit);
		
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntitySynthesiser) {
			return ((BlockEntitySynthesiser) tileEntity).use(state, worldIn, pos, playerIn, handIn, hit);
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ENERGY);
	}
	
	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {	
		BlockEntitySynthesiser tile = (BlockEntitySynthesiser) worldIn.getBlockEntity(currentPos);

		return stateIn.setValue(ENERGY, tile.getEnergyScaled(14));
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

	@Override
	public void playerDestroy(Level levelIn, Player playerIn, BlockPos posIn, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stackIn) {
		super.playerDestroy(levelIn, playerIn, posIn, state, blockEntity, stackIn);
	}
}