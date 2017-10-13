package com.domochevsky.quiverbow.ammo;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ArrowBundle extends _AmmoBase
{
    @Override
    public void addRecipes()
    {
	// One arrow bundle, holding 8 arrows
	GameRegistry.addRecipe(new ItemStack(this), "xxx", "xyx", "xxx", 'x', Items.ARROW, 'y', Items.STRING);

	// Bundle of arrows back to 8 arrows
	GameRegistry.addShapelessRecipe(new ItemStack(Items.ARROW, 8), new ItemStack(this));
    }
}
