package com.tcn.cosmosindustry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IndustryReference {
	public static class RESOURCE {
		
		/**
		 * Prefix for all ResourceLocations.
		 */
		public static final String PRE = CosmosIndustry.MOD_ID + ":";
		public static final String RESOURCE = PRE + "textures/";
		
		/**
		 * ResourceLocations for Base Objects.
		 */
		public static class BASE {
			public static final String BASE = RESOURCE + "base/";
			public static final String BLOCKS = BASE + "blocks/";
			public static final String FLUID = PRE + "base/blocks/fluid/";
			public static final String ITEMS = BASE + "items/";
			public static final String GUI = BASE + "gui/";
			
			public static final ResourceLocation GUI_ALARM = new ResourceLocation(GUI + "utility/gui_alarm.png");
			
			public static final ResourceLocation FLUID_COOLANT_STILL = new ResourceLocation(FLUID + "coolant/coolant_still");
			public static final ResourceLocation FLUID_COOLANT_FLOWING = new ResourceLocation(FLUID + "coolant/coolant_flow");
			
			public static final ResourceLocation FLUID_ENERGIZED_REDSTONE_STILL= new ResourceLocation(FLUID + "energized_redstone/energized_redstone_still");
			public static final ResourceLocation FLUID_ENERGIZED_REDSTONE_FLOWING = new ResourceLocation(FLUID + "energized_redstone/energized_redstone_flow");
			
			public static final ResourceLocation FLUID_GLOWSTONE_INFUSED_MAGMA_STILL = new ResourceLocation(FLUID + "glowstone_infused_magma/glowstone_infused_magma_still");
			public static final ResourceLocation FLUID_GLOWSTONE_INFUSED_MAGMA_FLOWING = new ResourceLocation(FLUID + "glowstone_infused_magma/glowstone_infused_magma_flow");
		}
		
		/**
		 * ResourceLocations for Processing Objects.
		 */
		public static class PROCESSING {
			public static final String PROCESSING = RESOURCE + "processing/";
			public static final String BLOCKS = PROCESSING + "blocks/";
			public static final String ITEMS = PROCESSING + "items/";
			public static final String GUI = PROCESSING + "gui/";
			
			public static final String TESR = PROCESSING + "tesr/";
			
			public static final String PREFIX = "gui.processing.";
			public static final String SUFFIX = ".name";
			
			public static final String BLOCK_PREFIX = "block_";
			
			public static final ResourceLocation KILN_LOC_GUI = new ResourceLocation(GUI + "kiln/gui.png");
			public static final ResourceLocation KILN_LOC_GUI_JEI = new ResourceLocation(GUI + "kiln/kiln_jei.png");
			public static final ResourceLocation KILN_LOC_TESR = new ResourceLocation(TESR + "kiln/internals.png");
			public static final String KILN_NAME = PREFIX + "kiln" + SUFFIX;
			public static final int KILN_INDEX = 1;
			
			public static final ResourceLocation GRINDER_LOC_GUI = new ResourceLocation(GUI + "grinder/gui.png");
			public static final ResourceLocation GRINDER_LOC_GUI_JEI = new ResourceLocation(GUI + "grinder/grinder_jei.png");
			public static final ResourceLocation GRINDER_LOC_TESR = new ResourceLocation(TESR + "grinder/internals.png");
			public static final String GRINDER_NAME = PREFIX + "grinder" + SUFFIX;
			public static final int GRINDER_INDEX = 2;
			
			public static final ResourceLocation COMPACTOR_LOC_GUI = new ResourceLocation(GUI + "compactor/gui.png");
			public static final ResourceLocation COMPACTOR_LOC_GUI_JEI = new ResourceLocation(GUI + "compactor/compactor_jei.png");
			public static final ResourceLocation COMPACTOR_LOC_TESR = new ResourceLocation(TESR + "compactor/internals.png");
			public static final String COMPACTOR_NAME = PREFIX + "compactor" + SUFFIX;
			public static final int COMPACTOR_INDEX = 3;
			
			public static final ResourceLocation SEPARATOR_LOC_GUI = new ResourceLocation(GUI + "separator/gui.png");
			public static final ResourceLocation SEPARATOR_LOC_GUI_JEI = new ResourceLocation(GUI + "separator/separator_jei.png");
			public static final ResourceLocation SEPARATOR_LOC_TESR = new ResourceLocation(TESR + "separator/internals.png");
			public static final String SEPARATOR_NAME = PREFIX + "seperator" + SUFFIX;
			public static final int SEPARATOR_INDEX = 4;

			public static final ResourceLocation LASER_CUTTER_LOC_GUI = new ResourceLocation(GUI + "laser_cutter/gui.png");
			public static final ResourceLocation LASER_CUTTER_LOC_GUI_JEI = new ResourceLocation(GUI + "laser_cutter/laser_cutter_jei.png");
			public static final ResourceLocation LASER_CUTTER_LOC_TESR = new ResourceLocation(TESR + "laser_cutter/internals.png");
			public static final String LASER_CUTTER_NAME = PREFIX + "laser_cutter" + SUFFIX;
			public static final int LASER_CUTTER_INDEX = 1;
			
			public static final ResourceLocation CHARGER_LOC_GUI = new ResourceLocation(GUI + "charger/gui.png");
			public static final ResourceLocation CHARGER_LOC_TESR = new ResourceLocation(TESR + "charger/model.png");
			public static final String CHARGER_NAME = PREFIX + "charger" + SUFFIX;
			public static final int CHARGER_INDEX = 5;
			
			public static final ResourceLocation ORE_PLANT_LOC_GUI = new ResourceLocation(GUI + "ore_plant/gui.png");
			public static final ResourceLocation ORE_PLANT_LOC_TESR = new ResourceLocation(TESR + "ore_plant/model.png");
			public static final String ORE_PLANT_NAME = PREFIX + "ore_plant" + SUFFIX;
			public static final int ORE_PLANT_INDEX = 6;
			
			public static final ResourceLocation FLUID_CRAFTER_LOC_GUI = new ResourceLocation(GUI + "fluid_crafter/gui.png");
			public static final ResourceLocation FLUID_CRAFTER_LOC_TESR = new ResourceLocation(TESR + "fluid_crafter/model.png");
			public static final String FLUID_CRAFTER_NAME = PREFIX + "fluid_crafter" + SUFFIX;
			public static final int FLUID_CRAFTER_INDEX = 7;
			
			public static final ResourceLocation SYNTHESISER_LOC_GUI = new ResourceLocation(GUI + "synthesiser/synthesiser_gui.png");
			public static final ResourceLocation SYNTHESISER_LOC_GUI_JEI = new ResourceLocation(GUI + "synthesiser/synthesiser_jei.png");
			public static final ResourceLocation SYNTHESISER_LOC_GUI_JEI_LASER_2 = new ResourceLocation(GUI + "synthesiser/synthesiser_jei_laser_2.png");
			public static final ResourceLocation SYNTHESISER_LOC_GUI_JEI_LASER_4 = new ResourceLocation(GUI + "synthesiser/synthesiser_jei_laser_4.png");
			public static final ResourceLocation SYNTHESISER_LOC_GUI_JEI_LASER_8 = new ResourceLocation(GUI + "synthesiser/synthesiser_jei_laser_8.png");
			public static final ResourceLocation SYNTHESISER_LOC_TESR = new ResourceLocation(BLOCKS + "synthesiser/synthesiser.png");
			public static final String SYNTHESISER_NAME = PREFIX + "synthesiser" + SUFFIX;
			public static final int SYNTHESISER_INDEX = 8;
			
			public static final String SYNTHESISER_STAND = PREFIX + "synthesiser_stand" + SUFFIX;
			public static final int SYNTHESISER_STAND_INDEX = 9;

			public static final int[] LASER_JEI_ARRAY_X = new int[] { 0, 0, 0, 0, 60, 60, 60, 60, 120, 120, 120, 120,180, 180, 180, 180 };
			public static final int[] LASER_JEI_ARRAY_Y = new int[] { 0, 60, 120, 180, 0, 60, 120, 180, 0, 60, 120, 180, 0, 60, 120, 180 };
			
			/** Values */
			public static final int[] CAPACITY = new int[] { 40000, 50000, 60000, 70000, 80000 };
			public static final int[] MAX_INPUT = new int [] { 200, 300, 400, 500, 600 };
			
			public static final int[] RF_TICK_RATE = new int[] { 40, 60, 80, 100, 120 };
			public static final int[] RF_EFF_RATE = new int[] { 0, 10, 15, 20, 30, 50 };
			public static final int[] SPEED_RATE = new int[] { 80, 70, 60, 50, 40 };
			
			public static final int[] CAPACITY_U = new int[] { 80000, 90000, 100000, 120000, 120000 };
			public static final int[] MAX_INPUT_U = new int[] { 600, 700, 800, 900, 1000 };
			
			public static final int[] RF_TICK_RATE_U = new int[] { 100, 120, 140, 160, 180 };
			public static final int[] SPEED_RATE_U = new int[] { 35, 30, 25, 20, 15 };
		}
		
		/**
		 * ResourceLocations for Production Objects.
		 */
		public static class PRODUCTION {
			public static final String PRODUCTION = RESOURCE + "production/";
			public static final String BLOCKS = PRODUCTION + "blocks/";
			public static final String ITEMS = PRODUCTION + "items/";
			public static final String GUI = PRODUCTION + "gui/";

			public static final String TESR = PRODUCTION + "tesr/";
			
			/** Gui */
			public static final ResourceLocation GUI_SCALED_ELEMENTS = new ResourceLocation(GUI + "scaled_elements.png");
			
			public static final ResourceLocation GUI_SOLIDFUEL = new ResourceLocation(GUI + "gui_solid_fuel.png");
			public static final ResourceLocation GUI_HEATEDFLUID = new ResourceLocation(GUI + "gui_heatedfluid.png");
			public static final ResourceLocation GUI_PELTIER = new ResourceLocation(GUI + "gui_peltier.png");
			public static final ResourceLocation GUI_SOLARPANEL = new ResourceLocation(GUI + "gui_solarpanel.png");
			
			/** Localised Names */
			public static final String SOLIDFUEL = "gui.production.solid_fuel.name";
			public static final String SOLARPANEL = "gui.production.solar.name";
			public static final String HEATEDFLUID = "gui.production.heated_fluid.name";
			public static final String PELTIER = "gui.production.peltier.name";
			
			/** Values */
			public static final int CAPACITY = 60000;
			public static final int MAX_OUTPUT = 500;
			
			public static final int[] RF_TICK_RATE = new int[] { 200, 300, 400, 500 };
			public static final int[] SPEED_RATE = new int[] { 100, 150, 200, 250 };
			
			public static final int CAPACITY_U = 120000;
			public static final int MAX_OUTPUT_U = 1000;
			
			public static final int[] RF_TICK_RATE_U = new int[] { 250, 500, 750, 1000 };
			public static final int[] SPEED_RATE_U = new int[] { 100, 150, 200, 250 };
		}
		
		/**
		 * ResourceLocations for Storage Objects.
		 */
		public static class STORAGE {
			public static final String STORAGE = RESOURCE + "storage/";
			public static final String BLOCKS = STORAGE + "blocks/";
			public static final String ITEMS = STORAGE + "items/";
			public static final String GUI = STORAGE + "gui/";

			/** Gui */
			public static final ResourceLocation GUI_CAPACITOR = new ResourceLocation(GUI + "capacitor/gui.png");
			
			/** Localised Names */
			public static final String P_CAPACITOR = "gui.storage.powered.capacitor.name";
			public static final String E_CAPACITOR = "gui.storage.energized.capacitor.name";
			
			public static final String MECHANISEDSTORAGESMALL = "gui.storage.mechanisedstoragesmall.name";
			public static final String MECHANISEDSTORAGELARGE = "gui.storage.mechanisedstoragelarge.name";
			
			/** Values */
			public static final int CAPACITY = 5000000;
			public static final int MAX_INPUT = 50000;
			public static final int MAX_OUTPUT = 50000;
			
			public static final int CAPACITY_U = 10000000;
			public static final int MAX_INPUT_U = 100000;
			public static final int MAX_OUTPUT_U = 100000;
		}
		
		/**
		 * ResourceLocation for Transport Objects.
		 */
		public static class TRANSPORT {
			
			/** 
			 * Bounding Boxes for "standard" pipes.
			 * 
			 * Order is: [Base - D-U-N-S-W-E]
			 */
			public static VoxelShape[] BOUNDING_BOXES_STANDARD = new VoxelShape[] {
				Block.box(5.00D, 5.00D, 5.00D, 11.0D, 11.0D, 11.0D), // BASE
				Block.box(5.00D, 0.00D, 5.00D, 11.0D, 5.00D, 11.0D), // DOWN
				Block.box(5.00D, 11.0D, 5.00D, 11.0D, 16.0D, 11.0D), // UP
				Block.box(5.00D, 5.00D, 0.00D, 11.0D, 11.0D, 5.00D), // NORTH
				Block.box(5.00D, 5.00D, 11.0D, 11.0D, 11.0D, 16.0D), // SOUTH
				Block.box(0.00D, 5.00D, 5.00D, 5.00D, 11.0D, 11.0D), // WEST
				Block.box(11.0D, 5.00D, 5.00D, 16.0D, 11.0D, 11.0D), // EAST
			};

			/** 
			 * Bounding Boxes for "interface" elements of pipes.
			 * 
			 * Order is: [Base - D-U-N-S-W-E]
			 */
			public static VoxelShape[] BOUNDING_BOXES_INTERFACE = new VoxelShape[] {
				Block.box(4.00F, 0.00F, 4.00F, 12.0F, 3.00F, 12.0F), // DOWN
				Block.box(4.00F, 13.0F, 4.00F, 12.0F, 16.0F, 12.0F), // UP
				
				Block.box(4.00F, 4.00F, 0.00F, 12.0F, 12.0F, 3.00F), // NORTH
				Block.box(4.00F, 4.00F, 13.0F, 12.0F, 12.0F, 16.0F), // SOUTH
				
				Block.box(0.00F, 4.00F, 4.00F, 3.00F, 12.0F, 12.0F), // WEST
				Block.box(13.0F, 4.00F, 4.00F, 16.0F, 12.0F, 12.0F) // EAST
			};
			
			/** 
			 * Bounding Boxes for "surge" pipes.
			 * 
			 * Order is: [Base - D-U-N-S-W-E]
			 */
			public static VoxelShape[] BOUNDING_BOXES_STANDARD_SURGE = new VoxelShape[] {
				Block.box(4.50D, 4.50D, 4.50D, 11.5D, 11.5D, 11.5D), //BASE
				Block.box(4.50D, 0.00D, 4.50D, 11.5D, 4.50D, 11.5D), // DOWN
				Block.box(4.50D, 11.5D, 4.50D, 11.5D, 16.0D, 11.5D), // UP
				Block.box(4.50D, 4.50D, 0.00D, 11.5D, 11.5D, 4.50D), // NORTH
				Block.box(4.50D, 4.50D, 11.5D, 11.5D, 11.5D, 16.0D), //SOUTH
				Block.box(0.00D, 4.50D, 4.50D, 4.50D, 11.5D, 11.5D), // WEST
				Block.box(11.5D, 4.50D, 4.50D, 16.0D, 11.5D, 11.5D), // EAST
			};
				
			//[WIP] Bounding Boxes for "thin" pipes.
			public VoxelShape[] BOUNDING_BOXES_THIN = new VoxelShape[] {};
			
			public static final String TRANSPORT = RESOURCE + "transport/";
			public static final String BLOCKS = TRANSPORT + "blocks/";
			public static final String ITEMS = TRANSPORT + "items/";
			public static final String GUI = TRANSPORT + "gui/";
			public static final String TESR = TRANSPORT + "tesr/";
			
			/** Values */
			public static final int ENERGY_CAPACITY = 100000;
			public static final int ENERGY_MAX_TRANSFER = 50000;
			
			public static final int ENERGY_CAPACITY_SURGE = 200000;
			public static final int ENERGY_MAX_TRANSFER_SURGE = 100000;
			
			public static final ResourceLocation ENERGY_CHANNEL_LOC_TESR = new ResourceLocation(BLOCKS + "channel/energy/channel.png");
			public static final ResourceLocation ENERGY_CHANNEL_TRANSPARENT_LOC_TESR = new ResourceLocation(BLOCKS + "channel/energy/channel_transparent.png");
			
			public static final ResourceLocation ENERGY_CHANNEL_SURGE_LOC_TESR = new ResourceLocation(BLOCKS + "channel/energy/channel_surge.png");
			public static final ResourceLocation ENERGY_CHANNEL_TRANSPARENT_SURGE_LOC_TESR = new ResourceLocation(BLOCKS + "channel/energy/channel_transparent_surge.png");
		}
	}
	
	/**
	 * JEI Integration.
	 */
	public static class JEI {
		public static final String KILN_UNLOC = "jei.recipe.kiln";
		public static final String GRINDER_UNLOC = "jei.recipe.grinder";
		public static final String COMPACTOR_UNLOC = "jei.recipe.compactor";
		public static final String SEPARATOR_UNLOC = "jei.recipe.separator";
		public static final String SYNTHESISER_UNLOC = "jei.recipe.synthesiser";
		
		public static final ResourceLocation GRINDER_UID = new ResourceLocation(CosmosIndustry.MOD_ID, "grinder_category");
		public static final ResourceLocation SEPARATOR_UID = new ResourceLocation(CosmosIndustry.MOD_ID, "separator_category");
	}
}