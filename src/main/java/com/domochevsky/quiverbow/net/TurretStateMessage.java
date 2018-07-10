package com.domochevsky.quiverbow.net;

import java.util.List;

import com.domochevsky.quiverbow.armsassistant.*;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TurretStateMessage implements IMessage
{
	int turretID;
	int[] upgrades;

	public TurretStateMessage()
	{} // this constructor is required otherwise you'll get errors (used
		// somewhere in fml through reflection)

	// Sending a message to the client to inform them about turret state changes
	public TurretStateMessage(EntityArmsAssistant turret, List<IArmsAssistantUpgrade> upgrades)
	{
		this.turretID = turret.getEntityId();
		this.upgrades = upgrades.stream().mapToInt(UpgradeRegistry::getUpgradeIntegerID).toArray();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.turretID = buf.readInt();
		int upgradeCount = buf.readInt();
		this.upgrades = new int[upgradeCount];
		for(int u = 0; u < upgradeCount; u++)
		{
			this.upgrades[u] = buf.readInt();
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(turretID);
		buf.writeInt(upgrades.length);
		for(int upgrade : upgrades)
		{
			buf.writeInt(upgrade);
		}
	}
}
