package com.domochevsky.quiverbow.weapons.base.ammosource;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InternalAmmoSource implements AmmoSource
{
    private final int max,
                      consumption;

    public InternalAmmoSource(int max)
    {
        this(max, 1);
    }

    public InternalAmmoSource(int max, int consumption)
    {
        this.max = max;
        this.consumption = consumption;
    }

    @Override
    public boolean hasAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode)
            return true;
        return stack.getItemDamage() < stack.getMaxDamage();
    }

    @Override
    public boolean consumeAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode)
            return true;
        if (!hasAmmo(shooter, stack, properties))
            return false;
        stack.setItemDamage(stack.getItemDamage() + consumption);
        return true;
    }

    @Override
    public void adjustItemProperties(Weapon weapon)
    {
        weapon.setMaxDamage(max);
    }
}
