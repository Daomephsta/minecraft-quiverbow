package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.HelperClient;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ParticleMessageHandler implements ISidedMessageHandler<ParticleMessage, IMessage>
{
	@Override
	public void processMessage(ParticleMessage message, MessageContext ctx)
	{
		HelperClient.displayParticles(message.entity, message.particleType, message.strength);
	}
	
	@Override
	public Side getSide()
	{
		return Side.CLIENT;
	}
}