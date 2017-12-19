package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.Helper_Client;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PositionPacket implements IMessageHandler<PositionMessage, IMessage>
{
	@Override
	public IMessage onMessage(PositionMessage message, MessageContext ctx)
	{
		if (ctx.side.isClient()) // just to make sure that the side is correct
		{
			// Setting the position of the passed in entity, for precision
			// purposes
			Helper_Client.updateEntityPositionClient(message.entityID, message.posX, message.posY, message.posZ);
		}

		return null; // Don't care about returning anything
	}
}