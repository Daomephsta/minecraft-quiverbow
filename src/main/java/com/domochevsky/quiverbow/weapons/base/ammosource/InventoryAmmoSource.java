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
            if (ammo.test(invStack))
            {
                invStack.setCount(0);
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
}
