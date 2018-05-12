package com.domochevsky.quiverbow.ammo;

import java.util.List;

import com.domochevsky.quiverbow.miscitems.QuiverBowItem;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class AmmoBase extends QuiverBowItem
{
	public AmmoBase()
	{
		this.setMaxStackSize(16);
		this.setCreativeTab(CreativeTabs.COMBAT); // On the combat tab by
		// default, since this is
		// amunition
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean unknown)
	{
		list.add(I18n.format(getUnlocalizedName() + ".description"));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return false;
	} // Don't care about durabilities
}
