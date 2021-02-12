package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.armsassistant.EntityArmsAssistant;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TurretInventoryMessageHandler implements ISidedMessageHandler<TurretInventoryMessage, IMessage>
{
    @Override
    public void processMessage(TurretInventoryMessage message, MessageContext ctx)
    {
        EntityArmsAssistant turret = (EntityArmsAssistant) Minecraft.getMinecraft().world.getEntityByID(message.turretID);
        IItemHandler inv = turret.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        inv.insertItem(message.itemSlot, message.stack, false);
    }

    @Override
    public Side getSide()
    {
        return Side.CLIENT;
    }
}
