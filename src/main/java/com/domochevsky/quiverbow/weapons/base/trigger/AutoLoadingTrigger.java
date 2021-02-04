package com.domochevsky.quiverbow.weapons.base.trigger;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.domochevsky.quiverbow.weapons.base.ammosource.AmmoSource;
import com.domochevsky.quiverbow.weapons.base.fireshape.FireShape;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class AutoLoadingTrigger extends Trigger
{
    public AutoLoadingTrigger(AmmoSource ammoSource, FireShape shape)
    {
        super(ammoSource, shape);
    }

    @Override
    public ActionResult<ItemStack> usePressed(World world, EntityLivingBase shooter,
        ItemStack stack, EnumHand hand, WeaponProperties properties)
    {
        if (ammoSource.alternateUse(shooter, stack, properties))
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        if (Weapon.getCooldown(stack) > 0)
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        if (!isLoaded(stack))
        {
            if (shooter.isSneaking())
                setLoaded(stack, world, shooter, true);
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        }
        if (!ammoSource.consumeAmmo(shooter, stack, properties))
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        if (!shape.fire(world, shooter, stack, properties))
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        Weapon.setCooldown(stack, properties.getMaxCooldown());
        setLoaded(stack, world, shooter, false);
        shooter.setActiveHand(hand);
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    public static boolean isLoaded(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean("loaded");
    }

    private static void setLoaded(ItemStack stack, World world, Entity entity, boolean loaded)
    {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setBoolean("loaded", loaded);
        Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.8F, 0.5F);
    }
}
