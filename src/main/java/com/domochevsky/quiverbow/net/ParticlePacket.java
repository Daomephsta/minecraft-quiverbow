package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.HelperClient;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ParticlePacket implements IMessageHandler<ParticleMessage, IMessage>
{
	@Override
	public IMessage onMessage(ParticleMessage message, MessageContext ctx)
	{
		if (ctx.side.isClient()) // just to make sure that the side is correct
		{
			HelperClient.displayParticles(message.entityID, message.particleType, message.strength);
		}

		return null; // Don't care about returning anything
	}
}