package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.util.InventoryHelper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EnderQuartzClip extends AmmoMagazine
{
	public EnderQuartzClip()
	{
		super();
		this.setMaxDamage(8);
		this.setCreativeTab(CreativeTabs.COMBAT); // On the combat tab by
		// default, since this is
		// amunition
	}

	@Override
	public void addRecipes()
	{
		GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "xxx", "ixi", "iii", 'x', Items.QUARTZ,
				'i', Items.IRON_INGOT);
	}

	@Override
	protected boolean hasComponentItems(EntityPlayer player, int amount)
	{
		return InventoryHelper.hasItem(player, Items.ENDER_PEARL, amount)
				&& InventoryHelper.hasItem(player, Items.QUARTZ, amount);
	}

	@Override
	protected boolean consumeComponentItems(EntityPlayer player, int amount)
	{
		Helper.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.3F);
		return InventoryHelper.consumeItem(player, Items.ENDER_PEARL, amount)
				&& InventoryHelper.consumeItem(player, Items.QUARTZ, amount);
	}
}
