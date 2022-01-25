package com.tcn.cosmosindustry.processing.client.renderer.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiserStand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererSynthesiserStand implements BlockEntityRenderer<BlockEntitySynthesiserStand> {

	@OnlyIn(Dist.CLIENT)
	public RendererSynthesiserStand(BlockEntityRendererProvider.Context contextIn) { }
	
	@Override
	public void render(BlockEntitySynthesiserStand tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		Level world = tileEntity.getLevel();
		
		int slot = 0;

		if (!tileEntity.getItem(slot).isEmpty()) {
			matrixStack.pushPose();
			matrixStack.translate(0.5, 1.1, 0.5);
			
			Quaternion rotation = Vector3f.YP.rotationDegrees(world.getGameTime() * 2);
			
			matrixStack.mulPose(rotation);
			if ((tileEntity.getItem(slot).getItem() instanceof BlockItem)) {
				matrixStack.scale(0.6F, 0.6F, 0.6F);
			} else {
				matrixStack.scale(0.5F, 0.5F, 0.5F);
			}
			
			Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.getItem(slot), TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStack, buffer, 0);
			
			matrixStack.popPose();
		}
	}
}