package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.armsassistant.EntityAA;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TurretInventoryMessage implements IMessage
{
	// this constructor is required otherwise you'll get errors (used
	// somewhere in fml through reflection)
	public TurretInventoryMessage() {} 

	EntityAA turret;
	ItemStack stack;
	int itemSlot;

	// Sending a message to the client to inform them about turret state changes
	public TurretInventoryMessage(EntityAA turret, ItemStack stack, int slot)
	{
		this.turret = turret;
		this.stack = stack;
		this.itemSlot = slot;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		// the order is important
		this.turret = (EntityAA) Minecraft.getMinecraft().world.getEntityByID(buf.readInt());
		this.stack = ByteBufUtils.readItemStack(buf);
		this.itemSlot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(turret.getEntityId());
		ByteBufUtils.writeItemStack(buf, stack);
		buf.writeInt(this.itemSlot);
	}
}
