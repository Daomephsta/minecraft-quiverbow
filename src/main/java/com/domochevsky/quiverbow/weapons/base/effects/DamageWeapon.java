package com.domochevsky.quiverbow.weapons.base.effects;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon.Effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DamageWeapon implements Effect
{
    @Override
    public void apply(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        if (user instanceof EntityPlayer && ((EntityPlayer) user).capabilities.isCreativeMode)
            return;
        stack.setItemDamage(stack.getItemDamage() + 1);
    }
}