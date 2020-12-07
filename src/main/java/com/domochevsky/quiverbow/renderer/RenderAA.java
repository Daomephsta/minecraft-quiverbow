package com.domochevsky.quiverbow.renderer;


import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.armsassistant.EntityArmsAssistant;
import com.domochevsky.quiverbow.armsassistant.UpgradeRegistry;
import com.domochevsky.quiverbow.client.render.ModelArmsAssistant;
import com.domochevsky.quiverbow.models.WeaponModel;
import com.domochevsky.quiverbow.models.WeaponModelOld;
import com.domochevsky.quiverbow.models.WeaponModelOld.BakedWeaponModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

public class RenderAA extends RenderLiving<EntityArmsAssistant>
{
	private static final float PIXEL = 0.0625F;
	private static final ResourceLocation TEXTURE = new ResourceLocation(QuiverbowMain.MODID, "textures/entity/arms_assistant.png");

	public RenderAA(RenderManager renderManager)
	{
		super(renderManager, new ModelArmsAssistant(), 0.6F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityArmsAssistant entity)
	{
		return TEXTURE;
	}

	@Override
	protected void renderLivingAt(EntityArmsAssistant entity, double x, double y, double z)
	{
		super.renderLivingAt(entity, x, y, z);
		if (!entity.hasUpgrade(UpgradeRegistry.MOBILITY))
			GlStateManager.translate(0.0D, -1.0D / 16.0D, 0.0D);
		renderEquippedItems(entity);
		renderStoredItems(entity);
	}

	protected void renderEquippedItems(EntityArmsAssistant turret)
	{
		ItemStack itemstack = turret.getHeldItemMainhand();
		float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
		float rotPitchInterp = interpolateRotation(turret.prevRotationPitch, turret.rotationPitch, partialTicks);
		float rotYawInterpHead = interpolateRotation(turret.prevRotationYawHead, turret.rotationYawHead, partialTicks);
		float netYaw = -rotYawInterpHead;

		if (!itemstack.isEmpty())
		{
			GlStateManager.pushMatrix();
			{
				//Yaw
				GlStateManager.rotate(netYaw, 0.0F, 1.0F, 0.0F);
				//Translate to rail rotation point
				GlStateManager.translate(6.0F * PIXEL, 15.0F * PIXEL, -5.0F * PIXEL);
				//Rail pitch
				GlStateManager.rotate(rotPitchInterp, 1.0F, 0.0F, 0.0F);
				renderItemOnRail(turret, itemstack, EnumHand.MAIN_HAND);
			}
			GlStateManager.popMatrix();
		}

		if (turret.hasUpgrade(UpgradeRegistry.EXTRA_WEAPON)) // Has a second weapon rail, so drawing the weapon from that
		{
			itemstack = turret.getHeldItemOffhand();
			if (!itemstack.isEmpty())
			{
				GlStateManager.pushMatrix();
				{
				    //Yaw
				    GlStateManager.rotate(netYaw, 0.0F, 1.0F, 0.0F);
	                //Translate to rail rotation point
	                GlStateManager.translate(-6.0F * PIXEL, 15.0F * PIXEL, -5.0F * PIXEL);
	                //Rail pitch
	                GlStateManager.rotate(rotPitchInterp, 1.0F, 0.0F, 0.0F);
					renderItemOnRail(turret, itemstack, EnumHand.OFF_HAND);
				}
				GlStateManager.popMatrix();
			}
		}

		this.renderStoredItems(turret);
	}

	private void renderItemOnRail(EntityArmsAssistant turret, ItemStack stack, EnumHand rail)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		int color = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, 0);
		float colorR = (color >> 16 & 255) / 255.0F;
		float colorB = (color >> 8 & 255) / 255.0F;
		float colorG = (color & 255) / 255.0F;
		GlStateManager.color(colorR, colorB, colorG, 1.0F);
		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, turret.world, turret);
		handleTransform(model, rail);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
	}

	private static final FloatBuffer multBuffer = BufferUtils.createFloatBuffer(16);
	private void handleTransform(IBakedModel model, EnumHand rail)
	{
	    Matrix4f transform = null;
		if (model instanceof WeaponModelOld.BakedWeaponModel)
		    transform = ((BakedWeaponModel) model).getAATransforms().forRail(rail);
		else if (model instanceof WeaponModel.Baked)
            transform = ((WeaponModel.Baked) model).getAATransforms().forRail(rail);
		if (transform != null)
		{
            multBuffer.clear();
            transform.store(multBuffer);
            multBuffer.flip();
            GlStateManager.multMatrix(multBuffer);
		}
	}

	private void renderStoredItems(EntityArmsAssistant turret)
	{

	}
}
