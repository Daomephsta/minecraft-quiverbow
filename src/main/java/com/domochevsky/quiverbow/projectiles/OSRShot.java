package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
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

public class OSRShot extends ProjectilePotionEffect
{
    public OSRShot(World world)
    {
        super(world);
    }

    public OSRShot(World world, Entity entity, WeaponProperties properties)
    {
        super(world, new PotionEffect(MobEffects.WITHER,
            properties.getInt(CommonProperties.WITHER_DUR),
            properties.getInt(CommonProperties.WITHER_STRENGTH)));
        this.doSetup(entity, properties.getProjectileSpeed());
        this.damage = properties.generateDamage(world.rand);
    }

    @Override
    public void doFlightSFX()
    {
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SMOKE_NORMAL,
                (byte) 1);
    }

    @Override
    public void onImpact(RayTraceResult movPos)
    {
        if (movPos.entityHit != null) // We hit a living thing!
        {
            super.onImpact(movPos);

            this.setDead(); // Hit an entity, so begone.
        }
        else // Hit the terrain
        {
            // Glass breaking
            if (Helper.tryBlockBreak(this.world, this, movPos.getBlockPos(), 2) && this.targetsHit < 2)
            {
                this.targetsHit += 1;
            }
            else
            {
                this.setDead();
            } // Punching through glass, 2 thick
        }

        // SFX
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SMOKE_NORMAL,
                (byte) 1);
        this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 0.5F);
    }
}
