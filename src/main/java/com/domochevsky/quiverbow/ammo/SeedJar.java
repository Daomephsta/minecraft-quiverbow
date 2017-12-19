package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.util.InventoryHelper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SeedJar extends AmmoMagazine
{
	public SeedJar() // Holds seeds for the Seed Sweeper (512, for 8 per shot
	// with 64 shots total), loaded directly into the weapon
	{
		super(8, 8);
		this.setMaxDamage(512); // Filled with gold nuggets (8 shots with 9
		// scatter, 24 with 3 scatter)
		this.setCreativeTab(CreativeTabs.COMBAT); // On the combat tab by
		// default, since this is
		// amunition
	}

	@Override
	public void addRecipes()
	{
		GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "gwg", "g g", "gig", 'g',
				Blocks.GLASS_PANE, 'i', Items.IRON_INGOT, 'w', Blocks.WOODEN_BUTTON);
	}

	@Override
	protected boolean hasComponentItems(EntityPlayer player, int amount)
	{
		return InventoryHelper.hasItem(player, Items.WHEAT_SEEDS, amount)
				|| InventoryHelper.hasItem(player, Items.MELON_SEEDS, amount)
				|| InventoryHelper.hasItem(player, Items.PUMPKIN_SEEDS, amount);
	}

	@Override
	protected boolean consumeComponentItems(EntityPlayer player, int amount)
	{
		Helper.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 0.7F);
		return InventoryHelper.consumeItem(player, Items.WHEAT_SEEDS, amount)
				|| InventoryHelper.consumeItem(player, Items.MELON_SEEDS, amount)
				|| InventoryHelper.consumeItem(player, Items.PUMPKIN_SEEDS, amount);
	}
}
