package com.tcn.cosmosindustry.core.management;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class CoreConfigurationManager {
	
	public static final ForgeConfigSpec spec;
	
	static final CoreConfigurationManager INSTANCE;
	
	static {
		{
			final Pair<CoreConfigurationManager, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CoreConfigurationManager::new);
			INSTANCE = specPair.getLeft();
			spec = specPair.getRight();
		}
	}
	
	public static void save() {
		spec.save();
	}
	
	private final BooleanValue debug_message;
	
	CoreConfigurationManager(final ForgeConfigSpec.Builder builder) {
		builder.push("debug");
		{
			debug_message = builder
						.comment("Whether cosmosindustry can send system messages.")
						.define("debug_message", true);
		}
		builder.pop();
	}
	
	public static CoreConfigurationManager getInstance() {
		return INSTANCE;
	}

	public boolean getDebugMessage() {
		return debug_message.get();
	}
	
	public void setDebugMessage(boolean value) {
		this.debug_message.set(value);
	}
}