package com.domochevsky.quiverbow.weapons.base.trigger;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.domochevsky.quiverbow.weapons.base.ammosource.AmmoSource;
import com.domochevsky.quiverbow.weapons.base.fireshape.FireShape;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BurstTrigger extends Trigger
{
    private static final String BURST_FIRE = "burstFire";
    private final int burstSize;

    public BurstTrigger(int burstSize, AmmoSource ammoSource, FireShape shape)
    {
        super(ammoSource, shape);
        this.burstSize = burstSize;
    }

    @Override
    public ActionResult<ItemStack> usePressed(World world, EntityLivingBase user, ItemStack stack, EnumHand hand, WeaponProperties properties)
    {
        if (Weapon.getCooldown(stack) > 0)
            return ActionResult.newResult(EnumActionResult.PASS, stack);
        if (!ammoSource.consumeAmmo(user, stack, properties))
            return ActionResult.newResult(EnumActionResult.PASS, stack);
        if (!shape.fire(world, user, stack, properties))
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(BURST_FIRE, burstSize - 1);
        user.setActiveHand(hand);
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean weaponTick(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        if (!stack.hasTagCompound())
            return false;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag.getInteger(BURST_FIRE) == 0 ||
            !ammoSource.consumeAmmo(user, stack, properties) ||
            !shape.fire(world, user, stack, properties))
        {
            return false;
        }
        tag.setInteger(BURST_FIRE, tag.getInteger(BURST_FIRE) - 1);
        if (tag.getInteger(BURST_FIRE) == 0)
            Weapon.setCooldown(stack, properties.getMaxCooldown());
        return true;
    }

    @Override
    public void adjustItemProperties(Weapon weapon)
    {
        super.adjustItemProperties(weapon);
        weapon.setUseParameters(EnumAction.NONE, 72000);
    }
}
