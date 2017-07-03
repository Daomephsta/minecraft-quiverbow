package com.domochevsky.quiverbow.ammo;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ColdIronClip extends _AmmoBase
{
    @Override
    public void addRecipes()
    {
	// A bundle of ice-laced iron ingots (4), merged with a slime ball
	GameRegistry.addShapelessRecipe(new ItemStack(this), Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT,
		Items.IRON_INGOT, Blocks.ICE, Blocks.ICE, Blocks.ICE, Blocks.ICE, Items.SLIME_BALL);
    }
}
