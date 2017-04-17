package com.domochevsky.quiverbow.miscitems;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Part_GatlingBarrel extends QuiverBowItem
{	
	public Part_GatlingBarrel()
	{
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.tabMaterials);	// On the combat tab by default, but this isn't ammo. It's a building part
	}
	
	@Override
	public void addRecipes() 
	{ 
        // Sugar Gatling, barrel
        // Piston accelerators? Sticky, regular + iron walls
        GameRegistry.addRecipe(new ItemStack(this), "i i", "ipi", "isi",
                'i', Items.iron_ingot,
                'p', Blocks.piston,
                's', Blocks.sticky_piston
    	);
	}
	
	@Override
	public String getIconPath()
	{
	    return "misc/Part_SEBarrel";
	}
}
