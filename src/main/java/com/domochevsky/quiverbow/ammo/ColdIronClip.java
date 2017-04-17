package com.domochevsky.quiverbow.ammo;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ColdIronClip extends _AmmoBase
{	
	@Override
	public String getIconPath() { return "Bundle_Frost"; }
	
	@Override
	public void addRecipes() 
	{ 
		// A bundle of ice-laced iron ingots (4), merged with a slime ball
        GameRegistry.addShapelessRecipe(new ItemStack(this),
                Items.iron_ingot,
                Items.iron_ingot,
                Items.iron_ingot,
                Items.iron_ingot,
                Blocks.ice,
                Blocks.ice,
                Blocks.ice,
                Blocks.ice,
                Items.slime_ball
        );
	}
}
