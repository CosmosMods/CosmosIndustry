package com.tcn.cosmosindustry.transport.client.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.transport.client.ter.model.ModelEnergyChannelSurge;
import com.tcn.cosmosindustry.transport.core.blockentity.BlockEntityChannelSurgeEnergy;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererEnergyChannelSurge implements BlockEntityRenderer<BlockEntityChannelSurgeEnergy> {

	private static final ResourceLocation TEXTURE = IndustryReference.RESOURCE.TRANSPORT.ENERGY_CHANNEL_SURGE_LOC_TESR;
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);
	
	@OnlyIn(Dist.CLIENT)
	private ModelEnergyChannelSurge model;

	@OnlyIn(Dist.CLIENT)
	public RendererEnergyChannelSurge(BlockEntityRendererProvider.Context contextIn) {
		this.model = new ModelEnergyChannelSurge();
	}
	
	@Override
	public void render(BlockEntityChannelSurgeEnergy blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);

		if (blockEntity != null) {
			poseStack.pushPose();
			//poseStack.translate((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F);
			poseStack.translate(0.5D, 0.5D, 0.5D);
			
			this.model.renderBasedOnTile(blockEntity, poseStack, builder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
	
			poseStack.popPose();
		}
	}
}