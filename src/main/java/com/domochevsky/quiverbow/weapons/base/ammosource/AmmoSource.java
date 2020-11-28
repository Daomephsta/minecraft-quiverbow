package com.domochevsky.quiverbow.weapons.base.ammosource;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface AmmoSource
{
    public boolean hasAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties);

    public boolean consumeAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties);

    public default void adjustItemProperties(Weapon weapon) {}

    public default void weaponTick(World world, EntityLivingBase user,
        ItemStack stack, WeaponProperties properties) {}
}
