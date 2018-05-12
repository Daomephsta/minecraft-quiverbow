package com.domochevsky.quiverbow.ammo;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class LargeRocket extends AmmoBase
{
	@Override
	public void addRecipes()
	{
		// A big rocket
		GameRegistry.addRecipe(new ItemStack(this), "zaa", "aya", "aab", 'y', Blocks.TNT, 'z', Blocks.PLANKS, 'a',
				Items.PAPER, 'b', Items.STRING);
	}
}
