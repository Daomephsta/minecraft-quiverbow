package com.domochevsky.quiverbow.weapons.base.ammosource;

import static java.lang.Math.max;
import static java.lang.Math.min;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.util.NBTags;
import com.domochevsky.quiverbow.weapons.base.Weapon;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface AmmoSource
{
    public boolean hasAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties);

    public boolean consumeAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties);

    public default void adjustItemProperties(Weapon weapon) {}

    public default void weaponTick(World world, EntityLivingBase user,
        ItemStack stack, WeaponProperties properties) {}

    /** Allows ammo sources to take over the weapon use action
     * @param shooter the entity using the weapon
     * @param stack the itemstack of the weapon
     * @param properties properties of the weapon
     * @return if an alternate action was performed
     */
    public default boolean alternateUse(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        return false;
    }

    public default int getAmmo(ItemStack stack)
    {
        return NBTags.getOrCreate(stack).getInteger("ammo");
    }

    public default void addAmmo(ItemStack stack, int increment)
    {
        NBTags.getOrCreate(stack).setInteger("ammo",
            min(getAmmo(stack) + increment, getAmmoCapacity(stack)));
    }

    public default void removeAmmo(ItemStack stack, int increment)
    {

        NBTags.getOrCreate(stack).setInteger("ammo", max(0, getAmmo(stack) - increment));
    }

    public int getAmmoCapacity(ItemStack stack);
}
