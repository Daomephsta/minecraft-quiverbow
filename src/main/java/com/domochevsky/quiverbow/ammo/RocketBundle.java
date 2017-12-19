package com.domochevsky.quiverbow.ammo;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RocketBundle extends _AmmoBase
{
	@Override
	public void addRecipes()
	{
		// A bundle of rockets (8)
		GameRegistry.addRecipe(new ItemStack(this), "xxx", "xyx", "xxx", 'x', Items.FIREWORKS, 'y', Items.STRING);

		// Bundle of rockets back to 8 rockets
		GameRegistry.addShapelessRecipe(new ItemStack(Items.FIREWORKS, 8), new ItemStack(this));
	}
}
