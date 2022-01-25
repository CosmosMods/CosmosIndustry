package com.tcn.cosmosindustry.core.management;

import java.util.ArrayList;

import com.tcn.cosmosindustry.CosmosIndustry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber(modid = CosmosIndustry.MOD_ID)
public class SoundManager {
	
	@EventBusSubscriber(modid = CosmosIndustry.MOD_ID)
	public static class MACHINE {

		public static final SoundEvent GRINDER = new SoundEvent(new ResourceLocation(CosmosIndustry.MOD_ID + ":" + "grinder")).setRegistryName("grinder");
		public static final SoundEvent CRUSHER = new SoundEvent(new ResourceLocation(CosmosIndustry.MOD_ID + ":" + "crusher")).setRegistryName("crusher");
		
		public static final SoundEvent LASERHUM = new SoundEvent(new ResourceLocation(CosmosIndustry.MOD_ID + ":" + "laserhum")).setRegistryName("laserhum");

		public static final SoundEvent COMPRESSOR = new SoundEvent(new ResourceLocation(CosmosIndustry.MOD_ID + ":" + "compressor")).setRegistryName("compressor");
		public static final SoundEvent EXTRACTOR = new SoundEvent(new ResourceLocation(CosmosIndustry.MOD_ID + ":" + "extractor")).setRegistryName("extractor");
		
		public static final ArrayList<SoundEvent> SOUND_LIST = new ArrayList<SoundEvent> () {
			private static final long serialVersionUID = 1L;

			{
				add(GRINDER);
				add(CRUSHER);
				add(LASERHUM);
				add(COMPRESSOR);
				add(EXTRACTOR);
			}
		};
		
		@SubscribeEvent
		public static void register(RegistryEvent.Register<SoundEvent> event) {
			event.getRegistry().registerAll(GRINDER, CRUSHER, LASERHUM, COMPRESSOR, EXTRACTOR);
		}
	}
	
	@EventBusSubscriber(modid = CosmosIndustry.MOD_ID)
	public static class ALARM {
		
		public static final SoundEvent DANGER = new SoundEvent(new ResourceLocation(CosmosIndustry.MOD_ID + ":" + "danger")).setRegistryName("danger");
		public static final SoundEvent DIGITAL_ALIEN = new SoundEvent(new ResourceLocation(CosmosIndustry.MOD_ID + ":" + "digital_alien")).setRegistryName("digital_alien");
		public static final SoundEvent DIGITAL_ALIEN_SLOW = new SoundEvent(new ResourceLocation(CosmosIndustry.MOD_ID + ":" + "digital_alien_slow")).setRegistryName("digital_alien_slow");
		public static final SoundEvent DIGITAL_HARSH = new SoundEvent(new ResourceLocation(CosmosIndustry.MOD_ID + ":" + "digital_harsh")).setRegistryName("digital_harsh");
		public static final SoundEvent DIGITAL_WAILING = new SoundEvent(new ResourceLocation(CosmosIndustry.MOD_ID + ":" + "digital_wailing")).setRegistryName("digital_wailing");
		
		public static final SoundEvent AGENCY = new SoundEvent(new ResourceLocation(CosmosIndustry.MOD_ID + ":" + "agency")).setRegistryName("agency");
		
		public static final ArrayList<SoundEvent> ALARM_SOUND_LIST = new ArrayList<SoundEvent> () {
			private static final long serialVersionUID = 1L;

			{
				add(DANGER);
				add(DIGITAL_ALIEN);
				add(DIGITAL_ALIEN_SLOW);
				add(DIGITAL_HARSH);
				add(DIGITAL_WAILING);
				add(AGENCY);
			}
		};
		
		@SubscribeEvent
		public static void register(RegistryEvent.Register<SoundEvent> event) {
			event.getRegistry().registerAll(DANGER, DIGITAL_ALIEN, DIGITAL_ALIEN_SLOW, DIGITAL_HARSH, DIGITAL_WAILING, AGENCY);
		}
	}
}