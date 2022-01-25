package com.tcn.cosmosindustry.processing.client.renderer.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityKiln;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class RendererKiln implements BlockEntityRenderer<BlockEntityKiln> {

	private static final ResourceLocation TEXTURE = IndustryReference.RESOURCE.PROCESSING.KILN_LOC_TESR;
	private static final RenderType RENDER_TYPE = RenderType.entitySolid(TEXTURE);
	
	private Internals internals;
	private BlockEntityRendererProvider.Context context;

	public RendererKiln(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;
		
		this.internals = new Internals(RENDER_TYPE);
	}
	
	@Override
	public void render(BlockEntityKiln tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
		
		matrixStack.pushPose();
		matrixStack.translate(0.5D, 0.375D, 0.5D);
		
		if (tileEntity.isProcessing()) {
			this.internals.renderCoilsOn(matrixStack, builder, combinedLightIn, combinedOverlayIn);
		} else {
			this.internals.renderCoilsOff(matrixStack, builder, combinedLightIn, combinedOverlayIn);
		}
		
		if (tileEntity.getProcessTime(0) > (tileEntity.getProcessSpeed() - 10)) {
			Recipe<?> iRecipe = tileEntity.getRecipeUsed();
			
			if (iRecipe != null) {
				ItemStack output = iRecipe.getResultItem();
				
				matrixStack.pushPose();
				matrixStack.translate(0, 0.2, 0);
				
				if ((tileEntity.getItem(0).getItem() instanceof BlockItem)) {
					matrixStack.scale(0.6F, 0.6F, 0.6F);
				} else {
					Quaternion rotation = Vector3f.XP.rotationDegrees(90);

					matrixStack.translate(0, -0.119, 0);
					matrixStack.mulPose(rotation);
					matrixStack.scale(0.35F, 0.35F, 0.35F);
				}
				
				Minecraft.getInstance().getItemRenderer().renderStatic(output, TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStack, buffer, 0);
				
				matrixStack.popPose();
			}
			
		} else if (!tileEntity.getItem(0).isEmpty()) {
			matrixStack.pushPose();
			matrixStack.translate(0, 0.2, 0);
			
			if ((tileEntity.getItem(0).getItem() instanceof BlockItem)) {
				matrixStack.scale(0.6F, 0.6F, 0.6F);
			} else {
				Quaternion rotation = Vector3f.XP.rotationDegrees(90);

				matrixStack.translate(0, -0.119, 0);
				matrixStack.mulPose(rotation);
				matrixStack.scale(0.35F, 0.35F, 0.35F);
			}
			
			Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.getItem(0), TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStack, buffer, 0);
			
			matrixStack.popPose();
		}
		
		matrixStack.popPose();
	}

	public class Internals extends Model {
		ModelPart coilsOff;
		ModelPart coilsOn;
		
		public Internals(RenderType renderType) {
			super((type) -> { return renderType; });
			
			this.coilsOff = createModelOff().bakeRoot();
			this.coilsOn = createModelOn().bakeRoot();
		}
		
		public static LayerDefinition createModelOff(){
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();
			
			partDef.addOrReplaceChild("heater_coil_bottom_one", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -5.0F, 8.0F, 1.0F, 1.0F).mirror(), PartPose.ZERO);
			partDef.addOrReplaceChild("heater_coil_bottom_two", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, 4.0F, 8.0F, 1.0F, 1.0F).mirror(), PartPose.ZERO);
			partDef.addOrReplaceChild("heater_coil_bottom_three", CubeListBuilder.create().texOffs(0, 4).addBox(-5.0F, 0.0F, -5.0F, 1.0F, 1.0F, 10.0F).mirror(), PartPose.ZERO);
			partDef.addOrReplaceChild("heater_coil_bottom_four", CubeListBuilder.create().texOffs(0, 4).addBox(4.0F, 0.0F, -5.0F, 1.0F, 1.0F, 10.0F).mirror(), PartPose.ZERO);
			
			return LayerDefinition.create(meshDef, 32, 32);
		}

		public static LayerDefinition createModelOn(){
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();
			
			partDef.addOrReplaceChild("heater_coil_bottom_one_on", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -5.0F, 8.0F, 1.0F, 1.0F).mirror(), PartPose.ZERO);
			partDef.addOrReplaceChild("heater_coil_bottom_two_on", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, 4.0F, 8.0F, 1.0F, 1.0F).mirror(), PartPose.ZERO);
			partDef.addOrReplaceChild("heater_coil_bottom_three_on", CubeListBuilder.create().texOffs(0, 20).addBox(-5.0F, 0.0F, -5.0F, 1.0F, 1.0F, 10.0F).mirror(), PartPose.ZERO);
			partDef.addOrReplaceChild("heater_coil_bottom_four_on", CubeListBuilder.create().texOffs(0, 20).addBox(4.0F, 0.0F, -5.0F, 1.0F, 1.0F, 10.0F).mirror(), PartPose.ZERO);
			
			return LayerDefinition.create(meshDef, 32, 32);
		}
		
		public void renderCoilsOff(PoseStack matrixStack, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn) {
			this.coilsOff.render(matrixStack, builder, combinedLightIn, combinedOverlayIn);
		}
		
		public void renderCoilsOn(PoseStack matrixStack, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn) {
			this.coilsOn.render(matrixStack, builder, combinedLightIn, combinedOverlayIn);
		}
		
		@Override
		public void renderToBuffer(PoseStack matrixStack, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn, float r, float g, float b, float a) {
			this.renderCoilsOff(matrixStack, builder, combinedLightIn, combinedOverlayIn);
		}	
	}
}