package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.miscitems.QuiverBowItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class _AmmoBase extends QuiverBowItem
{	
	public _AmmoBase()
	{
		this.setMaxStackSize(16);
		this.setCreativeTab(CreativeTabs.tabCombat);	// On the combat tab by default, since this is amunition
	}
	
	
	@SideOnly(Side.CLIENT)
	protected IIcon Icon;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{ 
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/" + this.getIconPath());
	}

	@Override
	public IIcon getIconFromDamage(int meta) { return this.Icon; }
	
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) { return false; }	// Don't care about durabilities
}
