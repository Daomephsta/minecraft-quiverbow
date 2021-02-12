package com.domochevsky.quiverbow.net;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ParticleMessageHandler implements ISidedMessageHandler<ParticleMessage, IMessage>
{
    @Override
    public void processMessage(ParticleMessage message, MessageContext ctx)
    {
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityID);
        /* It's possible for the entity to be null on the client world if it
         * moves out of the client's loading range after this packet has been
         * sent */
        if(entity == null) return;
        for (int count = 0; count < message.strength; count++)
        {
            Minecraft.getMinecraft().world.spawnParticle(message.particleType,
                entity.posX + entity.motionX * count / 4.0D,
                entity.posY + entity.motionY * count / 4.0D,
                entity.posZ + entity.motionZ * count / 4.0D,
                0, 0.2D, 0);
        }
    }

    @Override
    public Side getSide()
    {
        return Side.CLIENT;
    }
}