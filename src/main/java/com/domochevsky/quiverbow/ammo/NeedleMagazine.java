package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.util.InventoryHelper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;

public class NeedleMagazine extends AmmoMagazine
{
	public NeedleMagazine()
	{
		super(1, 8);
		this.setMaxDamage(64); // Filled with cactus thorns
		this.setCreativeTab(CreativeTabs.COMBAT);
	}

	@Override
	protected boolean hasComponentItems(EntityPlayer player, int amount)
	{
		return InventoryHelper.hasBlock(player, Blocks.CACTUS, amount);
	}

	@Override
	protected boolean consumeComponentItems(EntityPlayer player, int amount)
	{
		Helper.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 1.3F);
		return InventoryHelper.consumeBlock(player, Blocks.CACTUS, amount);
	}
}
