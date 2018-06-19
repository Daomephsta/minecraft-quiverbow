package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.HelperClient;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TurretInventoryMessageHandler implements ISidedMessageHandler<TurretInventoryMessage, IMessage>
{
	@Override
	public void processMessage(TurretInventoryMessage message, MessageContext ctx)
	{
		HelperClient.setTurretInventory(message.turret, message.stack, message.itemSlot);
	}
	
	@Override
	public Side getSide()
	{
		return Side.CLIENT;
	}
}
