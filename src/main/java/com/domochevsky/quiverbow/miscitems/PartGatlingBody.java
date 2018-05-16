package com.domochevsky.quiverbow.miscitems;

import net.minecraft.creativetab.CreativeTabs;

public class PartGatlingBody extends QuiverBowItem
{
	public PartGatlingBody()
	{
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.MATERIALS); // On the combat tab by
		// default, but this isn't
		// ammo. It's a building
		// part
	}
}
