package com.domochevsky.quiverbow.miscitems;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Part_GatlingBarrel extends QuiverBowItem
{
    public Part_GatlingBarrel()
    {
	this.setMaxStackSize(1);
	this.setCreativeTab(CreativeTabs.MATERIALS); // On the combat tab by
						     // default, but this isn't
						     // ammo. It's a building
						     // part
    }

    @Override
    public void addRecipes()
    {
	// Sugar Gatling, barrel
	// Piston accelerators? Sticky, regular + iron walls
	GameRegistry.addRecipe(new ItemStack(this), "i i", "ipi", "isi", 'i', Items.IRON_INGOT, 'p', Blocks.PISTON, 's',
		Blocks.STICKY_PISTON);
    }

    @Override
    public String getIconPath()
    {
	return "misc/Part_SEBarrel";
    }
}
