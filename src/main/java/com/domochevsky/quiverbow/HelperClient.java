package com.domochevsky.quiverbow;

import com.domochevsky.quiverbow.ai.AIProperties;
import com.domochevsky.quiverbow.armsassistant.EntityAA;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class HelperClient
{
	// Only ever called on client side
	public static void knockUserBackClient(byte strength)
	{
		EntityPlayer user = Minecraft.getMinecraft().player;

		user.motionZ += -MathHelper.cos((user.rotationYaw) * (float) Math.PI / 180.0F) * (strength * 0.08F);
		user.motionX += MathHelper.sin((user.rotationYaw) * (float) Math.PI / 180.0F) * (strength * 0.08F);
	}

	// We're guaranteed to be on client side
	public static void displayParticles(int entityID, EnumParticleTypes particleType, byte strength)
	{
		// String SFX;

		World world = Minecraft.getMinecraft().world;

		if (world == null)
		{
			return;
		} // World doesn't exist? oO

		Entity entity = world.getEntityByID(entityID);

		if (entity == null)
		{
			return;
		} // Entity doesn't exist

		int count = 0;

		while (count < strength)
		{
			world.spawnParticle(particleType, entity.posX + entity.motionX * (double) count / 4.0D,
					entity.posY + entity.motionY * (double) count / 4.0D,
					entity.posZ + entity.motionZ * (double) count / 4.0D, 0, 0.2D, 0);

			count += 1;
		}
	}

	// We're guaranteed to be on client side. Updating that entity's position
	// now
	public static void updateEntityPositionClient(int entityID, double x, double y, double z)
	{
		Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityID);

		if (entity == null)
		{
			return;
		} // Doesn't exist? Shame.

		// Entity exists, so setting its position now
		entity.setPosition(x, y, z);
	}

	// TODO: Replace with DataParameter
	// Informing the client about the fact that my (visual) state has changed
	public static void setTurretState(int entityID, boolean hasArmor, boolean hasWeaponUpgrade, boolean hasRidingUpgrade, boolean hasPlatingUpgrade, boolean hasCommunicationUpgrade)
	{
		Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityID);

		if (entity == null)
		{
			return;
		} // Doesn't exist? Shame.

		if (entity instanceof EntityAA)
		{
			EntityAA turret = (EntityAA) entity;

			// Keeping that updated as well, for the renderer
			turret.hasArmorUpgrade = hasArmor;
			turret.hasWeaponUpgrade = hasWeaponUpgrade;
			turret.hasRidingUpgrade = hasRidingUpgrade;
			turret.hasHeavyPlatingUpgrade = hasPlatingUpgrade;
			turret.hasCommunicationUpgrade = hasCommunicationUpgrade;
		}
	}

	// Informing the client about the fact that my inventory has changed
	public static void setTurretInventory(int entityID, int itemID, int itemSlot, int metadata)
	{
		Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityID);

		if (entity == null)
		{
			return;
		} // Doesn't exist? Shame.

		if (entity instanceof EntityAA)
		{
			EntityAA turret = (EntityAA) entity;

			if (itemSlot >= turret.storage.length) // Received a slot that is
			// higher than what we got,
			// so assuming that this
			// turret has a storage
			// upgrade
			{
				AIProperties.applyStorageUpgrade(turret); // Safeguard
			}

			if (itemID == -1)
			{
				turret.storage[itemSlot] = ItemStack.EMPTY; // Empty
			}
			else
			{
				turret.storage[itemSlot] = new ItemStack(Item.getItemById(itemID), 1, metadata); // There
				// ya
				// go.
				// Now
				// the
				// client
				// knows
				// about
				// that
				// too.
			}
		}
	}
}
