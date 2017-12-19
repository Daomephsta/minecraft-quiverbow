package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.util.InventoryHelper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GoldMagazine extends AmmoMagazine
{
	public GoldMagazine()
	{
		super(1, 8);
		this.setMaxDamage(72); // Filled with gold nuggets (8 shots with 9
		// scatter, 24 with 3 scatter)
		this.setCreativeTab(CreativeTabs.COMBAT); // On the combat tab by
		// default, since this is
		// amunition
	}

	@Override
	public void addRecipes()
	{
		GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "x x", "x x", "xgx", 'x', Items.IRON_INGOT,
				'g', Items.GOLD_INGOT);
	}

	@Override
	protected boolean hasComponentItems(EntityPlayer player, int amount)
	{
		return InventoryHelper.hasItem(player, Items.GOLD_NUGGET, amount);
	}

	@Override
	protected boolean consumeComponentItems(EntityPlayer player, int amount)
	{
		Helper.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.3F);
		return InventoryHelper.consumeItem(player, Items.GOLD_NUGGET, amount);
	}
}
