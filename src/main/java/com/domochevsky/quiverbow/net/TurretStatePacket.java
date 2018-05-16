package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.HelperClient;

import net.minecraftforge.fml.common.network.simpleimpl.*;

public class TurretStatePacket implements IMessageHandler<TurretStateMessage, IMessage>
{
	@Override
	public IMessage onMessage(TurretStateMessage message, MessageContext ctx)
	{
		if (ctx.side.isClient()) // just to make sure that the side is correct
		{
			HelperClient.setTurretState(message.entityID, message.hasArmorUpgrade, message.hasWeaponUpgrade,
					message.hasRidingUpgrade, message.hasPlatingUpgrade, message.hasCommunicationUpgrade);
		}

		return null; // Don't care about returning anything
	}
}
