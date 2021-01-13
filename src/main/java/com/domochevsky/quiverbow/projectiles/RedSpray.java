package com.domochevsky.quiverbow.projectiles;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class RedSpray extends ProjectilePotionEffect
{
    public static final Pair<String, String> BLINDNESS_DUR =
    Pair.of("blindnessDur", "The duration in ticks of the Blindness effect applied");

    public RedSpray(World world)
    {
        super(world);
    }

    public RedSpray(World world, Entity entity, WeaponProperties properties, float accHor, float accVert)
    {
        super(world, new PotionEffect(MobEffects.WITHER, properties.getInt(CommonProperties.WITHER_DUR),
            properties.getInt(CommonProperties.WITHER_STRENGTH)),
            new PotionEffect(MobEffects.BLINDNESS, properties.getInt(BLINDNESS_DUR), 1));
        this.damage = 0;
        this.doSetup(entity, properties.getProjectileSpeed(), accHor, accVert);
    }

    @Override
    public void doFlightSFX()
    {
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.REDSTONE, (byte) 4);
    }

    @Override
    public void onImpact(RayTraceResult movPos)
    {
        if (movPos.entityHit != null) // We hit a living thing!
        {
            super.onImpact(movPos);
        }
        // else, hit the terrain

        // SFX
        this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.7F, 1.5F);
        this.world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);

        this.setDead(); // We've hit something, so begone with the projectile
    }
}
