package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.HelperClient;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TurretStateMessageHandler implements ISidedMessageHandler<TurretStateMessage, IMessage>
{
	@Override
	public void processMessage(TurretStateMessage message, MessageContext ctx)
	{
		HelperClient.setTurretState(message.turret, message.hasArmorUpgrade, message.hasWeaponUpgrade,
			message.hasRidingUpgrade, message.hasPlatingUpgrade, message.hasCommunicationUpgrade);
	}

	@Override
	public Side getSide()
	{
		return Side.CLIENT;
	}
}
