package com.domochevsky.quiverbow.weapons.base.effects;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon.Effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Knockback implements Effect
{
    @Override
    public void apply(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        Helper.knockUserBack(user, properties.getKickback());
    }
}