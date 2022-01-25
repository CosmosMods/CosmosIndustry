package com.tcn.cosmosindustry.transport.client.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.transport.client.ter.model.ModelEnergyChannel;
import com.tcn.cosmosindustry.transport.core.blockentity.BlockEntityChannelTransparentEnergy;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererEnergyChannelTransparent implements BlockEntityRenderer<BlockEntityChannelTransparentEnergy> {

	private static final ResourceLocation TEXTURE = IndustryReference.RESOURCE.TRANSPORT.ENERGY_CHANNEL_TRANSPARENT_LOC_TESR;
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);
	
	@OnlyIn(Dist.CLIENT)
	private ModelEnergyChannel model;

	@OnlyIn(Dist.CLIENT)
	public RendererEnergyChannelTransparent(BlockEntityRendererProvider.Context contextIn) {
		this.model = new ModelEnergyChannel();
	}
	
	@Override
	public void render(BlockEntityChannelTransparentEnergy tileentity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		BlockEntityChannelTransparentEnergy cable = (BlockEntityChannelTransparentEnergy) tileentity;
		
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);

		poseStack.pushPose();
		//poseStack.translate((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F);
		poseStack.translate(0.5D, 0.5D, 0.5D);
		
		this.model.renderBasedOnTile(cable, poseStack, builder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
		
		poseStack.popPose();
	}
}