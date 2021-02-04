package com.domochevsky.quiverbow.weapons.base.trigger;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.domochevsky.quiverbow.weapons.base.ammosource.AmmoSource;
import com.domochevsky.quiverbow.weapons.base.fireshape.FireShape;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class SpoolingTrigger extends Trigger
{
    private static final int SPINDOWN_IMMUNITY = 20;
    private static final String MOMENTUM = "momentum";
    private static final int SPINUP_TIME = 30;

    public SpoolingTrigger(AmmoSource ammoSource, FireShape shape)
    {
        super(ammoSource, shape);
    }

    @Override
    public ActionResult<ItemStack> usePressed(World world, EntityLivingBase user, ItemStack stack, EnumHand hand, WeaponProperties properties)
    {
        ammoSource.alternateUse(user, stack, properties);
        user.setActiveHand(hand);
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean useReleased(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        int spin = stack.getMaxItemUseDuration() - user.getItemInUseCount();
        getTag(stack).setInteger(MOMENTUM, Math.min(spin, SPINUP_TIME) + SPINDOWN_IMMUNITY);
        return false;
    }

    @Override
    public boolean useTick(World world, ItemStack stack, EntityLivingBase user, WeaponProperties properties, int count)
    {
        int spin = stack.getMaxItemUseDuration() - count + getTag(stack).getInteger(MOMENTUM);
        spinFX(world, stack, user, spin);
        if (spin <= SPINUP_TIME)
            return false;
        else
        {
            if (!ammoSource.consumeAmmo(user, stack, properties))
                return false;
            return shape.fire(world, user, stack, properties);
        }
    }

    @Override
    public boolean weaponTick(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        super.weaponTick(world, user, stack, properties);
        // Momentum is lost while not firing
        if (user.getActiveItemStack() != stack)
        {
            int momentum = getTag(stack).getInteger(MOMENTUM);
            if (momentum > 0)
            {
                getTag(stack).setInteger(MOMENTUM, momentum - 1);
                spinFX(world, stack, user, momentum - 1);
            }
        }
        return false;
    }

    private void spinFX(World world, ItemStack stack, EntityLivingBase user, int spin)
    {
        switch (spin)
        {
        case 1:
        case 5:
        case 9:
        case 13:
        case 16:
        case 19:
        case 21:
        case 23:
        case 25:
            Helper.playSoundAtEntityPos(user, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.8F, 1.8F);
            break;
        default :
            if (spin >= 27)
                Helper.playSoundAtEntityPos(user, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.8F, 0.4F);
            break;
        }
    }

    private NBTTagCompound getTag(ItemStack stack)
    {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    @Override
    public void adjustItemProperties(Weapon weapon)
    {
        super.adjustItemProperties(weapon);
        weapon.setUseParameters(EnumAction.NONE, 72000);
    }
}
