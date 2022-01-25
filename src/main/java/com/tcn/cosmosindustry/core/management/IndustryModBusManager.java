package com.tcn.cosmosindustry.core.management;

import com.google.common.base.Preconditions;
import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.client.screen.ScreenConfiguration;
import com.tcn.cosmosindustry.processing.client.container.ContainerCharger;
import com.tcn.cosmosindustry.processing.client.container.ContainerCompactor;
import com.tcn.cosmosindustry.processing.client.container.ContainerGrinder;
import com.tcn.cosmosindustry.processing.client.container.ContainerKiln;
import com.tcn.cosmosindustry.processing.client.container.ContainerLaserCutter;
import com.tcn.cosmosindustry.processing.client.container.ContainerSeparator;
import com.tcn.cosmosindustry.processing.client.container.ContainerSynthesiser;
import com.tcn.cosmosindustry.processing.client.container.ContainerSynthesiserStand;
import com.tcn.cosmosindustry.processing.client.renderer.ber.RendererGrinder;
import com.tcn.cosmosindustry.processing.client.renderer.ber.RendererKiln;
import com.tcn.cosmosindustry.processing.client.renderer.ber.RendererSynthesiser;
import com.tcn.cosmosindustry.processing.client.renderer.ber.RendererSynthesiserStand;
import com.tcn.cosmosindustry.processing.client.screen.ScreenCharger;
import com.tcn.cosmosindustry.processing.client.screen.ScreenCompactor;
import com.tcn.cosmosindustry.processing.client.screen.ScreenGrinder;
import com.tcn.cosmosindustry.processing.client.screen.ScreenKiln;
import com.tcn.cosmosindustry.processing.client.screen.ScreenLaserCutter;
import com.tcn.cosmosindustry.processing.client.screen.ScreenSeparator;
import com.tcn.cosmosindustry.processing.client.screen.ScreenSynthesiser;
import com.tcn.cosmosindustry.processing.core.block.BlockCharger;
import com.tcn.cosmosindustry.processing.core.block.BlockCompactor;
import com.tcn.cosmosindustry.processing.core.block.BlockGrinder;
import com.tcn.cosmosindustry.processing.core.block.BlockKiln;
import com.tcn.cosmosindustry.processing.core.block.BlockLaserCutter;
import com.tcn.cosmosindustry.processing.core.block.BlockSeparator;
import com.tcn.cosmosindustry.processing.core.block.BlockStructure;
import com.tcn.cosmosindustry.processing.core.block.BlockSynthesiser;
import com.tcn.cosmosindustry.processing.core.block.BlockSynthesiserStand;
import com.tcn.cosmosindustry.processing.core.block.ItemBlockCharger;
import com.tcn.cosmosindustry.processing.core.block.ItemBlockCompactor;
import com.tcn.cosmosindustry.processing.core.block.ItemBlockGrinder;
import com.tcn.cosmosindustry.processing.core.block.ItemBlockKiln;
import com.tcn.cosmosindustry.processing.core.block.ItemBlockLaserCutter;
import com.tcn.cosmosindustry.processing.core.block.ItemBlockSeparator;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityCharger;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityCompactor;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityGrinder;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityKiln;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityLaserCutter;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySeparator;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiser;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiserStand;
import com.tcn.cosmosindustry.storage.client.container.ContainerCapacitor;
import com.tcn.cosmosindustry.storage.client.screen.ScreenCapacitor;
import com.tcn.cosmosindustry.storage.core.block.BlockCapacitor;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityCapacitor;
import com.tcn.cosmosindustry.transport.client.ter.RendererEnergyChannel;
import com.tcn.cosmosindustry.transport.client.ter.RendererEnergyChannelSurge;
import com.tcn.cosmosindustry.transport.client.ter.RendererEnergyChannelTransparent;
import com.tcn.cosmosindustry.transport.client.ter.RendererEnergyChannelTransparentSurge;
import com.tcn.cosmosindustry.transport.core.block.BlockChannelEnergy;
import com.tcn.cosmosindustry.transport.core.block.BlockChannelSurgeEnergy;
import com.tcn.cosmosindustry.transport.core.block.BlockChannelTransparentEnergy;
import com.tcn.cosmosindustry.transport.core.block.BlockChannelTransparentSurgeEnergy;
import com.tcn.cosmosindustry.transport.core.blockentity.BlockEntityChannelEnergy;
import com.tcn.cosmosindustry.transport.core.blockentity.BlockEntityChannelSurgeEnergy;
import com.tcn.cosmosindustry.transport.core.blockentity.BlockEntityChannelTransparentEnergy;
import com.tcn.cosmosindustry.transport.core.blockentity.BlockEntityChannelTransparentSurgeEnergy;
import com.tcn.cosmoslibrary.common.block.CosmosBlock;
import com.tcn.cosmoslibrary.common.block.CosmosBlockConnectedGlass;
import com.tcn.cosmoslibrary.common.block.CosmosItemBlock;
import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.item.CosmosItemTool;
import com.tcn.cosmoslibrary.common.item.CosmosItemUpgradeEnergy;
import com.tcn.cosmoslibrary.common.item.CosmosItemUpgradeFluid;
import com.tcn.cosmoslibrary.common.runtime.CosmosRuntimeHelper;
import com.tcn.cosmoslibrary.common.tab.CosmosCreativeModeTab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = CosmosIndustry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IndustryModBusManager {

	public static final CosmosCreativeModeTab BLOCKS_GROUP = new CosmosCreativeModeTab(CosmosIndustry.MOD_ID + ".blocks", () -> new ItemStack(IndustryModBusManager.BLOCK_ORE_TIN));
	public static final CosmosCreativeModeTab ITEMS_GROUP = new CosmosCreativeModeTab(CosmosIndustry.MOD_ID + ".items", () -> new ItemStack(IndustryModBusManager.MACHINE_WRENCH));
	public static final CosmosCreativeModeTab DEVICES_GROUP = new CosmosCreativeModeTab(CosmosIndustry.MOD_ID + ".tools", () -> new ItemStack(IndustryModBusManager.BLOCK_STRUCTURE));
	
	/** -- ITEM START -- */
	public static final Item COPPER_DUST = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item COPPER_PLATE = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item TIN_INGOT = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item TIN_DUST = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item TIN_PLATE = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item SILVER_INGOT = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item SILVER_DUST = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item SILVER_PLATE = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item BRONZE_INGOT = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item BRONZE_DUST = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item BRONZE_PLATE = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item BRASS_INGOT = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item BRASS_DUST = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item BRASS_PLATE = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item LAPIS_INGOT = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item SILICON_INGOT = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item STEEL_INGOT = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item STEEL_INGOT_UNREFINED = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item STEEL_DUST = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item STEEL_PLATE = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item STEEL_ROD = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item IRON_DUST = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item IRON_DUST_REFINE = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item IRON_PLATE = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item GOLD_DUST = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item GOLD_DUST_REFINE = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item GOLD_PLATE = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item DIAMOND_DUST = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item DIAMOND_PLATE = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item STONE_DUST = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item ENERGY_INGOT = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item ENERGY_DUST = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item ENERGY_WAFER = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item CIRCUIT_RAW = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP).stacksTo(32));
	public static final Item CIRCUIT = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP).stacksTo(32));
	public static final Item CIRCUIT_ADVANCED = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP).stacksTo(8));
	public static final Item CIRCUIT_ADVANCED_RAW = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP).stacksTo(8));
	
	public static final Item SILICON = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item SILICON_REFINED = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item SILICON_WAFER = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item UPGRADE_BASE = new CosmosItemUpgradeEnergy(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item UPGRADE_SPEED = new CosmosItemUpgradeEnergy(new Item.Properties().tab(ITEMS_GROUP).stacksTo(8));
	public static final Item UPGRADE_CAPACITY = new CosmosItemUpgradeEnergy(new Item.Properties().tab(ITEMS_GROUP).stacksTo(8));
	public static final Item UPGRADE_EFFICIENCY = new CosmosItemUpgradeEnergy(new Item.Properties().tab(ITEMS_GROUP).stacksTo(8));
	
	public static final Item UPGRADE_FLUID_SPEED = new CosmosItemUpgradeFluid(new Item.Properties().tab(ITEMS_GROUP).stacksTo(8));
	public static final Item UPGRADE_FLUID_CAPACITY = new CosmosItemUpgradeFluid(new Item.Properties().tab(ITEMS_GROUP).stacksTo(8));
	public static final Item UPGRADE_FLUID_EFFICIENCY = new CosmosItemUpgradeFluid(new Item.Properties().tab(ITEMS_GROUP).stacksTo(8));
	
	public static final Item RUBBER = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item RUBBER_INSULATION = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	public static final Item TOOL_ROD = new CosmosItem(new Item.Properties().tab(ITEMS_GROUP));
	
	public static final Item MACHINE_WRENCH = new CosmosItemTool(new Item.Properties().tab(ITEMS_GROUP));
	
	/** -- BLOCK START -- */
	public static final Block BLOCK_GLASS_BLACK = new CosmosBlockConnectedGlass(Block.Properties.of(Material.GLASS).strength(1.5F, 1.5F).noOcclusion());
	
	public static final Block BLOCK_ORE_TIN = new CosmosBlock(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
	public static final Block BLOCK_ORE_SILVER = new CosmosBlock(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
	public static final Block BLOCK_ORE_SILICON = new CosmosBlock(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
	
	public static final Block BLOCK_TIN = new CosmosBlock(Block.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F));
	public static final Block BLOCK_SILVER = new CosmosBlock(Block.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F));
	public static final Block BLOCK_SILICON = new CosmosBlock(Block.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F));
	
	public static final Block BLOCK_STEEL = new CosmosBlock(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(8.0F, 10.0F));
	public static final Block BLOCK_BRONZE = new CosmosBlock(Block.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F));
	public static final Block BLOCK_BRASS = new CosmosBlock(Block.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F));
	
	/** - Processing - */
	public static final Block BLOCK_STRUCTURE = new BlockStructure(Block.Properties.of(Material.HEAVY_METAL));
	public static final BlockItem ITEMBLOCK_STRUCTURE = new CosmosItemBlock(BLOCK_STRUCTURE, new Item.Properties().tab(DEVICES_GROUP), "Base block to craft machines.", "", "");
	
	public static final Block BLOCK_KILN = new BlockKiln(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape());
	public static final BlockItem ITEMBLOCK_KILN = new ItemBlockKiln(BLOCK_KILN, new Item.Properties().tab(DEVICES_GROUP), "A machine to smelt things.", "Smelts things using RF power.", "Can be upgraded internally.");
	public static BlockEntityType<BlockEntityKiln> TILE_TYPE_KILN;
	public static MenuType<ContainerKiln> CONTAINER_TYPE_KILN;
	
	public static final Block BLOCK_GRINDER = new BlockGrinder(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape());
	public static final BlockItem ITEMBLOCK_GRINDER = new ItemBlockGrinder(BLOCK_GRINDER, new Item.Properties().tab(DEVICES_GROUP), "A machine to grind things.", "Grinds things using RF power.", "Can be upgraded internally.");
	public static BlockEntityType<BlockEntityGrinder> TILE_TYPE_GRINDER;
	public static MenuType<ContainerGrinder> CONTAINER_TYPE_GRINDER;
	
	public static final Block BLOCK_COMPACTOR = new BlockCompactor(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape());
	public static final BlockItem ITEMBLOCK_COMPACTOR = new ItemBlockCompactor(BLOCK_COMPACTOR, new Item.Properties().tab(DEVICES_GROUP), "A machine to compact things.", "Compacts things using RF power.", "Can be upgraded internally.");
	public static BlockEntityType<BlockEntityCompactor> TILE_TYPE_COMPACTOR;
	public static MenuType<ContainerCompactor> CONTAINER_TYPE_COMPACTOR;
	
	public static final Block BLOCK_CHARGER = new BlockCharger(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape());
	public static final BlockItem ITEMBLOCK_CHARGER = new ItemBlockCharger(BLOCK_CHARGER, new Item.Properties().tab(DEVICES_GROUP), "Charges Items.", "Charges any RF enabled Item.", "Can be upgraded internally.");
	public static BlockEntityType<BlockEntityCharger> TILE_TYPE_CHARGER;
	public static MenuType<ContainerCharger> CONTAINER_TYPE_CHARGER;
	
	public static final Block BLOCK_SEPARATOR = new BlockSeparator(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape());
	public static final BlockItem ITEMBLOCK_SEPARATOR = new ItemBlockSeparator(BLOCK_SEPARATOR, new Item.Properties().tab(DEVICES_GROUP), "Separates Items.", "Separates items into other items using RF power", "Can be upgraded internally.");
	public static BlockEntityType<BlockEntitySeparator> TILE_TYPE_SEPARATOR;
	public static MenuType<ContainerSeparator> CONTAINER_TYPE_SEPARATOR;

	public static final Block BLOCK_LASER_CUTTER = new BlockLaserCutter(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape());
	public static final BlockItem ITEMBLOCK_LASER_CUTTER = new ItemBlockLaserCutter(BLOCK_LASER_CUTTER, new Item.Properties().tab(DEVICES_GROUP), "A machine to cut things with a laser.", "Laser cuts things using RF power.", "Can be upgraded internally.");
	public static BlockEntityType<BlockEntityLaserCutter> TILE_TYPE_LASER_CUTTER;
	public static MenuType<ContainerLaserCutter> CONTAINER_TYPE_LASER_CUTTER;
	
	public static final Block BLOCK_SYNTHESISER = new BlockSynthesiser(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().dynamicShape());
	public static final BlockItem ITEMBLOCK_SYNTHESISER = new CosmosItemBlock(BLOCK_SYNTHESISER, new Item.Properties().tab(DEVICES_GROUP), "Base block of the Synthesiser multiblock.", "Used in complex crafting.", "Requires Synthesiser Stands.");
	public static BlockEntityType<BlockEntitySynthesiser> TILE_TYPE_SYNTHESISER;
	public static MenuType<ContainerSynthesiser> CONTAINER_TYPE_SYNTHESISER;
	
	public static final Block BLOCK_SYNTHESISER_STAND = new BlockSynthesiserStand(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 10.0F).noOcclusion().noOcclusion().dynamicShape());
	public static final BlockItem ITEMBLOCK_SYNTHESISER_STAND = new CosmosItemBlock(BLOCK_SYNTHESISER_STAND, new Item.Properties().tab(DEVICES_GROUP), "Support block for the Synthesiser multiblock.", "Used in complex crafting.", "Requires a Synthesiser to use.");
	public static BlockEntityType<BlockEntitySynthesiserStand> TILE_TYPE_SYNTHESISER_STAND;
	public static MenuType<ContainerSynthesiserStand> CONTAINER_TYPE_SYNTHESISER_STAND;
	

	
	/** - Storage - */
	public static final Block BLOCK_CAPACITOR = new BlockCapacitor(Block.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(8.0F, 12.0F).noOcclusion().dynamicShape());
	public static final BlockItem ITEMBLOCK_CAPACITOR = new CosmosItemBlock(BLOCK_CAPACITOR, new Item.Properties().tab(DEVICES_GROUP), "", "", "");
	public static BlockEntityType<BlockEntityCapacitor> BLOCK_ENTITY_TYPE_CAPACITOR;
	public static MenuType<ContainerCapacitor> CONTAINER_TYPE_CAPACITOR;
	
	
	
	/** - Transport - */
	public static final Block BLOCK_ENERGY_CHANNEL = new BlockChannelEnergy(Block.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(8.0F, 12.0F));
	public static final BlockItem ITEMBLOCK_ENERGY_CHANNEL = new CosmosItemBlock(BLOCK_ENERGY_CHANNEL, new Item.Properties().tab(DEVICES_GROUP), "Basic block for transporting energy.", "Transports RF.", "Max transfer rate: " + IndustryReference.RESOURCE.TRANSPORT.ENERGY_MAX_TRANSFER + " RF/t.");
	public static BlockEntityType<BlockEntityChannelEnergy> BLOCK_ENTITY_TYPE_CHANNEL_ENERGY;

	public static final Block BLOCK_ENERGY_CHANNEL_SURGE = new BlockChannelSurgeEnergy(Block.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(8.0F, 12.0F));
	public static final BlockItem ITEMBLOCK_ENERGY_CHANNEL_SURGE = new CosmosItemBlock(BLOCK_ENERGY_CHANNEL_SURGE, new Item.Properties().tab(DEVICES_GROUP), "Advanced block for transporting energy.", "Transports RF.", "Max transfer rate: " + IndustryReference.RESOURCE.TRANSPORT.ENERGY_MAX_TRANSFER_SURGE + " RF/t.");
	public static BlockEntityType<BlockEntityChannelSurgeEnergy> BLOCK_ENTITY_TYPE_CHANNEL_SURGE_ENERGY;
	
	public static final Block BLOCK_ENERGY_CHANNEL_TRANSPARENT = new BlockChannelTransparentEnergy(Block.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(8.0F, 12.0F));
	public static final BlockItem ITEMBLOCK_ENERGY_CHANNEL_TRANSPARENT = new CosmosItemBlock(BLOCK_ENERGY_CHANNEL_TRANSPARENT, new Item.Properties().tab(DEVICES_GROUP), "Basic block for transporting energy.", "Transports RF. Clear to be able to see energy transfer.", "Max transfer rate: " + IndustryReference.RESOURCE.TRANSPORT.ENERGY_MAX_TRANSFER + " RF/t.");
	public static BlockEntityType<BlockEntityChannelTransparentEnergy> BLOCK_ENTITY_TYPE_CHANNEL_TRANSPARENT_ENERGY;
	
	public static final Block BLOCK_ENERGY_CHANNEL_TRANSPARENT_SURGE = new BlockChannelTransparentSurgeEnergy(Block.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(8.0F, 12.0F));
	public static final BlockItem ITEMBLOCK_ENERGY_CHANNEL_TRANSPARENT_SURGE = new CosmosItemBlock(BLOCK_ENERGY_CHANNEL_TRANSPARENT_SURGE, new Item.Properties().tab(DEVICES_GROUP), "Advanced block for transporting energy.", "Transports RF. Clear to be able to see energy transfer.", "Max transfer rate: " + IndustryReference.RESOURCE.TRANSPORT.ENERGY_MAX_TRANSFER_SURGE + " RF/t.");
	public static BlockEntityType<BlockEntityChannelTransparentSurgeEnergy> BLOCK_ENTITY_TYPE_CHANNEL_TRANSPARENT_SURGE_ENERGY;
	
	@SubscribeEvent
	public static void onItemRegistry(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		
		event.getRegistry().registerAll(
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "copper_dust", COPPER_DUST),	CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "copper_plate", COPPER_PLATE),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "tin_ingot", TIN_INGOT), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "tin_dust", TIN_DUST), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "tin_plate", TIN_PLATE),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "silver_ingot", SILVER_INGOT), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "silver_dust", SILVER_DUST), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "silver_plate", SILVER_PLATE),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "bronze_ingot", BRONZE_INGOT), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "bronze_dust", BRONZE_DUST), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "bronze_plate", BRONZE_PLATE),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "brass_ingot", BRASS_INGOT), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "brass_dust", BRASS_DUST), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "brass_plate", BRASS_PLATE),
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "lapis_ingot", LAPIS_INGOT), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "silicon_ingot", SILICON_INGOT),
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "steel_ingot", STEEL_INGOT), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "steel_ingot_unrefined", STEEL_INGOT_UNREFINED), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "steel_dust", STEEL_DUST),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "steel_plate", STEEL_PLATE), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "steel_rod", STEEL_ROD),

			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "iron_dust", IRON_DUST), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "iron_dust_refine", IRON_DUST_REFINE), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "iron_plate", IRON_PLATE),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "gold_dust", GOLD_DUST), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "gold_dust_refine", GOLD_DUST_REFINE), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "gold_plate", GOLD_PLATE),

			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "diamond_dust", DIAMOND_DUST), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "diamond_plate", DIAMOND_PLATE),
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "stone_dust", STONE_DUST),
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "energy_ingot", ENERGY_INGOT), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "energy_dust", ENERGY_DUST), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "energy_wafer", ENERGY_WAFER),
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "circuit", CIRCUIT), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "circuit_raw", CIRCUIT_RAW), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "circuit_advanced", CIRCUIT_ADVANCED), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "circuit_advanced_raw", CIRCUIT_ADVANCED_RAW),
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "silicon", SILICON), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "silicon_refined", SILICON_REFINED), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "silicon_wafer", SILICON_WAFER),
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "upgrade_base", UPGRADE_BASE), 
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "upgrade_speed", UPGRADE_SPEED), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "upgrade_capacity", UPGRADE_CAPACITY), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "upgrade_efficiency", UPGRADE_EFFICIENCY), 
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "upgrade_fluid_speed", UPGRADE_FLUID_SPEED), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "upgrade_fluid_capacity", UPGRADE_FLUID_CAPACITY), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "upgrade_fluid_efficiency", UPGRADE_FLUID_EFFICIENCY), 
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "rubber", RUBBER), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "rubber_insulation", RUBBER_INSULATION), 
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "tool_rod", TOOL_ROD), CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "machine_wrench", MACHINE_WRENCH)
		);
	
		//Register BlockItems
		for (final Block block : ForgeRegistries.BLOCKS.getValues()) {
			final ResourceLocation blockRegistryName = block.getRegistryName();
			Preconditions.checkNotNull(blockRegistryName, "Registry Name of Block \"" + block + "\" of class \"" + block.getClass().getName() + "\"is null! This is not allowed!");

			if (!blockRegistryName.getNamespace().equals(CosmosIndustry.MOD_ID)) {
				continue;
			}
			
			if (block instanceof BlockStructure) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_STRUCTURE));
			} else if (block instanceof BlockKiln) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_KILN));
			} else if (block instanceof BlockGrinder) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_GRINDER));
			} else if (block instanceof BlockCompactor) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_COMPACTOR));
			} else if (block instanceof BlockCharger) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_CHARGER));
			} else if (block instanceof BlockSeparator) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_SEPARATOR));
			} else if (block instanceof BlockLaserCutter) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_LASER_CUTTER));
			} else if (block instanceof BlockSynthesiser) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_SYNTHESISER));
			} else if (block instanceof BlockSynthesiserStand) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_SYNTHESISER_STAND));
			}
			
			else if (block instanceof BlockChannelEnergy) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_ENERGY_CHANNEL));
			} else if (block instanceof BlockChannelSurgeEnergy) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_ENERGY_CHANNEL_SURGE));
			} else if (block instanceof BlockChannelTransparentEnergy) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_ENERGY_CHANNEL_TRANSPARENT));
			} else if (block instanceof BlockChannelTransparentSurgeEnergy) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEMBLOCK_ENERGY_CHANNEL_TRANSPARENT_SURGE));
			}
			
			/*
			else if (block instanceof BlockEnergyChannel) {
				registry.register(ITEMBLOCK_ENERGY_CHANNEL);
			}
			*/
			
			else {
				final Item.Properties properties = new Item.Properties().tab(BLOCKS_GROUP);
				final BlockItem blockItem = new BlockItem(block, properties);
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, blockItem));
			}
		}
	}
	
	@SubscribeEvent
	public static void onBlockRegistry(final RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_ore_tin", BLOCK_ORE_TIN),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_ore_silver", BLOCK_ORE_SILVER),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_ore_silicon", BLOCK_ORE_SILICON),
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_tin", BLOCK_TIN),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_silver", BLOCK_SILVER), 
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_silicon", BLOCK_SILICON),

			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_steel", BLOCK_STEEL), 
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_bronze", BLOCK_BRONZE), 
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_brass", BLOCK_BRASS),
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_structure", BLOCK_STRUCTURE), 
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_kiln", BLOCK_KILN), 
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_grinder", BLOCK_GRINDER),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_compactor", BLOCK_COMPACTOR), 
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_charger", BLOCK_CHARGER), 
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_separator", BLOCK_SEPARATOR), 
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_laser_cutter", BLOCK_LASER_CUTTER),
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_synthesiser", BLOCK_SYNTHESISER),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_synthesiser_stand", BLOCK_SYNTHESISER_STAND),
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_energy_channel", BLOCK_ENERGY_CHANNEL),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_energy_channel_transparent", BLOCK_ENERGY_CHANNEL_TRANSPARENT),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_energy_channel_surge", BLOCK_ENERGY_CHANNEL_SURGE),
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_energy_channel_transparent_surge", BLOCK_ENERGY_CHANNEL_TRANSPARENT_SURGE),
			
			CosmosRuntimeHelper.setupString(CosmosIndustry.MOD_ID, "block_capacitor", BLOCK_CAPACITOR)
		);
	}
	
	@SubscribeEvent
	public static void onMenuTypeRegistry(final RegistryEvent.Register<MenuType<?>> event) {
		CONTAINER_TYPE_KILN = IForgeMenuType.create(ContainerKiln::new);
		CONTAINER_TYPE_KILN.setRegistryName(new ResourceLocation(CosmosIndustry.MOD_ID, "container_kiln"));
	
		CONTAINER_TYPE_GRINDER = IForgeMenuType.create(ContainerGrinder::new);
		CONTAINER_TYPE_GRINDER.setRegistryName(new ResourceLocation(CosmosIndustry.MOD_ID, "container_grinder"));
	
		CONTAINER_TYPE_COMPACTOR = IForgeMenuType.create(ContainerCompactor::new);
		CONTAINER_TYPE_COMPACTOR.setRegistryName(new ResourceLocation(CosmosIndustry.MOD_ID, "container_compactor"));
	
		CONTAINER_TYPE_CHARGER = IForgeMenuType.create(ContainerCharger::new);
		CONTAINER_TYPE_CHARGER.setRegistryName(new ResourceLocation(CosmosIndustry.MOD_ID, "container_charger"));

		CONTAINER_TYPE_SEPARATOR = IForgeMenuType.create(ContainerSeparator::new);
		CONTAINER_TYPE_SEPARATOR.setRegistryName(new ResourceLocation(CosmosIndustry.MOD_ID, "container_separator"));

		CONTAINER_TYPE_LASER_CUTTER = IForgeMenuType.create(ContainerLaserCutter::new);
		CONTAINER_TYPE_LASER_CUTTER.setRegistryName(new ResourceLocation(CosmosIndustry.MOD_ID, "container_laser_cutter"));

		CONTAINER_TYPE_SYNTHESISER = IForgeMenuType.create(ContainerSynthesiser::new);
		CONTAINER_TYPE_SYNTHESISER.setRegistryName(new ResourceLocation(CosmosIndustry.MOD_ID, "container_synthesiser"));

		CONTAINER_TYPE_SYNTHESISER_STAND = IForgeMenuType.create(ContainerSynthesiserStand::new);
		CONTAINER_TYPE_SYNTHESISER_STAND.setRegistryName(new ResourceLocation(CosmosIndustry.MOD_ID, "container_synthesiser_stand"));
		
		CONTAINER_TYPE_CAPACITOR = IForgeMenuType.create(ContainerCapacitor::new);
		CONTAINER_TYPE_CAPACITOR.setRegistryName(new ResourceLocation(CosmosIndustry.MOD_ID, "container_capacitor"));
		
		event.getRegistry().registerAll(
			CONTAINER_TYPE_KILN, CONTAINER_TYPE_GRINDER, CONTAINER_TYPE_COMPACTOR, CONTAINER_TYPE_CHARGER, CONTAINER_TYPE_SEPARATOR, CONTAINER_TYPE_LASER_CUTTER, 
			CONTAINER_TYPE_SYNTHESISER, CONTAINER_TYPE_SYNTHESISER_STAND,
			
			CONTAINER_TYPE_CAPACITOR
		);
		
		CosmosIndustry.CONSOLE.startup("MenuTypes Registered.");
	}
	
	@SubscribeEvent
	public static void onBlockEntityTypeRegistry(final RegistryEvent.Register<BlockEntityType<?>> event) {	
		TILE_TYPE_KILN = BlockEntityType.Builder.<BlockEntityKiln>of(BlockEntityKiln::new, BLOCK_KILN).build(null);
		TILE_TYPE_KILN.setRegistryName(CosmosIndustry.MOD_ID, "tile_entity_kiln");

		TILE_TYPE_GRINDER = BlockEntityType.Builder.<BlockEntityGrinder>of(BlockEntityGrinder::new, BLOCK_GRINDER).build(null);
		TILE_TYPE_GRINDER.setRegistryName(CosmosIndustry.MOD_ID, "tile_entity_grinder");

		TILE_TYPE_COMPACTOR = BlockEntityType.Builder.<BlockEntityCompactor>of(BlockEntityCompactor::new, BLOCK_COMPACTOR).build(null);
		TILE_TYPE_COMPACTOR.setRegistryName(CosmosIndustry.MOD_ID, "tile_entity_compactor");
		
		TILE_TYPE_CHARGER = BlockEntityType.Builder.<BlockEntityCharger>of(BlockEntityCharger::new, BLOCK_CHARGER).build(null);
		TILE_TYPE_CHARGER.setRegistryName(CosmosIndustry.MOD_ID, "tile_entity_charger");

		TILE_TYPE_SEPARATOR = BlockEntityType.Builder.<BlockEntitySeparator>of(BlockEntitySeparator::new, BLOCK_SEPARATOR).build(null);
		TILE_TYPE_SEPARATOR.setRegistryName(CosmosIndustry.MOD_ID, "tile_entity_separator");

		TILE_TYPE_LASER_CUTTER = BlockEntityType.Builder.<BlockEntityLaserCutter>of(BlockEntityLaserCutter::new, BLOCK_LASER_CUTTER).build(null);
		TILE_TYPE_LASER_CUTTER.setRegistryName(CosmosIndustry.MOD_ID, "tile_entity_laser_cutter");

		TILE_TYPE_SYNTHESISER = BlockEntityType.Builder.<BlockEntitySynthesiser>of(BlockEntitySynthesiser::new, BLOCK_SYNTHESISER).build(null);
		TILE_TYPE_SYNTHESISER.setRegistryName(CosmosIndustry.MOD_ID, "tile_entity_synthesiser");

		TILE_TYPE_SYNTHESISER_STAND = BlockEntityType.Builder.<BlockEntitySynthesiserStand>of(BlockEntitySynthesiserStand::new, BLOCK_SYNTHESISER_STAND).build(null);
		TILE_TYPE_SYNTHESISER_STAND.setRegistryName(CosmosIndustry.MOD_ID, "tile_entity_synthesiser_stand");

		BLOCK_ENTITY_TYPE_CHANNEL_ENERGY = BlockEntityType.Builder.<BlockEntityChannelEnergy>of(BlockEntityChannelEnergy::new, BLOCK_ENERGY_CHANNEL).build(null);
		BLOCK_ENTITY_TYPE_CHANNEL_ENERGY.setRegistryName(CosmosIndustry.MOD_ID, "tile_entity_energy_channel");

		BLOCK_ENTITY_TYPE_CHANNEL_SURGE_ENERGY = BlockEntityType.Builder.<BlockEntityChannelSurgeEnergy>of(BlockEntityChannelSurgeEnergy::new, BLOCK_ENERGY_CHANNEL_SURGE).build(null);
		BLOCK_ENTITY_TYPE_CHANNEL_SURGE_ENERGY.setRegistryName(CosmosIndustry.MOD_ID, "tile_entity_energy_channel_surge");

		BLOCK_ENTITY_TYPE_CHANNEL_TRANSPARENT_ENERGY = BlockEntityType.Builder.<BlockEntityChannelTransparentEnergy>of(BlockEntityChannelTransparentEnergy::new, BLOCK_ENERGY_CHANNEL_TRANSPARENT).build(null);
		BLOCK_ENTITY_TYPE_CHANNEL_TRANSPARENT_ENERGY.setRegistryName(CosmosIndustry.MOD_ID, "tile_entity_energy_channel_transparent");

		BLOCK_ENTITY_TYPE_CHANNEL_TRANSPARENT_SURGE_ENERGY = BlockEntityType.Builder.<BlockEntityChannelTransparentSurgeEnergy>of(BlockEntityChannelTransparentSurgeEnergy::new, BLOCK_ENERGY_CHANNEL_TRANSPARENT_SURGE).build(null);
		BLOCK_ENTITY_TYPE_CHANNEL_TRANSPARENT_SURGE_ENERGY.setRegistryName(CosmosIndustry.MOD_ID, "tile_entity_energy_channel_transparent_surge");

		BLOCK_ENTITY_TYPE_CAPACITOR = BlockEntityType.Builder.<BlockEntityCapacitor>of(BlockEntityCapacitor::new, BLOCK_CAPACITOR).build(null);
		BLOCK_ENTITY_TYPE_CAPACITOR.setRegistryName(CosmosIndustry.MOD_ID, "block_entity_capacitor");
		
		event.getRegistry().registerAll(
			TILE_TYPE_KILN, TILE_TYPE_GRINDER, TILE_TYPE_COMPACTOR, TILE_TYPE_CHARGER, TILE_TYPE_SEPARATOR, TILE_TYPE_LASER_CUTTER,
			TILE_TYPE_SYNTHESISER, TILE_TYPE_SYNTHESISER_STAND,
			BLOCK_ENTITY_TYPE_CHANNEL_ENERGY, BLOCK_ENTITY_TYPE_CHANNEL_SURGE_ENERGY, BLOCK_ENTITY_TYPE_CHANNEL_TRANSPARENT_ENERGY, BLOCK_ENTITY_TYPE_CHANNEL_TRANSPARENT_SURGE_ENERGY,
			
			BLOCK_ENTITY_TYPE_CAPACITOR
		);

		CosmosIndustry.CONSOLE.startup("BlockEntityTypes Registered.");
	}

	@SubscribeEvent
	public static void onBlockEntityRendererRegistry(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_ENERGY, RendererEnergyChannel::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_SURGE_ENERGY, RendererEnergyChannelSurge::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_TRANSPARENT_ENERGY, RendererEnergyChannelTransparent::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CHANNEL_TRANSPARENT_SURGE_ENERGY, RendererEnergyChannelTransparentSurge::new);

		event.registerBlockEntityRenderer(TILE_TYPE_KILN, RendererKiln::new);
		event.registerBlockEntityRenderer(TILE_TYPE_GRINDER, RendererGrinder::new);
		
		event.registerBlockEntityRenderer(TILE_TYPE_SYNTHESISER, RendererSynthesiser::new);
		event.registerBlockEntityRenderer(TILE_TYPE_SYNTHESISER_STAND, RendererSynthesiserStand::new);

		CosmosIndustry.CONSOLE.startup("BlockEntityRenderer Registration complete.");
	}
	
	@SubscribeEvent
	public static void onEntityTypeRegistry(final RegistryEvent.Register<EntityType<?>> event) { }
	
	@OnlyIn(Dist.CLIENT)
	public static void registerClient(ModLoadingContext context) {
		context.registerExtensionPoint(ConfigGuiFactory.class, () -> ScreenConfiguration.getInstance());
		
		CosmosIndustry.CONSOLE.startup("ClientRegistry complete.");
	}
	
	@SuppressWarnings("unused")
	@OnlyIn(Dist.CLIENT)
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		RenderType CUTOUT_MIPPED = RenderType.cutoutMipped();
		RenderType TRANSLUCENT = RenderType.translucent();
		EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
		
		MenuScreens.register(CONTAINER_TYPE_KILN, ScreenKiln::new);
		MenuScreens.register(CONTAINER_TYPE_GRINDER, ScreenGrinder::new);
		MenuScreens.register(CONTAINER_TYPE_COMPACTOR, ScreenCompactor::new);
		MenuScreens.register(CONTAINER_TYPE_CHARGER, ScreenCharger::new);
		MenuScreens.register(CONTAINER_TYPE_SEPARATOR, ScreenSeparator::new);
		MenuScreens.register(CONTAINER_TYPE_LASER_CUTTER, ScreenLaserCutter::new);
		MenuScreens.register(CONTAINER_TYPE_SYNTHESISER, ScreenSynthesiser::new);

		MenuScreens.register(CONTAINER_TYPE_CAPACITOR, ScreenCapacitor::new);
		
		CosmosRuntimeHelper.setRenderLayers(CUTOUT_MIPPED,
			BLOCK_STRUCTURE, BLOCK_KILN, BLOCK_GRINDER, BLOCK_COMPACTOR, BLOCK_CHARGER, BLOCK_SEPARATOR, BLOCK_LASER_CUTTER,
			BLOCK_SYNTHESISER, BLOCK_SYNTHESISER_STAND,
			BLOCK_ENERGY_CHANNEL, BLOCK_ENERGY_CHANNEL_SURGE, BLOCK_ENERGY_CHANNEL_TRANSPARENT, BLOCK_ENERGY_CHANNEL_TRANSPARENT_SURGE,
			
			BLOCK_CAPACITOR
		);
		
		CosmosRuntimeHelper.setRenderLayers(TRANSLUCENT,
			BLOCK_LASER_CUTTER, BLOCK_GLASS_BLACK
		);
	}
}