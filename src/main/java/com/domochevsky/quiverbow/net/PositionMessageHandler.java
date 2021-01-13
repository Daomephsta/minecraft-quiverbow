package com.domochevsky.quiverbow.net;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PositionMessageHandler implements ISidedMessageHandler<PositionMessage, IMessage>
{
    @Override
    public void processMessage(PositionMessage message, MessageContext ctx)
    {
        Minecraft.getMinecraft().world.getEntityByID(message.entityID).setPosition(message.posX, message.posY,
            message.posZ);
    }

    @Override
    public Side getSide()
    {
        return Side.CLIENT;
    }
}