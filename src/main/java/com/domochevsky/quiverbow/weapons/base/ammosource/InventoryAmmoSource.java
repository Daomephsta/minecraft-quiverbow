package com.domochevsky.quiverbow.weapons.base.ammosource;

import java.util.function.Predicate;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.google.common.collect.Iterables;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryAmmoSource implements AmmoSource
{
    private final Predicate<ItemStack> ammo;

    public InventoryAmmoSource(Item item)
    {
        this.ammo = stack -> stack.getItem() == item;
    }

    @Override
    public boolean hasAmmo(EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        if (user instanceof EntityPlayer && ((EntityPlayer) user).capabilities.isCreativeMode)
            return true;
        for (ItemStack invStack : inventoryIterable(user))
        {
            if (ammo.test(invStack))
                return true;
        }
        return false;
    }

    @Override
    public boolean consumeAmmo(EntityLivingBase user, ItemStack stack, WeaponProperties properties)
    {
        if (user instanceof EntityPlayer && ((EntityPlayer) user).capabilities.isCreativeMode)
            return true;
        for (ItemStack invStack : inventoryIterable(user))
        {
            if (ammo.test(invStack) && stack.getCount() >= 1)
            {
                invStack.shrink(1);
                return true;
            }
        }
        return false;
    }

    private Iterable<ItemStack> inventoryIterable(EntityLivingBase user)
    {
        Iterable<ItemStack> equipment = user.getHeldEquipment();
        if (user instanceof EntityPlayer)
            equipment = Iterables.concat(equipment, ((EntityPlayer) user).inventory.mainInventory);
        return equipment;
    }

    @Override
    public int getAmmo(ItemStack stack)
    {
        // Closest thing such a weapon has to ammo
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        // Closest thing such a weapon has to a capacity
        return stack.getMaxDamage();
    }

    @Override
    public void addAmmo(ItemStack stack, int increment) {/* NO OP*/}

    @Override
    public void removeAmmo(ItemStack stack, int increment) {/* NO OP*/}
}
