package com.tcn.cosmosindustry.processing.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.IndustryReference.RESOURCE.PROCESSING;
import com.tcn.cosmosindustry.processing.client.container.ContainerLaserCutter;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityLaserCutter;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem.IS_HOVERING;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenBlockEntity;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ScreenLaserCutter extends CosmosScreenBlockEntity<ContainerLaserCutter> {
	
	public ScreenLaserCutter(ContainerLaserCutter containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(176, 177);
		this.setTexture(PROCESSING.LASER_CUTTER_LOC_GUI);
		this.setTitleLabelDims(51, 5);
		this.setInventoryLabelDims(8, 85);
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		super.render(poseStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float ticks, int mouseX, int mouseY) {
		super.renderBg(poseStack, ticks, mouseX, mouseY);
	
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityLaserCutter) {
			BlockEntityLaserCutter blockEntity = (BlockEntityLaserCutter) entity;

			if (blockEntity.canProcess()) {
				int k = blockEntity.getProcessProgressScaled(16);
				
				CosmosUISystem.renderScaledElementDownNestled(this, poseStack, this.getScreenCoords(), 104, 38, 176, 0, 16, blockEntity.getProcessProgressScaled(16));
			}

			CosmosUISystem.renderEnergyDisplay(this, poseStack, ComponentColour.RED, blockEntity, this.getScreenCoords(), 80, 16, 16, 60, false);
		}
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
	}
	
	@Override
	public void pushButton(Button button) {
		super.pushButton(button);
	}
	
	@Override
	public void renderComponentHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityLaserCutter) {
			BlockEntityLaserCutter blockEntity = (BlockEntityLaserCutter) entity;
			
			if (IS_HOVERING.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 80,  this.getScreenCoords()[0] + 95,  this.getScreenCoords()[1] + 16,  this.getScreenCoords()[1] + 75)) {
				DecimalFormat formatter = new DecimalFormat("#,###,###,###");
				String amount_string = formatter.format(blockEntity.getEnergyStored());
				String capacity_string = formatter.format(blockEntity.getMaxEnergyStored());
				
				Component[] comp = new Component[] { ComponentHelper.locComp(ComponentColour.WHITE, false, "cosmoslibrary.gui.energy_bar.pre"),
						ComponentHelper.locComp(ComponentColour.RED, false, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff") };

				Component[] compProcessing = new Component[] { ComponentHelper.locComp(ComponentColour.WHITE, false, "cosmoslibrary.gui.energy_bar.pre"),
						ComponentHelper.locComp(ComponentColour.RED, false, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff"),
						ComponentHelper.locComp(ComponentColour.PURPLE, false, "cosmoslibrary.gui.energy.fe_pre",  "" + blockEntity.getRFTickRate(), "cosmoslibrary.gui.energy.fe_rate")};
				
				if (blockEntity.isProcessing()) {
					this.renderComponentTooltip(poseStack, Arrays.asList(compProcessing), mouseX, mouseY);
				} else {
					this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
				}
			}
		}
	}
	
	@Override
	protected boolean isHovering(int positionX, int positionY, int width, int height, double mouseX, double mouseY) {
		return super.isHovering(positionX, positionY, width, height, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void slotClicked(Slot slotIn, int mouseX, int mouseY, ClickType clickTypeIn) {
		super.slotClicked(slotIn, mouseX, mouseY, clickTypeIn);
	}
}