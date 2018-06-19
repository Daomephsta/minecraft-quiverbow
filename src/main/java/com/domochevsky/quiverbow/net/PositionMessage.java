package com.domochevsky.quiverbow.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PositionMessage implements IMessage
{
	public PositionMessage()
	{} // this constructor is required otherwise you'll get errors (used
		// somewhere in fml through reflection)

	Entity entity;
	double posX;
	double posY;
	double posZ;

	public PositionMessage(Entity entity, double x, double y, double z)
	{
		this.entity = entity;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		// the order is important
		this.entity = Minecraft.getMinecraft().world.getEntityByID(buf.readInt());
		this.posX = buf.readDouble();
		this.posY = buf.readDouble();
		this.posZ = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(entity.getEntityId());
		buf.writeDouble(posX);
		buf.writeDouble(posY);
		buf.writeDouble(posZ);
	}
}
