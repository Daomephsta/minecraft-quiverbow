package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.util.InventoryHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class LargeNetherrackMagazine extends AmmoMagazine
{
    public LargeNetherrackMagazine()
    {
	super(1, 8);
	this.setMaxDamage(200);		// Filled with gold nuggets (8 shots with 9 scatter, 24 with 3 scatter)
	this.setCreativeTab(CreativeTabs.tabCombat);	// On the combat tab by default, since this is amunition
    }	

    @Override
    String getIconPath()
    {
	return "LargeNetherAmmo";
    }


    @Override
    public void addRecipes() 
    {
	GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xgx",
		'x', Blocks.nether_brick, 
		'g', Items.iron_ingot
		);
    }

    @Override
    protected boolean hasComponentItems(EntityPlayer player, int amount)
    {
	return InventoryHelper.hasBlock(player, Blocks.netherrack, amount);
    }

    @Override
    protected boolean consumeComponentItems(EntityPlayer player, int amount)
    {
	player.getEntityWorld().playSoundAtEntity(player, "random.wood_click", 0.5F, 0.3F);
	return InventoryHelper.consumeBlock(player, Blocks.netherrack, amount);
    }
}
