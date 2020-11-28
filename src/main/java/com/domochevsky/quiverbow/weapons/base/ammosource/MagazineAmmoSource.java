package com.domochevsky.quiverbow.weapons.base.ammosource;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.domochevsky.quiverbow.weapons.base.Weapon.Effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MagazineAmmoSource implements AmmoSource
{
    private final ItemStack magazine;
    private final int consumption;
    private Effect[] unloadEffects;

    public MagazineAmmoSource(Item magazine)
    {
        this(magazine, 1);
    }

    public MagazineAmmoSource(Item magazine, int consumption)
    {
        this.magazine = new ItemStack(magazine);
        this.consumption = consumption;
    }

    @Override
    public boolean hasAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode)
            return true;
        return stack.getItemDamage() < stack.getMaxDamage();
    }

    @Override
    public boolean consumeAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter.isSneaking())
        {
            dropMagazine(shooter.getEntityWorld(), stack, shooter, properties);
            return false;
        }
        if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode)
            return true;
        if (!hasAmmo(shooter, stack, properties))
            return false;
        stack.setItemDamage(stack.getItemDamage() + consumption);
        return true;
    }

    public void dropMagazine(World world, ItemStack stack,
        EntityLivingBase entity, WeaponProperties properties)
    {
        if (!world.isRemote)
        {
            ItemStack toDrop = magazine.copy();
            toDrop.setItemDamage(stack.getItemDamage());
            entity.entityDropItem(toDrop, 0.5F);
        }
        stack.setItemDamage(stack.getMaxDamage()); // Empty weapon
        if (unloadEffects != null)
        {
            for (Effect effect : unloadEffects)
                effect.apply(world, entity, stack, properties);
        }
    }

    @Override
    public void adjustItemProperties(Weapon weapon)
    {
        weapon.setMaxDamage(magazine.getMaxDamage());
    }

    public MagazineAmmoSource unloadEffects(Effect... unloadEffects)
    {
        this.unloadEffects = unloadEffects;
        return this;
    }
}
