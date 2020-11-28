package com.domochevsky.quiverbow.weapons.base.trigger;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.domochevsky.quiverbow.weapons.base.ammosource.AmmoSource;
import com.domochevsky.quiverbow.weapons.base.fireshape.FireShape;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class Trigger
{
    protected final AmmoSource ammoSource;
    protected final FireShape shape;

    public Trigger(AmmoSource ammoSource, FireShape shape)
    {
        this.ammoSource = ammoSource;
        this.shape = shape;
    }

    public ActionResult<ItemStack> usePressed(World world, EntityLivingBase user,
        ItemStack stack, EnumHand hand, WeaponProperties properties)
    {
        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }

    public boolean useReleased(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        return false;
    }

    public boolean attackPressed(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        return false;
    }

    public void adjustItemProperties(Weapon weapon)
    {
        ammoSource.adjustItemProperties(weapon);
    }

    public boolean weaponTick(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        ammoSource.weaponTick(world, user, stack, properties);
        return false;
    }

    public boolean useTick(World entityWorld, ItemStack stack, EntityLivingBase player, WeaponProperties properties, int count)
    {
        return false;
    }
}
