package com.domochevsky.quiverbow;

import com.domochevsky.quiverbow.ai.AIProperties;
import com.domochevsky.quiverbow.armsassistant.EntityAA;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class HelperClient
{
	// Only ever called on client side
	public static void knockUserBackClient(int strength)
	{
		EntityPlayer user = Minecraft.getMinecraft().player;

		user.motionZ += -MathHelper.cos((user.rotationYaw) * (float) Math.PI / 180.0F) * (strength * 0.08F);
		user.motionX += MathHelper.sin((user.rotationYaw) * (float) Math.PI / 180.0F) * (strength * 0.08F);
	}

	// We're guaranteed to be on client side
	public static void displayParticles(Entity entity, EnumParticleTypes particleType, byte strength)
	{
		World world = Minecraft.getMinecraft().world;
		if (world == null) return;

		for (int count = 0; count < strength; count++)
		{
			world.spawnParticle(particleType, entity.posX + entity.motionX * (double) count / 4.0D, entity.posY
				+ entity.motionY * (double) count / 4.0D, entity.posZ + entity.motionZ * (double) count / 4.0D, 0, 0.2D,
				0);
		}
	}

	// TODO: Replace with DataParameter
	// Informing the client about the fact that my (visual) state has changed
	public static void setTurretState(EntityAA turret, boolean hasArmor, boolean hasWeaponUpgrade, boolean hasRidingUpgrade, boolean hasPlatingUpgrade, boolean hasCommunicationUpgrade)
	{
		turret.hasArmorUpgrade = hasArmor;
		turret.hasWeaponUpgrade = hasWeaponUpgrade;
		turret.hasRidingUpgrade = hasRidingUpgrade;
		turret.hasHeavyPlatingUpgrade = hasPlatingUpgrade;
		turret.hasCommunicationUpgrade = hasCommunicationUpgrade;
	}

	// Informing the client about the fact that my inventory has changed
	public static void setTurretInventory(EntityAA turret, ItemStack stack, int itemSlot)
	{
		// Received a slot that is higher than what we got, so assuming that
		// this turret has a storage upgrade
		if (itemSlot >= turret.storage.length)
		{
			AIProperties.applyStorageUpgrade(turret);
		}
		turret.storage[itemSlot] = stack;
	}
}
