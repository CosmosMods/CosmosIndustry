package com.tcn.cosmosindustry.processing.client.renderer.ber;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiser;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiserStand;
import com.tcn.cosmoslibrary.client.renderer.lib.CosmosRendererHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererSynthesiser implements BlockEntityRenderer<BlockEntitySynthesiser> {

	@OnlyIn(Dist.CLIENT)
	public RendererSynthesiser(BlockEntityRendererProvider.Context contextIn) { }
	
	@Override
	public void render(BlockEntitySynthesiser tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		matrixStack.pushPose();
		BlockPos pos = tileEntity.getBlockPos();
		Level world = tileEntity.getLevel();
		
		if (!tileEntity.getItem(0).isEmpty()) {
			matrixStack.pushPose();
			matrixStack.translate(0.5, 1.3, 0.5);
			
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(world.getGameTime() * 2));
			
			if ((tileEntity.getItem(0).getItem() instanceof BlockItem)) {
				matrixStack.scale(0.7F, 0.7F, 0.7F);
			} else {
				matrixStack.scale(0.4F, 0.4F, 0.4F);
			}
			
			Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.getItem(0), TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStack, buffer, 0);
			
			matrixStack.popPose();
		}
		
		if (tileEntity.canProcessFourWay()) {
			ArrayList<BlockEntity> tiles = tileEntity.getBlockEntitiesFourWay();
			
			for (int i = 0; i < tiles.size(); i++) {
				BlockEntity testTile = tiles.get(i);
				
				if (testTile instanceof BlockEntitySynthesiserStand) {
					BlockEntitySynthesiserStand tileStand = (BlockEntitySynthesiserStand) testTile;
					
					if (!(tileStand.getItem(0).isEmpty())) {
						CosmosRendererHelper.renderLaser(buffer, matrixStack, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, tileStand.getBlockPos().getX() + 0.5, tileStand.getBlockPos().getY() + 0.5, tileStand.getBlockPos().getZ() + 0.5, 80, 0.2F, 0.1F, tileEntity.getColour());
					}
				}
			}
		} else if (tileEntity.canProcessEightWay()) {
			ArrayList<BlockEntity> tiles = tileEntity.getBlockEntitiesEightWay();
			
			for (int i = 0; i < tiles.size(); i++) {
				BlockEntity testTile = tiles.get(i);
				
				if (testTile instanceof BlockEntitySynthesiserStand) {
					BlockEntitySynthesiserStand tileStand = (BlockEntitySynthesiserStand) testTile;
					
					if (!(tileStand.getItem(0).isEmpty())) {
						CosmosRendererHelper.renderLaser(buffer, matrixStack, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, tileStand.getBlockPos().getX() + 0.5, tileStand.getBlockPos().getY() + 0.5, tileStand.getBlockPos().getZ() + 0.5, 80, 0.2F, 0.1F, tileEntity.getColour());
					}
				}
			}
		}
		matrixStack.popPose();
	}
}