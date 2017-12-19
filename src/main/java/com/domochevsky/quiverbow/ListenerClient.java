package com.domochevsky.quiverbow;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import com.domochevsky.quiverbow.util.InventoryHelper;
import com.domochevsky.quiverbow.weapons.EnderRifle;
import com.domochevsky.quiverbow.weapons.FrostLancer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class ListenerClient
{
	// TODO: Do translations in json and get rid of this
	/* @SubscribeEvent(priority = EventPriority.LOW) public void
	 * renderPlayerRightItemUsage(RenderLivingEvent.Pre event) { if
	 * (!(event.getRenderer() instanceof RenderPlayer)) { return; }
	 * if(!(event.getEntity() instanceof EntityPlayer)) { return; }
	 * 
	 * //renderPlayerLeftItemUsage(event);
	 * 
	 * EntityPlayer entityPlayer = (EntityPlayer) event.getEntity();
	 * 
	 * if (entityPlayer.getHeldItem() == null) { return; } // Not holding
	 * anything
	 * 
	 * ItemStack mainHand = entityPlayer.getHeldItem(); if (!(mainHand.getItem()
	 * instanceof _WeaponBase)) { return; } // Not mine
	 * 
	 * _WeaponBase weapon = (_WeaponBase) mainHand.getItem();
	 * 
	 * if (weapon.getCooldown(mainHand) <= 0) { return; }
	 * 
	 * RenderPlayer renderer = ((RenderPlayer) event.renderer);
	 * 
	 * // You are holding something renderer.modelArmorChestplate.heldItemRight
	 * = 1; renderer.modelArmor.heldItemRight = 1;
	 * renderer.modelBipedMain.heldItemRight = 1;
	 * 
	 * // Hold it straight out renderer.modelArmorChestplate.aimedBow = true;
	 * renderer.modelArmor.aimedBow = true; renderer.modelBipedMain.aimedBow =
	 * true; } */

	private float defaultFOV = 70; // net.minecraft.client.Minecraft.getMinecraft().gameSettings.fovSetting;
	private boolean wasZoomedLastTick = false;
	private float fovLastTick = this.defaultFOV;

	// A weapon-independent way to tick zoom for weapons. Maybe that'll squash
	// this issue once and for all.
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if (event.side.isServer())
		{
			return;
		} // Only doing this on client side
		if (event.player != Minecraft.getMinecraft().player)
		{
			return;
		} // Only for THIS player, that belongs to the client

		boolean holdingWeapon = false;
		boolean isSneaking = event.player.isSneaking();
		float zoomFOV = 0;

		// TODO: Create IZoomable interface
		ItemStack heldWeapon;
		if (!(heldWeapon = InventoryHelper.findItemInHandsByClass(event.player, FrostLancer.class)).isEmpty())
		{
			holdingWeapon = true;
			zoomFOV = ((FrostLancer) heldWeapon.getItem()).ZoomMax;
		}
		else if (!(heldWeapon = InventoryHelper.findItemInHandsByClass(event.player, EnderRifle.class)).isEmpty())
		{
			holdingWeapon = true;
			zoomFOV = ((EnderRifle) heldWeapon.getItem()).ZoomMax;
		}

		if (this.wasZoomedLastTick)
		{
			if (!holdingWeapon)
			{
				this.zoomOut();
			}
			else if (!isSneaking)
			{
				this.zoomOut();
			}
			else // else, zoomed, holding the weapon and sneaking. No change
			// there
			{
				if (zoomFOV != this.fovLastTick) // May have switched to another
				// rifle
				{
					net.minecraft.client.Minecraft.getMinecraft().gameSettings.fovSetting = zoomFOV;
				}
			}
		}
		else
		{
			if (holdingWeapon && isSneaking)
			{
				this.zoomIn(zoomFOV);
			}
			// else, not zoomed in and either not holding the weapon or not
			// sneaking
		}
	}

	private void zoomIn(float zoomFOV)
	{
		this.defaultFOV = net.minecraft.client.Minecraft.getMinecraft().gameSettings.fovSetting; // Recording
		// default
		// FOV
		net.minecraft.client.Minecraft.getMinecraft().gameSettings.fovSetting = zoomFOV; // And
		// setting
		// the
		// setting
		// to
		// our
		// new
		// zoom
		// FOV

		this.wasZoomedLastTick = true;
	}

	private void zoomOut()
	{
		net.minecraft.client.Minecraft.getMinecraft().gameSettings.fovSetting = this.defaultFOV; // Restoring
		// recorded
		// default
		// FOV
		this.wasZoomedLastTick = false;
	}
}
