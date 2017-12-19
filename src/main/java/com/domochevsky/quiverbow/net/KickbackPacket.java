package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.Helper_Client;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class KickbackPacket implements IMessageHandler<KickbackMessage, IMessage>
{
	@Override
	public IMessage onMessage(KickbackMessage message, MessageContext ctx)
	{
		if (ctx.side.isClient()) // just to make sure that the side is correct
		{
			Helper_Client.knockUserBackClient(message.strength);
		}

		return null; // Don't care about returning anything
	}
}
