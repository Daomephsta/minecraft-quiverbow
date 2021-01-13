package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.HelperClient;

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
        HelperClient.displayParticles(entity ,
            message.particleType, message.strength);
    }
    
    @Override
    public Side getSide()
    {
        return Side.CLIENT;
    }
}