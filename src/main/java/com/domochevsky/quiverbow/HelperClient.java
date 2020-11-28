package com.domochevsky.quiverbow;

import com.domochevsky.quiverbow.armsassistant.EntityArmsAssistant;
import com.domochevsky.quiverbow.armsassistant.UpgradeRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

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
			world.spawnParticle(particleType, entity.posX + entity.motionX * count / 4.0D, entity.posY
				+ entity.motionY * count / 4.0D, entity.posZ + entity.motionZ * count / 4.0D, 0, 0.2D,
				0);
		}
	}

	// Informing the client about the fact that my inventory has changed
	public static void setTurretInventory(EntityArmsAssistant turret, ItemStack stack, int itemSlot)
	{
		// Received a slot that is higher than what we got, so assuming that
		// this turret has a storage upgrade
		IItemHandler inv = turret.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if (itemSlot >= inv.getSlots())
		{
			turret.applyUpgrade(UpgradeRegistry.STORAGE);
		}
		inv.insertItem(itemSlot, stack, false);
	}
}
