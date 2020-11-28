package com.domochevsky.quiverbow.weapons.base.ammosource;

import com.domochevsky.quiverbow.config.WeaponProperties;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SolarAmmoSource extends InternalAmmoSource
{
    public SolarAmmoSource(int max)
    {
        super(max, 3);
    }

    @Override
    public boolean hasAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode)
            return true;
        return stack.getItemDamage() + 10 <= stack.getMaxDamage();
    }

    @Override
    public boolean consumeAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode)
            return true;
        if (!hasAmmo(shooter, stack, properties))
            return false;
        stack.setItemDamage(stack.getItemDamage() + 10);
        return true;
    }

    @Override
    public void weaponTick(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        if (stack.getItemDamage() > 0 && world.getLight(user.getPosition()) >= properties.getInt("minLight"))
            stack.setItemDamage(stack.getItemDamage() - 1);
    }
}
