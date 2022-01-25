package com.tcn.cosmosindustry.processing.core.blockentity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryModBusManager;
import com.tcn.cosmosindustry.core.management.SoundManager;
import com.tcn.cosmosindustry.core.management.SynthesiserRecipeManager;
import com.tcn.cosmosindustry.processing.client.container.ContainerSynthesiser;
import com.tcn.cosmosindustry.processing.core.block.BlockSynthesiser;
import com.tcn.cosmosindustry.processing.core.block.BlockSynthesiserStand;
import com.tcn.cosmoslibrary.client.enums.EnumBERColour;
import com.tcn.cosmoslibrary.client.interfaces.IBlockEntityClientUpdated;
import com.tcn.cosmoslibrary.common.interfaces.IEnergyEntity;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;

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
import net.minecraft.world.level.block.Block;
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
public class BlockEntitySynthesiser extends BlockEntity implements IBlockInteract, Container, WorldlyContainer, MenuProvider, IBlockEntityClientUpdated.Processing, IEnergyEntity {

	private static final int[] SLOTS_BOTTOM = new int[] { 0 };
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);

	private int update = 0;
	private int sound_timer = 0;
	private int process_time;
	private int process_speed = IndustryReference.RESOURCE.PROCESSING.SPEED_RATE[0];
	
	private int energy_stored = 0;
	private int energy_capacity = IndustryReference.RESOURCE.PROCESSING.CAPACITY_U[0];
	private int energy_max_receive = IndustryReference.RESOURCE.PROCESSING.MAX_INPUT_U[0];
	private int energy_max_extract = IndustryReference.RESOURCE.PROCESSING.MAX_INPUT_U[0];
	private int rf_tick_rate = IndustryReference.RESOURCE.PROCESSING.RF_TICK_RATE_U[4];
	
	public BlockEntitySynthesiser(BlockPos posIn, BlockState stateIn) {
		super(IndustryModBusManager.TILE_TYPE_SYNTHESISER, posIn, stateIn);
	}

	public void sendUpdates() {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			BlockSynthesiser block = (BlockSynthesiser) state.getBlock();
			
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
	}

	@Override
	public void onLoad() { }
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntitySynthesiser entityIn) {
		BlockPos pos = entityIn.getBlockPos();
		
		if (entityIn.canProcessEightWay()) {
			entityIn.sound_timer++;
			
			if (entityIn.sound_timer > 13) {
				entityIn.level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundManager.MACHINE.LASERHUM, SoundSource.BLOCKS, 1F, 1F, false);
				
				entityIn.sound_timer = 0;
			}
			
			entityIn.process_time++;
			
			if (entityIn.process_time == entityIn.getProcessTimeEightWay()) {
				entityIn.process_time = 0;
				entityIn.processEightWay();
				entityIn.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.getY() + 1.5D, pos.getZ() + 0.5, 1.0D, 1.0D, 1.0D);
			}
			
		} else if (entityIn.canProcessFourWay()) {
			entityIn.sound_timer++;
			
			if (entityIn.sound_timer > 13) {
				entityIn.level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundManager.MACHINE.LASERHUM, SoundSource.BLOCKS, 1F, 1F, false);
				
				entityIn.sound_timer = 0;
			}
			
			entityIn.process_time++;
			
			if (entityIn.process_time == entityIn.getProcessTimeFourWay()) {
				entityIn.process_time = 0;
				entityIn.processFourWay();
				entityIn.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.getY() + 1.5D, pos.getZ() + 0.5, 1.0D, 1.0D, 1.0D);
			}
		} else {
			entityIn.sound_timer = 0;
			entityIn.process_time = 0;
		}
		
		if (entityIn.process_time > 0) {
			//entityIn.sendUpdates();
		}
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (!playerIn.isShiftKeyDown()) {
			if ((!CosmosUtil.handEmpty(playerIn)) && (this.getItem(0).isEmpty())) {
				ItemStack stack = playerIn.getItemInHand(handIn);
	
				worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2F, 2F, false);
				
				this.setItem(0, stack.copy());
				stack.shrink(1);
				return InteractionResult.SUCCESS;
			} else if (!this.getItem(0).isEmpty()) {
				playerIn.addItem(this.getItem(0));
				this.setItem(0, ItemStack.EMPTY);
				worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2F, 2F, false);
				return InteractionResult.SUCCESS;
			}
		}	
			
		else if (!CosmosUtil.holdingWrench(playerIn)) {
			if (playerIn instanceof ServerPlayer) {
				NetworkHooks.openGui((ServerPlayer) playerIn, this, (packetBuffer)->{ packetBuffer.writeBlockPos(this.getBlockPos()); });
			}
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.FAIL;
	}

	@Override
	public boolean isProcessing() {
		return this.hasEnergy() && this.process_time > 0;
	}
	
	public boolean canProcessFourWay() {
		if (this.isSetupFourWay() && this.hasEnergy()) {
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesFourWay();
			
			if (!tiles.isEmpty()) {
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				List<ItemStack> list = Lists.newArrayList();
				ItemStack output_stack = this.getItem(0);
				
				for (int x = 0; x < tiles.size(); x++) {
					if (tiles.get(x) instanceof BlockEntitySynthesiserStand) {
						stacks.add(((BlockEntitySynthesiserStand) tiles.get(x)).getItem(0));
					}
				}
				
				for (int j = 0; j < stacks.size(); j++) {
					if (!(stacks.get(j).isEmpty())) {
						list.add(stacks.get(j));
					}
				}
				
				ItemStack result_stack = SynthesiserRecipeManager.getInstance().findMatchingRecipe(list);
				if (!(result_stack.isEmpty()) && !(output_stack.isEmpty())) {
					if (ItemStack.matches(output_stack, SynthesiserRecipeManager.getInstance().findFocusStack(list))) {
						return true;
					}
				}
				return false;
			}	
		}
		return false;
	}
	
	public boolean canProcessEightWay() {
		if (this.isSetupEightWay() && this.hasEnergy()) {
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesEightWay();
			
			if (!tiles.isEmpty()) {
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				List<ItemStack> list = Lists.newArrayList();
				ItemStack output_stack = this.getItem(0);
				
				for (int x = 0; x < tiles.size(); x++) {
					if (tiles.get(x) instanceof BlockEntitySynthesiserStand) {
						stacks.add(((BlockEntitySynthesiserStand) tiles.get(x)).getItem(0));
					}
				}
				
				for (int j = 0; j < stacks.size(); j++) {
					if (!(stacks.get(j).isEmpty())) {
						list.add(stacks.get(j));
					}
				}
				
				ItemStack result_stack = SynthesiserRecipeManager.getInstance().findMatchingRecipe(list);
				if (!(result_stack.isEmpty()) && !(output_stack.isEmpty())) {
					if (ItemStack.matches(output_stack, SynthesiserRecipeManager.getInstance().findFocusStack(list))) {
						return true;
					}
				}
				return false;
			}	
		}
		return false;
	}
	
	public void processFourWay() {
		if (this.isSetupFourWay() && this.canProcessFourWay()) {
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesFourWay();
			
			if (!(tiles.isEmpty())) {
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				List<ItemStack> list = Lists.newArrayList();
				ItemStack output_stack = this.getItem(0);
				
				for (int x = 0; x < tiles.size(); x++) {
					if (tiles.get(x) instanceof BlockEntitySynthesiserStand) {
						stacks.add(((BlockEntitySynthesiserStand) tiles.get(x)).getItem(0));
					}
				}
				
				for (int j = 0; j < stacks.size(); j++) {
					if (!(stacks.get(j).isEmpty())) {
						list.add(stacks.get(j));
					}
				}
				
				ItemStack result_stack = SynthesiserRecipeManager.getInstance().findMatchingRecipe(list);
				if (!(result_stack.isEmpty()) && !(output_stack.isEmpty())) {
					if (ItemStack.matches(output_stack, SynthesiserRecipeManager.getInstance().findFocusStack(list))) {
						output_stack.shrink(1);
						
						this.setItem(0, result_stack.copy());
						
						for (int i = 0; i < stacks.size(); i++) {
							stacks.get(i).shrink(1);
						}
					}
				}
			}
		}
		
		this.sendUpdates();
	}
	
	public void processEightWay() {
		if (this.isSetupEightWay() && this.canProcessEightWay()) {
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesEightWay();
			
			if (!(tiles.isEmpty())) {
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				List<ItemStack> list = Lists.newArrayList();
				ItemStack output_stack = this.getItem(0);
				
				for (int x = 0; x < tiles.size(); x++) {
					if (tiles.get(x) instanceof BlockEntitySynthesiserStand) {
						stacks.add(((BlockEntitySynthesiserStand) tiles.get(x)).getItem(0));
					}
				}
				
				for (int j = 0; j < stacks.size(); j++) {
					if (!(stacks.get(j).isEmpty())) {
						list.add(stacks.get(j));
					}
				}
				
				ItemStack result_stack = SynthesiserRecipeManager.getInstance().findMatchingRecipe(list);
				if (!(result_stack.isEmpty()) && !(output_stack.isEmpty())) {
					if (ItemStack.matches(output_stack, SynthesiserRecipeManager.getInstance().findFocusStack(list))) {
						output_stack.shrink(1);
						
						this.setItem(0, result_stack.copy());
						
						for (int i = 0; i < stacks.size(); i++) {
							stacks.get(i).shrink(1);
						}
					}
				}
			}
		}
		
		this.sendUpdates();
	}
	
	public Integer getProcessTimeFourWay() {
		if (this.isSetupFourWay()) {
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesFourWay();
			
			if (!tiles.isEmpty()) {
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				List<ItemStack> list = Lists.newArrayList();
				ItemStack output_stack = this.getItem(0);
				
				for (int x = 0; x < tiles.size(); x++) {
					if (tiles.get(x) instanceof BlockEntitySynthesiserStand) {
						stacks.add(((BlockEntitySynthesiserStand) tiles.get(x)).getItem(0));
					}
				}
				
				for (int j = 0; j < stacks.size(); j++) {
					if (!(stacks.get(j).isEmpty())) {
						list.add(stacks.get(j));
					}
				}
				
				ItemStack result_stack = SynthesiserRecipeManager.getInstance().findMatchingRecipe(list);
				if (!(result_stack.isEmpty()) && !(output_stack.isEmpty())) {
					if (ItemStack.matches(output_stack, SynthesiserRecipeManager.getInstance().findFocusStack(list))) {
						return SynthesiserRecipeManager.getInstance().findProcessTime(list);
					}
				}
				return 0;
			}
		}
		return 0;
	}
	
	public Integer getProcessTimeEightWay() {
		if (this.isSetupEightWay()) {
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesEightWay();
			
			if (!tiles.isEmpty()) {
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				List<ItemStack> list = Lists.newArrayList();
				ItemStack output_stack = this.getItem(0);
				
				for (int x = 0; x < tiles.size(); x++) {
					if (tiles.get(x) instanceof BlockEntitySynthesiserStand) {
						stacks.add(((BlockEntitySynthesiserStand) tiles.get(x)).getItem(0));
					}
				}
				
				for (int j = 0; j < stacks.size(); j++) {
					if (!(stacks.get(j).isEmpty())) {
						list.add(stacks.get(j));
					}
				}
				
				ItemStack result_stack = SynthesiserRecipeManager.getInstance().findMatchingRecipe(list);
				if (!(result_stack.isEmpty()) && !(output_stack.isEmpty())) {
					if (ItemStack.matches(output_stack, SynthesiserRecipeManager.getInstance().findFocusStack(list))) {
						return SynthesiserRecipeManager.getInstance().findProcessTime(list);
					}
				}
				return 0;
			}
		}
		return 0;
	}
	
	public boolean isSetupFourWay() {
		ArrayList<BlockEntity> tiles = this.getBlockEntitiesFourWay();
		ArrayList<Block> blocks = this.getBlocksFourWay();
		
		if (tiles.get(0) instanceof BlockEntitySynthesiserStand && tiles.get(1) instanceof BlockEntitySynthesiserStand &&
				tiles.get(2) instanceof BlockEntitySynthesiserStand && tiles.get(3) instanceof BlockEntitySynthesiserStand) {
			if (blocks.get(0) instanceof BlockSynthesiserStand && blocks.get(1) instanceof BlockSynthesiserStand &&
					blocks.get(2) instanceof BlockSynthesiserStand && blocks.get(3) instanceof BlockSynthesiserStand) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public boolean isSetupEightWay() {
		ArrayList<BlockEntity> tiles = this.getBlockEntitiesEightWay();
		ArrayList<Block> blocks = this.getBlocksEightWay();
		
		if (tiles.get(0) instanceof BlockEntitySynthesiserStand && tiles.get(1) instanceof BlockEntitySynthesiserStand &&
				tiles.get(2) instanceof BlockEntitySynthesiserStand && tiles.get(3) instanceof BlockEntitySynthesiserStand &&
					tiles.get(4) instanceof BlockEntitySynthesiserStand && tiles.get(5) instanceof BlockEntitySynthesiserStand &&
						tiles.get(6) instanceof BlockEntitySynthesiserStand && tiles.get(7) instanceof BlockEntitySynthesiserStand) {
			if (blocks.get(0) instanceof BlockSynthesiserStand && blocks.get(1) instanceof BlockSynthesiserStand &&
					blocks.get(2) instanceof BlockSynthesiserStand && blocks.get(3) instanceof BlockSynthesiserStand && 
						blocks.get(4) instanceof BlockSynthesiserStand && blocks.get(5) instanceof BlockSynthesiserStand &&
							blocks.get(6) instanceof BlockSynthesiserStand && blocks.get(7) instanceof BlockSynthesiserStand) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public ArrayList<BlockEntity> getBlockEntitiesFourWay() {
		ArrayList<BlockEntity> tiles = new ArrayList<BlockEntity>();
		
		for (Direction c : Direction.values()) {
			if (c != Direction.UP && c != Direction.DOWN) {
				tiles.add(this.level.getBlockEntity(this.getBlockPos().relative(c, 3)));
			}
		}
		return tiles;
	}
	
	public ArrayList<Block> getBlocksFourWay() {
		ArrayList<Block> blocks = new ArrayList<Block>();
		
		for (Direction c : Direction.values()) {
			if (c != Direction.UP && c != Direction.DOWN) {
				blocks.add(this.level.getBlockState(this.getBlockPos().relative(c, 3)).getBlock());
			}
		}
		return blocks;
	}
	
	public ArrayList<BlockEntity> getBlockEntitiesEightWay() {
		ArrayList<BlockEntity> tiles = new ArrayList<BlockEntity>();
		
		for (Direction c : Direction.values()) {
			if (c != Direction.UP && c != Direction.DOWN) {
				tiles.add(this.level.getBlockEntity(this.getBlockPos().relative(c, 3)));
				
				if (c.equals(Direction.NORTH) || c.equals(Direction.SOUTH)) {
					tiles.add(this.level.getBlockEntity(this.getBlockPos().relative(c, 2).relative(Direction.WEST, 2)));
					tiles.add(this.level.getBlockEntity(this.getBlockPos().relative(c, 2).relative(Direction.EAST, 2)));
				}
			}
		}
		return tiles;
	}
	
	public ArrayList<Block> getBlocksEightWay() {
		ArrayList<Block> blocks = new ArrayList<Block>();
		
		for (Direction c : Direction.values()) {
			if (c != Direction.UP && c != Direction.DOWN) {
				blocks.add(this.level.getBlockState(this.getBlockPos().relative(c, 3)).getBlock());
				
				if (c.equals(Direction.NORTH) || c.equals(Direction.SOUTH)) {
					blocks.add(this.level.getBlockState(this.getBlockPos().relative(c, 2).relative(Direction.WEST, 2)).getBlock());
					blocks.add(this.level.getBlockState(this.getBlockPos().relative(c, 2).relative(Direction.EAST, 2)).getBlock());
				}
			}
		}
		return blocks;
	}
	
	public EnumBERColour getColour() {
		if (this.isSetupEightWay()) {
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesEightWay();
			
			if (!tiles.isEmpty()) {
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				List<ItemStack> list = Lists.newArrayList();
				ItemStack output_stack = this.getItem(0);
				
				for (int x = 0; x < tiles.size(); x++) {
					if (tiles.get(x) instanceof BlockEntitySynthesiserStand) {
						stacks.add(((BlockEntitySynthesiserStand) tiles.get(x)).getItem(0));
					}
				}
				
				for (int j = 0; j < stacks.size(); j++) {
					if (!(stacks.get(j).isEmpty())) {
						list.add(stacks.get(j));
					}
				}
				
				ItemStack result_stack = SynthesiserRecipeManager.getInstance().findMatchingRecipe(list);
				if (!(result_stack.isEmpty()) && !(output_stack.isEmpty())) {
					if (ItemStack.matches(output_stack, SynthesiserRecipeManager.getInstance().findFocusStack(list))) {
						return SynthesiserRecipeManager.getInstance().findColour(list);
					}
				}
				return EnumBERColour.WHITE;
			}
		} else if (this.isSetupFourWay()) {
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesFourWay();
			
			if (!tiles.isEmpty()) {
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				List<ItemStack> list = Lists.newArrayList();
				ItemStack output_stack = this.getItem(0);
				
				for (int x = 0; x < tiles.size(); x++) {
					if (tiles.get(x) instanceof BlockEntitySynthesiserStand) {
						stacks.add(((BlockEntitySynthesiserStand) tiles.get(x)).getItem(0));
					}
				}
				
				for (int j = 0; j < stacks.size(); j++) {
					if (!(stacks.get(j).isEmpty())) {
						list.add(stacks.get(j));
					}
				}
				
				ItemStack result_stack = SynthesiserRecipeManager.getInstance().findMatchingRecipe(list);
				if (!(result_stack.isEmpty()) && !(output_stack.isEmpty())) {
					if (ItemStack.matches(output_stack, SynthesiserRecipeManager.getInstance().findFocusStack(list))) {
						return SynthesiserRecipeManager.getInstance().findColour(list);
					}
				}
				return EnumBERColour.WHITE;
			}
		}
		return EnumBERColour.WHITE;
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
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public ItemStack getItem(int index) {
		return this.inventoryItems.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		this.sendUpdates();
		return ContainerHelper.removeItem(this.inventoryItems, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		this.sendUpdates();
		return ContainerHelper.takeItem(this.inventoryItems, index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.inventoryItems.set(index, stack);
		
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		
		this.sendUpdates();
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
		return side == Direction.DOWN ? SLOTS_BOTTOM : new int[] { -1 };
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.locComp("cosmosindustry.gui.synthesiser");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerSynthesiser(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
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
                return BlockEntitySynthesiser.this.extractEnergy(directionIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntitySynthesiser.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntitySynthesiser.this.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BlockEntitySynthesiser.this.receiveEnergy(directionIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntitySynthesiser.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntitySynthesiser.this.canExtract(directionIn);
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
		
		this.sendUpdates();
		return storedReceived;
	}

	@Override
	public int extractEnergy(Direction directionIn, int max_extract, boolean simulate) {
		int storedExtracted = Math.min(energy_stored, Math.min(this.energy_max_extract, max_extract));

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
		if (directionIn.getOpposite().equals(Direction.DOWN)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getProcessSpeed() {
		return 0;
	}

	@Override
	public int getProcessTime(int i) {
		return 0;
	}

	@Override
	public int getProcessProgressScaled(int scale) {
		return 0;
	}

	@Override
	public boolean canProcess() {
		return false;
	}

	@Override
	public void processItem() { }

	@Override
	public int getRFTickRate() {
		return this.rf_tick_rate;
	}
}