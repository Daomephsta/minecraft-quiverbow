package com.domochevsky.quiverbow.weapons.base.trigger;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.domochevsky.quiverbow.weapons.base.ammosource.AmmoSource;
import com.domochevsky.quiverbow.weapons.base.fireshape.FireShape;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PunchTrigger extends Trigger
{
    public PunchTrigger(AmmoSource ammoSource, FireShape shape)
    {
        super(ammoSource, shape);
    }

    @Override
    public boolean attackPressed(World world, EntityLivingBase user,
        ItemStack stack, WeaponProperties properties)
    {
        if (Weapon.getCooldown(stack) > 0)
            return false;
        if (!ammoSource.hasAmmo(user, stack, properties))
            return false;
        if (!shape.fire(world, user, stack, properties))
            return false;
        ammoSource.consumeAmmo(user, stack, properties);
        Weapon.setCooldown(stack, properties.getMaxCooldown());
        return true;
    }
}
