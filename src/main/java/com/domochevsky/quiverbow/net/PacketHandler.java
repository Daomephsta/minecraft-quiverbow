package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.QuiverbowMain;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
    public static SimpleNetworkWrapper net = NetworkRegistry.INSTANCE.newSimpleChannel(QuiverbowMain.MODID.toUpperCase());

    private static int nextPacketId = 0;
    public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> packet, Class<REQ> message, Side side)
    {
        net.registerMessage(packet, message, nextPacketId++, side);
    }
}
