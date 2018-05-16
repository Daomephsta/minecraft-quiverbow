package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.util.InventoryHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class LapisMagazine extends AmmoMagazine
{
	public LapisMagazine()
	{
		super();
		this.setMaxDamage(150); // Filled with lapis
		this.setCreativeTab(CreativeTabs.COMBAT); // On the combat tab by
		// default, since this is
		// amunition
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItemDamage() == 0)
		{
			return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
		} // Already fully loaded
		if (stack.getItemDamage() < 25)
		{
			return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
		} // No room for another lapis block

		if (player.capabilities.isCreativeMode)
		{
			if (world.isRemote)
				Minecraft.getMinecraft().ingameGUI.setOverlayMessage(I18n.format("quiverchevsky.ammo.nocreative"),
						false);
			return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
		}
		if (hasComponentItems(player, 1))
		{
			// this.consumeItemStack(player.inventory, this.lapisStack); //
			// We're just grabbing what we need from the inventory

			int dmg = stack.getItemDamage() - 25;
			stack.setItemDamage(dmg);

			consumeComponentItems(player, 1);
		}

		return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	protected boolean hasComponentItems(EntityPlayer player, int amount)
	{
		return InventoryHelper.hasBlock(player, Blocks.LAPIS_BLOCK, amount);
	}

	@Override
	protected boolean consumeComponentItems(EntityPlayer player, int amount)
	{
		Helper.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.2F);
		return InventoryHelper.consumeBlock(player, Blocks.LAPIS_BLOCK, amount);
	}
}
