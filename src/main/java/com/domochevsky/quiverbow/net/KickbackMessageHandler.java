package com.domochevsky.quiverbow.net;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class KickbackMessageHandler implements ISidedMessageHandler<KickbackMessage, IMessage>
{
    @Override
    public void processMessage(KickbackMessage message, MessageContext ctx)
    {
        int strength = message.strength;
        Entity user = Minecraft.getMinecraft().player;
        user.motionZ += -MathHelper.cos(user.rotationYaw * (float) Math.PI / 180.0F) * (strength * 0.08F);
        user.motionX += MathHelper.sin(user.rotationYaw * (float) Math.PI / 180.0F) * (strength * 0.08F);
    }

    @Override
    public Side getSide()
    {
        return Side.CLIENT;
    }
}
