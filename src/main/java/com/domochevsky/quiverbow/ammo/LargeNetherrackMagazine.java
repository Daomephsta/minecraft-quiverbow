package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.util.InventoryHelper;
import com.domochevsky.quiverbow.util.Utils;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class LargeNetherrackMagazine extends AmmoMagazine
{
    public LargeNetherrackMagazine()
    {
	super(1, 8);
	this.setMaxDamage(200); // Filled with gold nuggets (8 shots with 9
				// scatter, 24 with 3 scatter)
	this.setCreativeTab(CreativeTabs.COMBAT); // On the combat tab by
						  // default, since this is
						  // amunition
    }

    @Override
    public String getIconPath()
    {
	return "LargeNetherAmmo";
    }

    @Override
    public void addRecipes()
    {
	GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "x x", "x x", "xgx", 'x',
		Blocks.NETHER_BRICK, 'g', Items.IRON_INGOT);
    }

    @Override
    protected boolean hasComponentItems(EntityPlayer player, int amount)
    {
	return InventoryHelper.hasBlock(player, Blocks.NETHERRACK, amount);
    }

    @Override
    protected boolean consumeComponentItems(EntityPlayer player, int amount)
    {
	Utils.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.3F);
	return InventoryHelper.consumeBlock(player, Blocks.NETHERRACK, amount);
    }
}
