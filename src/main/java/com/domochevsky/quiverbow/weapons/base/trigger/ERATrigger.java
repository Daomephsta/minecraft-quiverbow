package com.domochevsky.quiverbow.weapons.base.trigger;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.domochevsky.quiverbow.weapons.base.ammosource.AmmoSource;
import com.domochevsky.quiverbow.weapons.base.fireshape.FireShape;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ERATrigger extends Trigger
{
    private static final String COUNTDOWN = "countdown";
    private static final String FIRING = "firing";
    private static final int MAX_COUNTDOWN = 54;
    private static final float PITCH_VOLUME_COEFFICIENT = 0.02F;

    public ERATrigger(AmmoSource ammoSource, FireShape shape)
    {
        super(ammoSource, shape);
    }

    @Override
    public ActionResult<ItemStack> usePressed(World world, EntityLivingBase user, ItemStack stack, EnumHand hand, WeaponProperties properties)
    {
        if (Weapon.getCooldown(stack) > 0)
            return ActionResult.newResult(EnumActionResult.PASS, stack);
        if (!ammoSource.hasAmmo(user, stack, properties))
            return ActionResult.newResult(EnumActionResult.PASS, stack);
        getTag(stack).setInteger(COUNTDOWN, MAX_COUNTDOWN);
        getTag(stack).setBoolean(FIRING, true);
        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }

    @Override
    public boolean weaponTick(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        super.weaponTick(world, user, stack, properties);
        if (!getTag(stack).getBoolean(FIRING))
            return false;
        int fireCountdown = getTag(stack).getInteger(COUNTDOWN);
        float vol_pitch = (MAX_COUNTDOWN - fireCountdown + 1) * PITCH_VOLUME_COEFFICIENT;
        Helper.playSoundAtEntityPos(user, SoundEvents.ENTITY_ENDERMEN_TELEPORT, vol_pitch, vol_pitch);
        if (fireCountdown > 0)
            getTag(stack).setInteger(COUNTDOWN, fireCountdown - 1);
        else
        {
            getTag(stack).setBoolean(FIRING, false);
            ammoSource.consumeAmmo(user, stack, properties);
            return shape.fire(world, user, stack, properties);
        }
        return false;
    }

    private NBTTagCompound getTag(ItemStack stack)
    {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }
}
