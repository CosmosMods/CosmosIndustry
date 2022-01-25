package com.tcn.cosmosindustry.transport.client.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.transport.client.ter.model.ModelEnergyChannel;
import com.tcn.cosmosindustry.transport.core.blockentity.BlockEntityChannelEnergy;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererEnergyChannel implements BlockEntityRenderer<BlockEntityChannelEnergy> {

	private static final ResourceLocation TEXTURE = IndustryReference.RESOURCE.TRANSPORT.ENERGY_CHANNEL_LOC_TESR;
	private static final RenderType RENDER_TYPE = RenderType.entitySolid(TEXTURE);
	
	private ModelEnergyChannel model;

	public RendererEnergyChannel(BlockEntityRendererProvider.Context contextIn) {
		this.model = new ModelEnergyChannel();
	}
	
	@Override
	public void render(BlockEntityChannelEnergy blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
		
		if (blockEntity != null) {
			poseStack.pushPose();
			poseStack.translate(0.5D, 0.5D, 0.5D);
			
			this.model.renderBasedOnTile(blockEntity, poseStack, builder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
			poseStack.popPose();
		}
	}
}