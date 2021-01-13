package com.domochevsky.quiverbow.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ParticleMessage implements IMessage
{
    int entityID;
    EnumParticleTypes particleType;
    byte strength;

    // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
    public ParticleMessage() {}

    // Sending a message to the client to display particles at a specific entity's position
    public ParticleMessage(Entity entity, EnumParticleTypes type, byte strength)
    {
        this.entityID = entity.getEntityId();
        this.particleType = type;
        this.strength = strength;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        // the order is important
        this.entityID = buf.readInt();
        this.particleType = EnumParticleTypes.values()[buf.readInt()];
        this.strength = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityID);
        buf.writeInt(particleType.ordinal());
        buf.writeByte(strength);
    }
}
