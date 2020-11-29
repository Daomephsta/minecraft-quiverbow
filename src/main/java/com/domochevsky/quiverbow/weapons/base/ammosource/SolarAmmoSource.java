package com.domochevsky.quiverbow.weapons.base.ammosource;

import com.domochevsky.quiverbow.config.WeaponProperties;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SolarAmmoSource extends SimpleAmmoSource
{
    public SolarAmmoSource(int max)
    {
        super(max);
    }

    @Override
    public void weaponTick(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        if (stack.getItemDamage() > 0 && world.getLight(user.getPosition()) >= properties.getInt("minLight"))
            stack.setItemDamage(stack.getItemDamage() - 1);
    }
}
