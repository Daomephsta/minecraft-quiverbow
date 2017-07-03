package com.domochevsky.quiverbow.miscitems;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Part_GatlingBody extends QuiverBowItem
{
    public Part_GatlingBody()
    {
	this.setMaxStackSize(1);
	this.setCreativeTab(CreativeTabs.MATERIALS); // On the combat tab by
						     // default, but this isn't
						     // ammo. It's a building
						     // part
    }

    @Override
    public void addRecipes()
    {
	// Flavor as "over-indulgent" on pistons

	// Sugar Gatling, main body
	GameRegistry.addRecipe(new ItemStack(this), "rir", "ror", "tpb", 'o', Blocks.OBSIDIAN, 'i', Items.IRON_INGOT,
		't', Blocks.TRIPWIRE_HOOK, 'r', Items.REPEATER, 'p', Blocks.PLANKS, 'b', Blocks.PISTON);
    }
}
