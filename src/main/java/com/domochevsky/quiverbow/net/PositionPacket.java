package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.HelperClient;

import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PositionPacket implements IMessageHandler<PositionMessage, IMessage>
{
	@Override
	public IMessage onMessage(PositionMessage message, MessageContext ctx)
	{
		if (ctx.side.isClient()) // just to make sure that the side is correct
		{
			// Setting the position of the passed in entity, for precision
			// purposes
			HelperClient.updateEntityPositionClient(message.entityID, message.posX, message.posY, message.posZ);
		}

		return null; // Don't care about returning anything
	}
}