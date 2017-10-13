package com.domochevsky.quiverbow.ammo;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BoxOfFlintDust extends _AmmoBase
{
    public BoxOfFlintDust()
    {
	this.setMaxDamage(16);
	this.setCreativeTab(CreativeTabs.TOOLS);

	this.setHasSubtypes(true);
    }

    @Override
    public void addRecipes()
    {
	// A box of flint dust (4 dust per flint, meaning 32 per box), merged
	// with wooden planks
	GameRegistry.addShapelessRecipe(new ItemStack(this), Items.FLINT, Items.FLINT, Items.FLINT, Items.FLINT,
		Items.FLINT, Items.FLINT, Items.FLINT, Items.FLINT, Blocks.PLANKS);
    }
}
