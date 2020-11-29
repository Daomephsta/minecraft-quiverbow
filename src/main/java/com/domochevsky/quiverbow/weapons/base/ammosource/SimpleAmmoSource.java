package com.domochevsky.quiverbow.weapons.base.ammosource;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class SimpleAmmoSource implements AmmoSource
{
    public static final Pair<String, String>
        AMMO_CONSUMPTION = Pair.of("ammoConsumption", "How much ammo is consumed per trigger pull");
    private final int max;

    public SimpleAmmoSource(int max)
    {
        this.max = max;
    }

    @Override
    public boolean hasAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode)
            return true;
        return stack.getItemDamage() + properties.getInt(AMMO_CONSUMPTION) <= stack.getMaxDamage();
    }

    @Override
    public boolean consumeAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode)
            return true;
        if (!hasAmmo(shooter, stack, properties))
            return false;
        stack.setItemDamage(stack.getItemDamage() + properties.getInt(AMMO_CONSUMPTION));
        return true;
    }

    @Override
    public void adjustItemProperties(Weapon weapon)
    {
        weapon.setMaxDamage(max);
    }
}
