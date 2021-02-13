package com.domochevsky.quiverbow.weapons.base.fireshape;

import java.util.ArrayList;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.util.Raytrace;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HitscanFireShape implements FireShape
{
    private final HitEffect hitEffect;
    private final int piercing;

    public HitscanFireShape(HitEffect hitEffect)
    {
        this(hitEffect, 0);
    }

    public HitscanFireShape(HitEffect hitEffect, int piercing)
    {
        this.hitEffect = hitEffect;
        this.piercing = piercing;
    }

    @Override
    public boolean fire(World world, EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        Vec3d eyeVec = shooter.getPositionVector().addVector(0.0D, shooter.getEyeHeight(), 0.0D);
        Vec3d endVec = eyeVec.add(shooter.getLookVec().scale(shooter.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue()));
        if (piercing > 0)
        {
            for (RayTraceResult result : Raytrace.all(new ArrayList<>(), world, shooter, eyeVec, endVec))
            {
                if (!processRay(world, shooter, properties, result))
                    return false;
            }
            return true;
        }
        else
            return processRay(world, shooter, properties, Raytrace.closest(world, shooter, eyeVec, endVec));
    }

    private boolean processRay(World world, EntityLivingBase shooter, WeaponProperties properties, RayTraceResult result)
    {
        if (result == null)
            return false;
        if (result.typeOfHit != RayTraceResult.Type.MISS)
        {
            hitEffect.apply(world, shooter, properties, result);
            return true;
        }
        return false;
    }

    public interface HitEffect
    {
        public void apply(World world, EntityLivingBase user, WeaponProperties properties, RayTraceResult hit);
    }
}
