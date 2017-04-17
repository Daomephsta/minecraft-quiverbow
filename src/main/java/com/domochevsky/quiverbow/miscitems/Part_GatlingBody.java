package com.domochevsky.quiverbow.miscitems;

import java.util.List;

import com.domochevsky.quiverbow.ammo._AmmoBase;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Part_GatlingBody extends QuiverBowItem
{	
	public Part_GatlingBody()
	{
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.tabMaterials);	// On the combat tab by default, but this isn't ammo. It's a building part
	}
	
	
	@Override
	public void addRecipes() 
	{ 
		// Flavor as "over-indulgent" on pistons
        
        // Sugar Gatling, main body
        GameRegistry.addRecipe(new ItemStack(this), "rir", "ror", "tpb",
                'o', Blocks.obsidian,
                'i', Items.iron_ingot,
                't', Blocks.tripwire_hook,
                'r', Items.repeater,
        		'p', Blocks.planks,
        		'b', Blocks.piston
    	);
	}
	
	@Override
	public String getIconPath()
	{
	    return "misc/Part_SEBody";
	}
}
