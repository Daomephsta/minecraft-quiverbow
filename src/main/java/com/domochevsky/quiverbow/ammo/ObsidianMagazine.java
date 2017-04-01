package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.util.InventoryHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ObsidianMagazine extends AmmoMagazine
{
    public ObsidianMagazine()
    {
	super();
	this.setMaxDamage(16);
	this.setCreativeTab(CreativeTabs.tabCombat);	// On the combat tab by default, since this is amunition
    }


    @Override
    String getIconPath()
    {
	return "ObsidianAmmo";
    }


    @Override
    public void addRecipes() 
    {
	GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xox",
		'x', Items.iron_ingot, 
		'o', Blocks.obsidian
		);
    }

    @Override
    protected boolean hasComponentItems(EntityPlayer player, int amount)
    {
	return InventoryHelper.hasItem(player, Items.gunpowder, amount) && InventoryHelper.hasBlock(player, Blocks.obsidian, amount);
    }
    
    @Override
    protected boolean consumeComponentItems(EntityPlayer player, int amount)
    {
	player.getEntityWorld().playSoundAtEntity(player, "random.wood_click", 0.5F, 0.30F);
        return InventoryHelper.consumeItem(player, Items.gunpowder, amount) && InventoryHelper.consumeBlock(player, Blocks.obsidian, amount);
    }
}
