package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.armsassistant.EntityAA;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TurretStateMessage implements IMessage
{
	EntityAA turret;
	boolean hasArmorUpgrade;
	boolean hasWeaponUpgrade;
	boolean hasRidingUpgrade;
	boolean hasPlatingUpgrade;
	boolean hasCommunicationUpgrade;

	public TurretStateMessage()
	{} // this constructor is required otherwise you'll get errors (used
		// somewhere in fml through reflection)

	// Sending a message to the client to inform them about turret state changes
	public TurretStateMessage(EntityAA turret, boolean hasArmor, boolean hasWeaponUpgrade, boolean hasRidingUpgrade,
			boolean hasPlatingUpgrade, boolean hasComUpgrade)
	{
		this.turret = turret;
		this.hasArmorUpgrade = hasArmor;
		this.hasWeaponUpgrade = hasWeaponUpgrade;
		this.hasRidingUpgrade = hasRidingUpgrade;
		this.hasPlatingUpgrade = hasPlatingUpgrade;
		this.hasCommunicationUpgrade = hasComUpgrade;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.turret = (EntityAA) Minecraft.getMinecraft().world.getEntityByID(buf.readInt());
		this.hasArmorUpgrade = buf.readBoolean();
		this.hasWeaponUpgrade = buf.readBoolean();
		this.hasRidingUpgrade = buf.readBoolean();
		this.hasPlatingUpgrade = buf.readBoolean();
		this.hasCommunicationUpgrade = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(turret.getEntityId());
		buf.writeBoolean(this.hasArmorUpgrade);
		buf.writeBoolean(this.hasWeaponUpgrade);
		buf.writeBoolean(this.hasRidingUpgrade);
		buf.writeBoolean(this.hasPlatingUpgrade);
		buf.writeBoolean(this.hasCommunicationUpgrade);
	}
}
