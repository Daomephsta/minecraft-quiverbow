package com.domochevsky.quiverbow;

import com.domochevsky.quiverbow.config.QuiverbowConfig;
import com.domochevsky.quiverbow.util.InventoryHelper;
import com.domochevsky.quiverbow.weapons.base.IScopedWeapon;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ListenerClient
{
	private float defaultFOVModifier = 0;
	private boolean wasZoomedLastTick = false;
	private float currentZoomFovModifier;
	private float fovModifierHand, fovModifierHandLastTick;

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)
	{
		handleZoom();
	}

	@SubscribeEvent
	public void onFovModifierEvent(EntityViewRenderEvent.FOVModifier event)
	{
		if (wasZoomedLastTick) event.setFOV(currentZoomFovModifier);
	}

	private void handleZoom()
	{
		//Check that the Minecraft singleton is instantiated
		if (Minecraft.getMinecraft() == null) return;
		//Check that a world is loaded
		if (Minecraft.getMinecraft().world == null) return;

		updateFovModifierHand();

		boolean holdingWeapon = false;
		boolean shouldZoom = false;
		float maxZoomFovModifier = 0;

		ItemStack heldWeapon;
		if (!(heldWeapon = InventoryHelper.findItemInHandsByClass(Minecraft.getMinecraft().player, IScopedWeapon.class))
				.isEmpty())
		{
			holdingWeapon = true;
			shouldZoom = ((IScopedWeapon) heldWeapon.getItem()).shouldZoom(Minecraft.getMinecraft().world,
					Minecraft.getMinecraft().player, heldWeapon);
			maxZoomFovModifier = getFOVModifier(((IScopedWeapon) heldWeapon.getItem()).getMaxZoom());
		}
		if (this.wasZoomedLastTick)
		{
			if (currentZoomFovModifier > maxZoomFovModifier && shouldZoom)
			{
				zoomIn(maxZoomFovModifier);
			}
			else if (currentZoomFovModifier < defaultFOVModifier && (!holdingWeapon || !shouldZoom))
			{
				zoomOut();
			}
		}
		else
		{
			if (holdingWeapon && shouldZoom)
			{
				this.startZooming();
				this.zoomIn(maxZoomFovModifier);
			}
			// else, not zoomed in and either not holding the weapon or not sneaking
		}
	}

	private void startZooming()
	{
		this.defaultFOVModifier = getFOVModifier(Minecraft.getMinecraft().gameSettings.fovSetting); // Recording default FOV modifier
		this.wasZoomedLastTick = true;
	}

	private void zoomIn(float zoomFovModifier)
	{
		currentZoomFovModifier -= zoomFovModifier / QuiverbowConfig.zoomSpeed;
	}

	private void zoomOut()
	{
		currentZoomFovModifier += this.defaultFOVModifier / QuiverbowConfig.zoomSpeed;
		if (currentZoomFovModifier >= defaultFOVModifier)
		{
			currentZoomFovModifier = defaultFOVModifier;
			//Move the camera a tiny bit to get MC/LWJGL to render any chunks culled while zoomed in
			Minecraft.getMinecraft().player.turn(0.1F, 0.0F);
		}
	}

	//From EntityRenderer, modified for our purposes
	private float getFOVModifier(float fovIn)
	{
		Minecraft mc = Minecraft.getMinecraft();
		Entity entity = mc.getRenderViewEntity();
		float partialTicks = mc.getRenderPartialTicks();
		float fov = fovIn
				* (this.fovModifierHandLastTick + (this.fovModifierHand - this.fovModifierHandLastTick) * partialTicks);

		IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(mc.world, entity, partialTicks);

		if (iblockstate.getMaterial() == Material.WATER)
		{
			fov = fov * 60.0F / 70.0F;
		}

		return fov;
	}

	//From EntityRenderer
	private void updateFovModifierHand()
	{
		Minecraft mc = Minecraft.getMinecraft();
		float fov = 1.0F;

		if (mc.getRenderViewEntity() instanceof AbstractClientPlayer)
		{
			AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) mc.getRenderViewEntity();
			fov = abstractclientplayer.getFovModifier();
		}

		this.fovModifierHandLastTick = this.fovModifierHand;
		this.fovModifierHand += (fov - this.fovModifierHand) * 0.5F;

		if (this.fovModifierHand > 1.5F)
		{
			this.fovModifierHand = 1.5F;
		}

		if (this.fovModifierHand < 0.1F)
		{
			this.fovModifierHand = 0.1F;
		}
	}

}
