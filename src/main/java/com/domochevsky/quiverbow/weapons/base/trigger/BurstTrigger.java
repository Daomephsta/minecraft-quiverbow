package com.domochevsky.quiverbow.weapons.base.trigger;

import org.apache.commons.lang3.tuple.Pair;

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
    public static final Pair<String, String>
        BURST_SIZE = Pair.of("burstSize", "How many times the weapon fires per trigger pull");
    private static final String BURST_FIRE = "burstFire";

    public BurstTrigger(AmmoSource ammoSource, FireShape shape)
    {
        super(ammoSource, shape);
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
        stack.getTagCompound().setInteger(BURST_FIRE, properties.getInt(BURST_SIZE) - 1);
        user.setActiveHand(hand);
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean weaponTick(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        super.weaponTick(world, user, stack, properties);
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
