package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.util.InventoryHelper;
import com.domochevsky.quiverbow.util.Utils;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;

public class GatlingAmmo extends AmmoMagazine
{
    public GatlingAmmo()
    {
	super(4, 4);
	this.setMaxDamage(200);
	this.setCreativeTab(CreativeTabs.COMBAT); // On the combat tab by
						  // default, since this is
						  // amunition
    }

    @Override
    public String getIconPath()
    {
	return "GatlingAmmo";
    }

    @Override
    public void addRecipes()
    {
	// First, the clip itself (empty)
	GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "y y", "y y", "yxy", 'x', Items.IRON_INGOT,
		'y', Blocks.PLANKS);
    }

    @Override
    protected boolean hasComponentItems(EntityPlayer player, int amount)
    {
	return InventoryHelper.hasItem(player, Items.REEDS, amount)
		&& InventoryHelper.hasItem(player, Items.STICK, amount);
    }

    @Override
    protected boolean consumeComponentItems(EntityPlayer player, int amount)
    {
	Utils.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 1.50F);
	return InventoryHelper.consumeItem(player, Items.REEDS, amount)
		&& InventoryHelper.consumeItem(player, Items.STICK, amount);
    }
}
