package com.domochevsky.quiverbow.weapons.base.ammosource;

import com.domochevsky.quiverbow.config.WeaponProperties;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumSkyBlock;
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
        //Don't process on client because sky light calculations are stupid
        if (stack.getItemDamage() <= 0 || world.isRemote) return;

        int light = world.getLightFor(EnumSkyBlock.SKY, user.getPosition()) - world.getSkylightSubtracted();
        if (light >= properties.getInt("minLight"))
            stack.setItemDamage(stack.getItemDamage() - 1);
    }
}
