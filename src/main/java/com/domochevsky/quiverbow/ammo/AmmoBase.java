package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.miscitems.QuiverBowItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class AmmoBase extends QuiverBowItem
{
    public AmmoBase()
    {
        this.setMaxStackSize(16);
        this.setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return false;
    }
}
