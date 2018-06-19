package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.armsassistant.EntityAA;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TurretInventoryMessage implements IMessage
{
	// this constructor is required otherwise you'll get errors (used
	// somewhere in fml through reflection)
	public TurretInventoryMessage() {} 

	int turretID;
	ItemStack stack;
	int itemSlot;

	// Sending a message to the client to inform them about turret state changes
	public TurretInventoryMessage(EntityAA turret, ItemStack stack, int slot)
	{
		this.turretID = turret.getEntityId();
		this.stack = stack;
		this.itemSlot = slot;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		// the order is important
		this.turretID = buf.readInt();
		this.stack = ByteBufUtils.readItemStack(buf);
		this.itemSlot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(turretID);
		ByteBufUtils.writeItemStack(buf, stack);
		buf.writeInt(this.itemSlot);
	}
}
