package com.domochevsky.quiverbow.weapons.base.fireshape;

import com.domochevsky.quiverbow.config.WeaponProperties;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface FireShape
{
    public boolean fire(World world, EntityLivingBase shooter, ItemStack stack, WeaponProperties properties);
}
