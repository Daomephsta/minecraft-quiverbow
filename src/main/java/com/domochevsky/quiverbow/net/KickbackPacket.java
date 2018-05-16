package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.HelperClient;

import net.minecraftforge.fml.common.network.simpleimpl.*;

public class KickbackPacket implements IMessageHandler<KickbackMessage, IMessage>
{
	@Override
	public IMessage onMessage(KickbackMessage message, MessageContext ctx)
	{
		if (ctx.side.isClient()) // just to make sure that the side is correct
		{
			HelperClient.knockUserBackClient(message.strength);
		}

		return null; // Don't care about returning anything
	}
}
