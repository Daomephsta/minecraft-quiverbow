package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.HelperClient;

import net.minecraftforge.fml.common.network.simpleimpl.*;

public class TurretInventoryPacket implements IMessageHandler<TurretInventoryMessage, IMessage>
{
	@Override
	public IMessage onMessage(TurretInventoryMessage message, MessageContext ctx)
	{
		if (ctx.side.isClient()) // just to make sure that the side is correct
		{
			HelperClient.setTurretInventory(message.entityID, message.itemID, message.itemSlot, message.metadata);
		}

		return null; // Don't care about returning anything
	}
}
