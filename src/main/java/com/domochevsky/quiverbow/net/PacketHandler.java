package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.QuiverbowMain;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
	public static SimpleNetworkWrapper net;

	public static void initPackets()
	{
		net = NetworkRegistry.INSTANCE.newSimpleChannel(QuiverbowMain.MODID.toUpperCase());

		registerMessage(ParticlePacket.class, ParticleMessage.class);
		registerMessage(PositionPacket.class, PositionMessage.class);
		registerMessage(KickbackPacket.class, KickbackMessage.class);
		registerMessage(TurretInventoryPacket.class, TurretInventoryMessage.class);
		registerMessage(TurretStatePacket.class, TurretStateMessage.class);
	}

	private static int nextPacketId = 0;

	private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> packet, Class<REQ> message)
	{
		net.registerMessage(packet, message, nextPacketId, Side.CLIENT); // Only
		// care
		// about
		// sending
		// things
		// to
		// the
		// client
		// net.registerMessage(packet, message, nextPacketId, Side.SERVER);
		nextPacketId++;
	}
}
