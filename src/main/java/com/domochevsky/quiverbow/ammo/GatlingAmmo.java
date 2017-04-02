package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.util.InventoryHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GatlingAmmo extends AmmoMagazine
{
    public GatlingAmmo()
    {
	super(4, 4);
	this.setMaxDamage(200);
	this.setCreativeTab(CreativeTabs.tabCombat);	// On the combat tab by default, since this is amunition
    }


    @Override
    public String getIconPath() { return "GatlingAmmo"; }


    @Override
    public void addRecipes() 
    {
	// First, the clip itself (empty)
	GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "y y", "y y", "yxy",
		'x', Items.iron_ingot, 
		'y', Blocks.planks
		);
    }

    @Override
    protected boolean hasComponentItems(EntityPlayer player, int amount)
    {
	return InventoryHelper.hasItem(player, Items.reeds, amount) && InventoryHelper.hasItem(player, Items.stick, amount);
    }

    @Override
    protected boolean consumeComponentItems(EntityPlayer player, int amount)
    {
	player.getEntityWorld().playSoundAtEntity(player, "random.wood_click", 0.5F, 1.50F);
	return InventoryHelper.consumeItem(player, Items.reeds, amount) && InventoryHelper.consumeItem(player, Items.stick, amount);
    }
}
