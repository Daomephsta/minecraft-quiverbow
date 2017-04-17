package com.domochevsky.quiverbow.ammo;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class LargeRocket extends _AmmoBase
{	
	@Override
	public String getIconPath() { return "Bundle_BigRocket"; }	
	
	@Override
	public void addRecipes() 
	{ 
		// A big rocket
    	GameRegistry.addRecipe(new ItemStack(this), "zaa", "aya", "aab",
                'y', Blocks.tnt,
        		'z', Blocks.planks,
        		'a', Items.paper,
        		'b', Items.string
    	);
	}
}
