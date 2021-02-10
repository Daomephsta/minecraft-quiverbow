package com.domochevsky.quiverbow.projectiles;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EnderAccelerator extends ProjectileBase implements IEntityAdditionalSpawnData
{
    public static final Pair<String, String> SELF_EXPLOSION_SIZE = Pair.of("selfExplosionSize",
        "How large the explosion at the user location is in blocks. A TNT explosion is 4.0 blocks");
    private boolean damageTerrain;

    public EnderAccelerator(World world)
    {
        super(world);
    }

    public EnderAccelerator(World world, Entity entity, WeaponProperties properties)
    {
        super(world);
        this.doSetup(entity, properties.getProjectileSpeed());
        this.ticksInAirMax = 120;
        this.damageTerrain = properties.getBoolean(CommonProperties.DAMAGE_TERRAIN);
        this.explosionSize = properties.getFloat(CommonProperties.EXPLOSION_SIZE);
        this.ownerX = entity.posX;
        this.ownerY = entity.posY + entity.getEyeHeight();
        this.ownerZ = entity.posZ;
    }

    @Override
    public boolean doDropOff()
    {
        return false;
    } // Affected by gravity? Nope. Straight beam

    @Override
    public void doFlightSFX()
    {
        if (this.ticksExisted > this.ticksInAirMax)
        {
            if (!world.isRemote)
                this.world.createExplosion(this, this.posX, this.posY, this.posZ, 8.0f, damageTerrain);
            this.setDead();
        }

        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SPELL, (byte) 4);
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.ENCHANTMENT_TABLE,
                (byte) 4);
    }

    @Override
    public void onImpact(RayTraceResult target)
    {
        if (target.entityHit != null) // We hit a living thing!
        {
            target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getShooter()),
                    this.damage);
            target.entityHit.hurtResistantTime = 0; // No immunity frames
        }

        if (!world.isRemote)
            world.createExplosion(this, this.posX, this.posY, this.posZ, this.explosionSize, damageTerrain);

        // SFX
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SPELL, (byte) 8);

        this.setDead(); // No matter what, we're done here
    }

    // ERA self-detonation kills it otherwise
    @Override
    public boolean isImmuneToExplosions()
    {
        return true;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) // save extra data on the server
    {
        buffer.writeDouble(this.ownerX);
        buffer.writeDouble(this.ownerY);
        buffer.writeDouble(this.ownerZ);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) // read it on the client
    {
        this.ownerX = additionalData.readDouble();
        this.ownerY = additionalData.readDouble();
        this.ownerZ = additionalData.readDouble();
    }
}
