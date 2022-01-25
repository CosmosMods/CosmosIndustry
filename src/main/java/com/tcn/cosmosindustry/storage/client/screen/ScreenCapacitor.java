package com.tcn.cosmosindustry.storage.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmosindustry.IndustryReference.RESOURCE.STORAGE;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityCharger;
import com.tcn.cosmosindustry.storage.client.container.ContainerCapacitor;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityCapacitor;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem.IS_HOVERING;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenBlockEntity;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
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
public class ScreenCapacitor extends CosmosScreenBlockEntity<ContainerCapacitor> {

	private CosmosButtonWithType increaseButton; private int[] IBI = new int[] { 33, 18 };
	private CosmosButtonWithType decreaseButton; private int[] DBI = new int[] { 33, 48 };

	public ScreenCapacitor(ContainerCapacitor containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(176, 177);
		this.setTexture(STORAGE.GUI_CAPACITOR);
		this.setTitleLabelDims(60, 4);
		this.setInventoryLabelDims(8, 84);
	}
	
	@Override
	public void init() {
		super.init();
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float ticks, int mouseX, int mouseY) {
		super.renderBg(matrixStack, ticks, mouseX, mouseY);
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityCapacitor) {
			BlockEntityCapacitor tileEntity = (BlockEntityCapacitor) entity;
			
			CosmosUISystem.renderEnergyDisplay(this, matrixStack, ComponentColour.RED, tileEntity, this.getScreenCoords(), 67, 17, 16, 60, false);
		}
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
	}
	
	@Override
	protected void pushButton(Button button) {	}
	
	@Override
	public void renderComponentHoverEffect(PoseStack matrixStack, Style style, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityCapacitor) {
			BlockEntityCapacitor tileEntity = (BlockEntityCapacitor) entity;
			
			if (IS_HOVERING.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 67, this.getScreenCoords()[0] + 82, this.getScreenCoords()[1] + 17, this.getScreenCoords()[1] + 76)) {
				DecimalFormat formatter = new DecimalFormat("#,###,###,###");
				String amount_string = formatter.format(tileEntity.getEnergyStored());
				String capacity_string = formatter.format(tileEntity.getMaxEnergyStored());
				
				Component[] comp = new Component[] { ComponentHelper.locComp(ComponentColour.WHITE, false, "cosmoslibrary.gui.energy_bar.pre"),
						ComponentHelper.locComp(ComponentColour.RED, false, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff") };
				
				this.renderComponentTooltip(matrixStack, Arrays.asList(comp), mouseX, mouseY);
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