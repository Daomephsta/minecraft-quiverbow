package com.domochevsky.quiverbow.renderer;


import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.armsassistant.*;
import com.domochevsky.quiverbow.models.WeaponModel.BakedWeaponModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class RenderAA extends RenderLiving<EntityArmsAssistant>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(QuiverbowMain.MODID, "textures/entity/arms_assistant.png");

	public RenderAA(RenderManager renderManager)
	{
		super(renderManager, new ModelAA(), 1.0F);
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
		renderEquippedItems(entity);
		renderStoredItems(entity);
	}

	protected void renderEquippedItems(EntityArmsAssistant turret)
	{
		ItemStack itemstack = turret.getHeldItemMainhand();
		float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
		float rotYawInterpHead = interpolateRotation(turret.prevRotationYawHead, turret.rotationYawHead, partialTicks);
		float netYaw = -rotYawInterpHead;
		
		if (!itemstack.isEmpty())
		{
			GlStateManager.pushMatrix();
			{	
				GlStateManager.rotate(netYaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0.21F, 1.0F, 0.0F);
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
					GlStateManager.rotate(netYaw, 0.0F, 1.0F, 0.0F);
					GlStateManager.translate(0.52F, 0.74F, -0.11F);
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
		if (model instanceof BakedWeaponModel)
		{
			multBuffer.clear();
			Matrix4f transform = ((BakedWeaponModel) model).getAATransforms().forRail(rail);
			transform.store(multBuffer);
			multBuffer.flip();
			GlStateManager.multMatrix(multBuffer);
		}
	}
    
	private void renderStoredItems(EntityArmsAssistant turret)
	{
		float modX = 0;
		float modY = 0;

		int iconsPerRow = 4;
		int iconMulti = 1;
		int iconsDrawn = 0;

		IItemHandler turretInv = turret.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		for (int slot = 0; slot < turretInv.getSlots(); slot++)
		{
			ItemStack itemstack = turretInv.getStackInSlot(slot);

			// System.out.println("[RENDER] Items.ACK in slot " + slot + " is "
			// + itemstack);

			if (!itemstack.isEmpty() && itemstack.getItem() != null)
			{
				GlStateManager.pushMatrix();
				{
					GlStateManager.translate(0.32F, 1.19F + modY, 0.32F - modX);

					modX += 0.15f; // One step back

					float scale = 0.15F; // Smaller, to make that less blatant

					GlStateManager.scale(scale, -scale, scale);
					GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(195.0F, 0.0F, 0.0F, 1.0F);

					int color = Minecraft.getMinecraft().getItemColors().colorMultiplier(itemstack, 0);
					float f4 = (color >> 16 & 255) / 255.0F;
					float f5 = (color >> 8 & 255) / 255.0F;
					float f2 = (color & 255) / 255.0F;
					GlStateManager.color(f4, f5, f2, 1.0F);

					Minecraft.getMinecraft().getRenderItem().renderItem(itemstack,
							ItemCameraTransforms.TransformType.NONE);
				}
				GlStateManager.popMatrix();

				iconsDrawn += 1;
			}

			if (iconsDrawn == (iconsPerRow * iconMulti)) // This many items are
			// shown per row
			{
				modX = 0; // Reset
				modY = 0.15f; // One row down
				iconMulti += 1;
			}

			slot += 1;
		}
	}
}
