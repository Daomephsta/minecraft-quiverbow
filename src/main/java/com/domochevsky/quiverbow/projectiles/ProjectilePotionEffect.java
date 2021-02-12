package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class ProjectilePotionEffect extends ProjectileBase
{
    private PotionEffect[] effects;

    public ProjectilePotionEffect(World world, PotionEffect... effects)
    {
        super(world);
        this.effects = effects;
    }

    @Override
    public void onImpact(RayTraceResult hitPos)
    {
        hitPos.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getShooter()),
                this.damage);
        hitPos.entityHit.hurtResistantTime = 0; // No immunity frames
        if (hitPos.entityHit instanceof EntityLivingBase) // We hit a LIVING living thing!
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase) hitPos.entityHit;
            for (PotionEffect effect : effects)
                Helper.applyPotionEffect(entitylivingbase, effect);
        }
    }
}
