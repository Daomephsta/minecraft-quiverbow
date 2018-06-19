package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.armsassistant.EntityAA;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class NetHelper
{
	// Same as below, but sends it to all players
	public static void sendParticleMessageToAllPlayers(World world, Entity entity, EnumParticleTypes particle, byte strength)
	{
		// Server-use only
		if (world.isRemote) return; 
		PacketHandler.net.sendToDimension(new ParticleMessage(entity, particle, strength), world.provider.getDimension());
	}

	// Sends a custom packet to the other side
	public static void sendParticleMessage(Entity user, Entity entity, EnumParticleTypes particle, byte strength)
	{
		if (user instanceof EntityPlayerMP)
		{
			IMessage msg = new ParticleMessage(entity, particle, strength);
			PacketHandler.net.sendTo(msg, (EntityPlayerMP) user);
		}
		// else, not a player we're trying to send this to
	}

	// Same as below, but sends it to all players
	public static void sendPositionMessageToAllPlayers(World world, Entity entity, double x, double y, double z)
	{
		// Server-use only
		if (world.isRemote) return;
		PacketHandler.net.sendToDimension(new PositionMessage(entity, x, y, z), world.provider.getDimension());
	}

	// Same as above, but tries to save bandwidth by only sending packets to
	// players who actually have a chance to see this event
	public static void sendPositionMessageToPlayersInRange(World world, Entity entity, double x, double y, double z)
	{
		// Server-use only
		if (world.isRemote) return;
		PacketHandler.net.sendToAllTracking(new PositionMessage(entity, x, y, z), entity);
	}

	// Informing the player that they just got knocked back by their weapon
	public static void sendKickbackMessage(Entity user, int strength)
	{
		if (user instanceof EntityPlayerMP)
		{
			IMessage msg = new KickbackMessage(strength);
			PacketHandler.net.sendTo(msg, (EntityPlayerMP) user);
		}
	}

	public static void sendTurretStateMessageToPlayersInRange(World world, EntityAA turret, boolean hasArmor, boolean hasWeaponUpgrade, boolean hasRidingUpgrade, boolean hasPlatingUpgrade, boolean hasComUpgrade)
	{
		// Server-use only
		if (world.isRemote) return;
		PacketHandler.net.sendToAllTracking(new TurretStateMessage(turret, hasArmor, hasWeaponUpgrade, hasRidingUpgrade, hasPlatingUpgrade, hasComUpgrade), turret);
	}

	public static void sendTurretInventoryMessageToPlayersInRange(World world, EntityAA turret, ItemStack stack, int itemSlot)
	{
		// Server-use only
		if (world.isRemote) return;
		PacketHandler.net.sendToAllTracking(new TurretInventoryMessage(turret, stack, itemSlot),turret);
	}
}
