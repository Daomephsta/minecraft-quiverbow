package com.domochevsky.quiverbow.net;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
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
            int strength = message.strength;
            EntityPlayer user = Minecraft.getMinecraft().player;

            user.motionZ += -MathHelper.cos((user.rotationYaw) * (float) Math.PI / 180.0F) * (strength * 0.08F);
            user.motionX += MathHelper.sin((user.rotationYaw) * (float) Math.PI / 180.0F) * (strength * 0.08F);
        }

        return null; // Don't care about returning anything
    }
}
