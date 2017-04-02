package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.util.InventoryHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class NeedleMagazine extends AmmoMagazine
{
    public NeedleMagazine()
    {
	super(1, 8);
	this.setMaxDamage(64);		// Filled with cactus thorns
	this.setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override
    public String getIconPath()
    {
	return "NeedleAmmo";
    }


    @Override
    public void addRecipes() 
    {
	GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xix",
		'x', Items.leather, 
		'i', Items.iron_ingot
		);
    }

    @Override
    protected boolean hasComponentItems(EntityPlayer player, int amount)
    {
	return InventoryHelper.hasBlock(player, Blocks.cactus, amount);
    }
    
    @Override
    protected boolean consumeComponentItems(EntityPlayer player, int amount)
    {
	player.getEntityWorld().playSoundAtEntity(player, "random.wood_click", 0.5F, 1.3F);
        return InventoryHelper.consumeBlock(player, Blocks.cactus, amount);
    }
}
