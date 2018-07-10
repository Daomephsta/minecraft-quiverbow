package com.domochevsky.quiverbow.renderer;

import javax.vecmath.Matrix4f;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.armsassistant.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
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
		GlStateManager.translate(x, y, z);
		renderEquippedItems(entity);
		renderStoredItems(entity);
	}

	protected void renderEquippedItems(EntityArmsAssistant turret)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F);

		ItemStack itemstack = turret.getHeldItemMainhand();

		if (!itemstack.isEmpty())
		{
			GlStateManager.pushMatrix();
			{
				GlStateManager.translate(0.24F, 0.6F, 0.5F);

				renderItemOnRail(turret, itemstack);
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
					GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);

					float scale = 0.625F;

					GlStateManager.translate(0.6F, 0.5F, -0.25F); // 0.0F, 0.1875F, 0.0F,
					// left/right, up/down,
					// forward/backward?
					GlStateManager.scale(scale, -scale, scale);
					GlStateManager.rotate(-20.0F, 1.0F, 0.0F, 0.0F); // -100
					GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);

					renderItemOnRail(turret, itemstack);
				}
				GlStateManager.popMatrix();
			}
		}

		this.renderStoredItems(turret);
	}
	
	private void renderItemOnRail(EntityArmsAssistant turret, ItemStack stack)
	{
		int color = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, 0);
		float colorR = (float) (color >> 16 & 255) / 255.0F;
		float colorB = (float) (color >> 8 & 255) / 255.0F;
		float colorG = (float) (color & 255) / 255.0F;
		GlStateManager.color(colorR, colorB, colorG, 1.0F);
		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, turret.world, turret);
		handleTransform(model);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
	}

	/** From {@link ForgeHooksClient#handleCameraTransforms()} START **/
	private static final Matrix4f flipX;
	static
	{
		flipX = new Matrix4f();
		flipX.setIdentity();
		flipX.m00 = -1;
	}
	
	private void handleTransform(IBakedModel model)
	{
		// TODO Track down cause of bug that this line works around 
		GlStateManager.rotate(-3F, 0.0F, 1.0F, 0.0F);
		Matrix4f transform = AATransforms.getTransform(model);
		if (transform == null) return;
		Matrix4f matrix = new Matrix4f(transform);
        ForgeHooksClient.multiplyCurrentGlMatrix(matrix);
	}
    /** From {@link ForgeHooksClient#handleCameraTransforms()} END **/
    

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
					float f4 = (float) (color >> 16 & 255) / 255.0F;
					float f5 = (float) (color >> 8 & 255) / 255.0F;
					float f2 = (float) (color & 255) / 255.0F;
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
