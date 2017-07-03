package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.util.InventoryHelper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
    public void addRecipes()
    {
	// First, the clip itself (empty)
	GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "y y", "y y", "yxy", 'x', Items.IRON_INGOT,
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
	Helper.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 1.50F);
	return InventoryHelper.consumeItem(player, Items.REEDS, amount)
		&& InventoryHelper.consumeItem(player, Items.STICK, amount);
    }
}
