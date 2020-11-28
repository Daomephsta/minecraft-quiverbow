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

public class AutomaticTrigger extends Trigger
{
    public AutomaticTrigger(AmmoSource ammoSource, FireShape shape)
    {
        super(ammoSource, shape);
    }

    @Override
    public ActionResult<ItemStack> usePressed(World world, EntityLivingBase shooter,
        ItemStack stack, EnumHand hand, WeaponProperties properties)
    {
        if (Weapon.getCooldown(stack) > 0)
            return ActionResult.newResult(EnumActionResult.PASS, stack);
        if (!ammoSource.consumeAmmo(shooter, stack, properties))
            return ActionResult.newResult(EnumActionResult.PASS, stack);
        if (!shape.fire(world, shooter, stack, properties))
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        Weapon.setCooldown(stack, properties.getMaxCooldown());
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }
}
