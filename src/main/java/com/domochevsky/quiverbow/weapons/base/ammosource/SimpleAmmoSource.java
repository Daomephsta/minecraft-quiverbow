package com.domochevsky.quiverbow.weapons.base.ammosource;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SimpleAmmoSource implements AmmoSource
{
    public static final Pair<String, String>
        AMMO_CONSUMPTION = Pair.of("ammoConsumption", "How much ammo is consumed per trigger pull"),
        AMMO_CAPACITY = Pair.of("ammoCapacity", "How much ammo this weapon can store");

    @Override
    public boolean hasAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode)
            return true;
        return getAmmo(stack) >= properties.getInt(AMMO_CONSUMPTION);
    }

    @Override
    public boolean consumeAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode)
            return true;
        if (!hasAmmo(shooter, stack, properties))
            return false;
        removeAmmo(stack, properties.getInt(AMMO_CONSUMPTION));
        return true;
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        return ((Weapon) stack.getItem()).getProperties().getInt(AMMO_CAPACITY);
    }

    @Override
    public void adjustItemProperties(Weapon weapon)
    {
        weapon.addPropertyOverride(new ResourceLocation(QuiverbowMain.MODID, "ammo"), (stack, world, entity) ->
        {
            AmmoSource ammoSource = weapon.getTrigger().getAmmoSource();
            return 1.0F - (float) ammoSource.getAmmo(stack) / (float) ammoSource.getAmmoCapacity(stack);
        });
    }
}
