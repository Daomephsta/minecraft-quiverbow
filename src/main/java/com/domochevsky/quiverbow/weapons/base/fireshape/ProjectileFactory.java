package com.domochevsky.quiverbow.weapons.base.fireshape;

import com.domochevsky.quiverbow.config.WeaponProperties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public interface ProjectileFactory
{
    public Entity createProjectile(World world, EntityLivingBase shooter, WeaponProperties properties);
}
