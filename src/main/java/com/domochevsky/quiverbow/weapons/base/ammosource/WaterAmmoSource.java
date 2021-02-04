package com.domochevsky.quiverbow.weapons.base.ammosource;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class WaterAmmoSource implements AmmoSource
{
    @Override
    public boolean hasAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode)
            return true;
        return stack.getItemDamage() < stack.getMaxDamage();
    }

    @Override
    public boolean consumeAmmo(EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        if (!hasAmmo(user, stack, properties))
            return false;
        stack.setItemDamage(stack.getItemDamage() + 1);
        return true;
    }

    @Override
    public boolean alternateUse(EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        if (user instanceof EntityPlayer && stack.getItemDamage() == stack.getMaxDamage())
        {
            tryReload(user.getEntityWorld(), (EntityPlayer) user, stack);
            return true;
        }
        return false;
    }

    private void tryReload(World world, EntityPlayer user, ItemStack stack)
    {
        Vec3d eyeVec = user.getPositionVector().addVector(0.0D, user.getEyeHeight(), 0.0D);
        Vec3d rayEndVec = eyeVec.add(user.getLookVec().scale(8.0D));
        RayTraceResult target = world.rayTraceBlocks(eyeVec, rayEndVec, true);
        if (MinecraftForge.EVENT_BUS.post(new FillBucketEvent(user, stack, world, target)) || target == null)
            return;

        if (target.typeOfHit == Type.BLOCK)
        {
            IBlockState state = world.getBlockState(target.getBlockPos());
            if (state.getMaterial() == Material.WATER && state.getValue(BlockLiquid.LEVEL) == 0 &&
                user.canPlayerEdit(target.getBlockPos(), target.sideHit, stack) &&
                world.isBlockModifiable(user, target.getBlockPos()))
            {
                world.setBlockToAir(target.getBlockPos());
                Helper.playSoundAtEntityPos(user, SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                stack.setItemDamage(0);
            }
        }
    }

    @Override
    public void adjustItemProperties(Weapon weapon)
    {
        weapon.setMaxDamage(1);
    }
}
