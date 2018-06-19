package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.HelperClient;
import com.domochevsky.quiverbow.armsassistant.EntityAA;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TurretInventoryMessageHandler implements ISidedMessageHandler<TurretInventoryMessage, IMessage>
{
	@Override
	public void processMessage(TurretInventoryMessage message, MessageContext ctx)
	{
		HelperClient.setTurretInventory((EntityAA) Minecraft.getMinecraft().world.getEntityByID(message.turretID),
			message.stack, message.itemSlot);
	}
	
	@Override
	public Side getSide()
	{
		return Side.CLIENT;
	}
}
