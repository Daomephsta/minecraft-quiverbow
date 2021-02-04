package com.domochevsky.quiverbow.weapons.base.trigger;

import javax.annotation.Nullable;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.domochevsky.quiverbow.weapons.base.ammosource.AmmoSource;
import com.domochevsky.quiverbow.weapons.base.fireshape.FireShape;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class DrawnTrigger extends Trigger
{
    public DrawnTrigger(AmmoSource ammoSource, FireShape shape)
    {
        super(ammoSource, shape);
    }

    @Override
    public ActionResult<ItemStack> usePressed(World world, EntityLivingBase user,
        ItemStack stack, EnumHand hand, WeaponProperties properties)
    {
        if (ammoSource.alternateUse(user, stack, properties))
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        boolean hasAmmo = ammoSource.hasAmmo(user, stack, properties);
        if (user instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) user;
            ActionResult<ItemStack> nockResult = ForgeEventFactory.onArrowNock(
                stack, world, player, hand, hasAmmo);
            if (nockResult != null)
                return nockResult;
        }
        if (hasAmmo)
        {
            user.setActiveHand(hand);
            return ActionResult.newResult(EnumActionResult.PASS, stack);
        }
        else
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
    }

    @Override
    public boolean useReleased(World world, EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        int charge = stack.getMaxItemUseDuration() - user.getItemInUseCount();
        boolean hasAmmo = ammoSource.hasAmmo(user, stack, properties);
        if (user instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) user;
            charge = ForgeEventFactory.onArrowLoose(stack, world, player, charge, hasAmmo);
            if (charge < 0)
                return false;
        }
        if (!hasAmmo)
            return false;
        float seconds = charge / 20.0F;
        float velocity = Math.min(seconds * (seconds + 2.0F) / 3.0F, 1.0F);
        if (velocity >= 0.1F)
        {
            ammoSource.consumeAmmo(user, stack, properties);
            return shape.fire(world, user, stack, properties);
        }
        return false;
    }

    @Override
    public void adjustItemProperties(Weapon weapon)
    {
        weapon.setUseParameters(EnumAction.BOW, 72000);
        // Copied from ItemBow L29-L44 and modified
        weapon.addPropertyOverride(new ResourceLocation("pull"),
        (ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) ->
        {
            if(entity == null) return 0.0F;
            else
            {
                if(entity.getActiveItemStack().getItem() == weapon)
                    return stack.getMaxItemUseDuration() - entity.getItemInUseCount() / 20.0F;
                else return 0.0F;
            }
        });
        weapon.addPropertyOverride(new ResourceLocation("pulling"),
        (ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) ->
        {
            return entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack
                    ? 1.0F
                    : 0.0F;
        });
        super.adjustItemProperties(weapon);
    }
}
