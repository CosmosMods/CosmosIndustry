package com.tcn.cosmosindustry.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmosindustry.core.management.CoreConfigurationManager;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionBoolean;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionBoolean.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionsList;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;

@OnlyIn(Dist.CLIENT)
public final class ScreenConfiguration extends Screen {

	private final Screen parent;

	private final int TITLE_HEIGHT = 8;

	private final int OPTIONS_LIST_TOP_HEIGHT = 24;
	private final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
	private final int OPTIONS_LIST_ITEM_HEIGHT = 25;

	private final int BUTTON_WIDTH = 200;
	private final int BUTTON_HEIGHT = 20;
	private final int DONE_BUTTON_TOP_OFFSET = 26;

	private CosmosOptionsList optionsRowList;
	
	private static ConfigGuiHandler.ConfigGuiFactory INSTANCE = new ConfigGuiHandler.ConfigGuiFactory((mc, screen) -> new ScreenConfiguration(screen));

	public static ConfigGuiFactory getInstance() {
		return INSTANCE;
	}
	
	public ScreenConfiguration(Screen parent) {
		super(ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, true, "cosmosindustry.gui.config.name"));
		
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.optionsRowList = new CosmosOptionsList(
			this.minecraft, this.width, this.height,
			OPTIONS_LIST_TOP_HEIGHT,
			this.height - OPTIONS_LIST_BOTTOM_OFFSET,
			OPTIONS_LIST_ITEM_HEIGHT
		);
		
		this.optionsRowList.addBig(new CosmosOptionBoolean(
			ComponentColour.CYAN, false, "cosmosindustry.gui.config.messages", TYPE.ON_OFF,
			unused -> CoreConfigurationManager.getInstance().getDebugMessage(),
			(unused, newValue) -> CoreConfigurationManager.getInstance().setDebugMessage(newValue)
		));
		
		this.addWidget(this.optionsRowList);
		
		this.addRenderableWidget(new Button(
			(this.width - BUTTON_WIDTH) /2, this.height - DONE_BUTTON_TOP_OFFSET, BUTTON_WIDTH, BUTTON_HEIGHT,
			ComponentHelper.locComp(ComponentColour.GREEN, true, "cosmosindustry.gui.done"), (button) -> { this.onClose(); }
		));
	}
	
	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float ticks) {
		this.renderBackground(stack);
		
		this.optionsRowList.render(stack, mouseX, mouseY, ticks);
		drawCenteredString(stack, this.font, this.title, width / 2, TITLE_HEIGHT, 0xFFFFFF);
		
		super.render(stack, mouseX, mouseY, ticks);
	}
	
    @Override
    public void onClose() {
    	this.minecraft.setScreen(parent);
        CoreConfigurationManager.save();
    }
}