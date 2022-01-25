package com.tcn.cosmosindustry.processing.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmosindustry.IndustryReference.RESOURCE.PROCESSING;
import com.tcn.cosmosindustry.processing.client.container.ContainerSynthesiser;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiser;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem.IS_HOVERING;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenBlockEntity;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenSynthesiser extends CosmosScreenBlockEntity<ContainerSynthesiser> {
	
	public ScreenSynthesiser(ContainerSynthesiser containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(176, 177);
		this.setTexture(PROCESSING.SYNTHESISER_LOC_GUI);
		this.setTitleLabelDims(50, 4);
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
		
		if (entity instanceof BlockEntitySynthesiser) {
			BlockEntitySynthesiser blockEntity = (BlockEntitySynthesiser) entity;

			CosmosUISystem.renderStaticElementToggled(this, poseStack, this.getScreenCoords(), 52,  27, 176, 36, 18, 18, blockEntity.isSetupFourWay());
			CosmosUISystem.renderStaticElementToggled(this, poseStack, this.getScreenCoords(), 52,  47, 176, 0,  18, 18, blockEntity.isSetupFourWay());
			CosmosUISystem.renderStaticElementToggled(this, poseStack, this.getScreenCoords(), 106, 27, 176, 18, 18, 18, blockEntity.isSetupEightWay());
			CosmosUISystem.renderStaticElementToggled(this, poseStack, this.getScreenCoords(), 106, 47, 176, 0,  18, 18, blockEntity.isSetupEightWay());
			
			CosmosUISystem.renderEnergyDisplay(this, poseStack, ComponentColour.RED, blockEntity, getScreenCoords(), 80, 16, 16, 38, passEvents);
		}
	}
	
	@Override
	public void renderComponentHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntitySynthesiser) {
			BlockEntitySynthesiser blockEntity = (BlockEntitySynthesiser) entity;
			
			if (IS_HOVERING.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 80, this.getScreenCoords()[0] + 95, this.getScreenCoords()[1] + 16, this.getScreenCoords()[1] + 53)) {
				DecimalFormat formatter = new DecimalFormat("#,###,###,###");
				String amount_string = formatter.format(blockEntity.getEnergyStored());
				String capacity_string = formatter.format(blockEntity.getMaxEnergyStored());
				
				Component[] comp = new Component[] { ComponentHelper.locComp(ComponentColour.WHITE, false, "cosmoslibrary.gui.energy_bar.pre"),
						ComponentHelper.locComp(ComponentColour.RED, false, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff") };
				
				this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
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