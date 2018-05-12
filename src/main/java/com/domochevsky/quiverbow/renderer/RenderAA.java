package com.domochevsky.quiverbow.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.domochevsky.quiverbow.Main.Constants;
import com.domochevsky.quiverbow.armsassistant.EntityAA;
import com.domochevsky.quiverbow.armsassistant.ModelAA;

public class RenderAA extends RenderLiving<EntityAA>
{
	private static ResourceLocation texture = new ResourceLocation(Constants.MODID,
			"textures/entity/ArmsAssistant.png");

	public RenderAA(RenderManager renderManager)
	{
		super(renderManager, new ModelAA(), 1.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityAA entity)
	{
		return texture;
	}

	@Override
	protected void renderLivingAt(EntityAA entity, double x, double y, double z)
	{
		renderEquippedItems(entity);
		renderStoredItems(entity);
	}

	protected void renderEquippedItems(EntityAA entity)
	{
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		ItemStack itemstack = entity.getHeldItem(EnumHand.MAIN_HAND);

		if (!itemstack.isEmpty() && itemstack.getItem() != null)
		{
			GL11.glPushMatrix();

			GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

			float scale = 0.625F;

			GL11.glTranslatef(0.3F, 0.2F, -0.25F); // 0.0F, 0.1875F, 0.0F,
			// left/right, up/down,
			// forward/backward?
			GL11.glScalef(scale, -scale, scale);
			GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F); // -100
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

			int color = Minecraft.getMinecraft().getItemColors().getColorFromItemstack(itemstack, 0);
			float colorR = (float) (color >> 16 & 255) / 255.0F;
			float colorB = (float) (color >> 8 & 255) / 255.0F;
			float colorG = (float) (color & 255) / 255.0F;
			GL11.glColor4f(colorR, colorB, colorG, 1.0F);

			Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ItemCameraTransforms.TransformType.NONE);

			GL11.glPopMatrix();
		}

		EntityAA turret = (EntityAA) entity;

		if (turret.hasWeaponUpgrade) // Has a second weapon rail, so drawing the
		// weapon from that
		{
			itemstack = entity.getHeldItemOffhand();

			if (!itemstack.isEmpty() && itemstack.getItem() != null)
			{
				GL11.glPushMatrix();

				GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

				float scale = 0.625F;

				GL11.glTranslatef(0.6F, 0.5F, -0.25F); // 0.0F, 0.1875F, 0.0F,
				// left/right, up/down,
				// forward/backward?
				GL11.glScalef(scale, -scale, scale);
				GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F); // -100
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

				int color = Minecraft.getMinecraft().getItemColors().getColorFromItemstack(itemstack, 0);
				float colorR = (float) (color >> 16 & 255) / 255.0F;
				float colorB = (float) (color >> 8 & 255) / 255.0F;
				float colorG = (float) (color & 255) / 255.0F;
				GL11.glColor4f(colorR, colorB, colorG, 1.0F);

				Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ItemCameraTransforms.TransformType.NONE);

				GL11.glPopMatrix();
			}
		}

		this.renderStoredItems((EntityAA) entity);
	}

	private void renderStoredItems(EntityAA turret)
	{
		int slot = 0;
		float modX = 0;
		float modY = 0;

		int iconsPerRow = 4;
		int iconMulti = 1;
		int iconsDrawn = 0;

		while (slot < turret.storage.length)
		{
			ItemStack itemstack = turret.storage[slot];

			// System.out.println("[RENDER] Items.ACK in slot " + slot + " is "
			// + itemstack);

			if (!itemstack.isEmpty() && itemstack.getItem() != null)
			{
				GL11.glPushMatrix();

				GL11.glTranslatef(0.32F, 0.35F + modY, -0.35F + modX);

				modX += 0.15f; // One step back

				float scale = 0.08F; // Smaller, to make that less blatant

				GL11.glScalef(scale, -scale, scale);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

				int color = Minecraft.getMinecraft().getItemColors().getColorFromItemstack(itemstack, 0);
				float f4 = (float) (color >> 16 & 255) / 255.0F;
				float f5 = (float) (color >> 8 & 255) / 255.0F;
				float f2 = (float) (color & 255) / 255.0F;
				GL11.glColor4f(f4, f5, f2, 1.0F);

				Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ItemCameraTransforms.TransformType.NONE);

				GL11.glPopMatrix();

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
