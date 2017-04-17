package com.domochevsky.quiverbow.ammo;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArrowBundle extends _AmmoBase
{	
	@Override
	public String getIconPath() { return "Bundle_Arrows"; }
	
	@Override
	public void addRecipes() 
	{ 
		// One arrow bundle, holding 8 arrows
		GameRegistry.addRecipe(new ItemStack(this), "xxx", "xyx", "xxx",
                'x', Items.arrow,
                'y', Items.string
        );
		
		// Bundle of arrows back to 8 arrows
        GameRegistry.addShapelessRecipe(new ItemStack(Items.arrow, 8), new ItemStack(this) );
	}
}
