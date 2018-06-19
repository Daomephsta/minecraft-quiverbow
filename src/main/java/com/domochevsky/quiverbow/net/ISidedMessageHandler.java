package com.domochevsky.quiverbow.net;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;

/**Encourages the correct way of handling packets in 1.8+**/
public interface ISidedMessageHandler<REQ extends IMessage, REPLY extends IMessage> extends IMessageHandler<REQ, REPLY>
{
	public Side getSide();
	
	public void processMessage(REQ message, MessageContext ctx);
	
	@Override @Deprecated
	default REPLY onMessage(REQ message, MessageContext ctx)
	{
		if(getSide() == Side.CLIENT) Minecraft.getMinecraft().addScheduledTask(() -> this.processMessage(message, ctx));
		return null;
	}
}
