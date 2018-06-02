package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.miscitems.QuiverBowItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class AmmoBase extends QuiverBowItem
{
	public AmmoBase()
	{
		this.setMaxStackSize(16);
		this.setCreativeTab(CreativeTabs.COMBAT); // On the combat tab by
		// default, since this is
		// amunition
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return false;
	} // Don't care about durabilities}
}
