package com.domochevsky.quiverbow.net;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.domochevsky.quiverbow.HelperClient;
import com.domochevsky.quiverbow.armsassistant.*;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TurretStateMessageHandler implements ISidedMessageHandler<TurretStateMessage, IMessage>
{
	@Override
	public void processMessage(TurretStateMessage message, MessageContext ctx)
	{
		EntityArmsAssistant turret = (EntityArmsAssistant) Minecraft.getMinecraft().world.getEntityByID(message.turretID);
		List<IArmsAssistantUpgrade> upgrades = IntStream.of(message.upgrades).mapToObj(UpgradeRegistry::getUpgradeInstance).collect(Collectors.toList());
		HelperClient.setTurretState(turret, upgrades);
	}

	@Override
	public Side getSide()
	{
		return Side.CLIENT;
	}
}
