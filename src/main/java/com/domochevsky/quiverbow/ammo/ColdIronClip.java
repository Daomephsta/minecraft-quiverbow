package com.domochevsky.quiverbow.ammo;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ColdIronClip extends AmmoBase
{
	@Override
	public void addRecipes()
	{
		// A bundle of ice-laced iron ingots (4), merged with a slime ball
		GameRegistry.addShapelessRecipe(new ItemStack(this), Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT,
				Items.IRON_INGOT, Blocks.ICE, Blocks.ICE, Blocks.ICE, Blocks.ICE, Items.SLIME_BALL);
	}
}
