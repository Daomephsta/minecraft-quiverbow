package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.util.InventoryHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SeedJar extends AmmoMagazine
{
    public SeedJar()	// Holds seeds for the Seed Sweeper (512, for 8 per shot with 64 shots total), loaded directly into the weapon
    {
	super(8, 8);
	this.setMaxDamage(512);		// Filled with gold nuggets (8 shots with 9 scatter, 24 with 3 scatter)
	this.setCreativeTab(CreativeTabs.tabCombat);	// On the combat tab by default, since this is amunition
    }


    @Override
    String getIconPath() { return "SeedJar"; }


    @Override
    public void addRecipes() 
    {
	GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "gwg", "g g", "gig",
		'g', Blocks.glass_pane, 
		'i', Items.iron_ingot,
		'w', Blocks.wooden_button
		);
    }

    @Override
    protected boolean hasComponentItems(EntityPlayer player, int amount)
    {
	return InventoryHelper.hasItem(player, Items.wheat_seeds, amount) || InventoryHelper.hasItem(player, Items.melon_seeds, amount) || InventoryHelper.hasItem(player, Items.pumpkin_seeds, amount);
    }
    
    @Override
    protected boolean consumeComponentItems(EntityPlayer player, int amount)
    {
	player.getEntityWorld().playSoundAtEntity(player, "random.wood_click", 0.6F, 0.7F);
	return InventoryHelper.consumeItem(player, Items.wheat_seeds, amount) || InventoryHelper.consumeItem(player, Items.melon_seeds, amount) || InventoryHelper.consumeItem(player, Items.pumpkin_seeds, amount);
    }
}
