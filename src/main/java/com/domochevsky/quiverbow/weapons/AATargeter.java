package com.domochevsky.quiverbow.weapons;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AATargeter extends WeaponBase
{
	public AATargeter()
	{
		super("aa_target_assist", 1);
		this.setCreativeTab(CreativeTabs.TOOLS); // This is a tool
	} // Not consuming ammo

	public static final double TARGETING_DISTANCE = 64;

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		this.setDamage(stack, 1); // Set to be firing
		this.setCooldown(stack, 4); // 4 ticks

		// SFX
		player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.3F, 2.0F);

		return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
	{
		if (world.isRemote)
		{
			return;
		} // Not doing this on client side

		if (this.getCooldown(stack) > 0) // Active right now, so ticking down
		{
			this.setCooldown(stack, this.getCooldown(stack) - 1);

			if (this.getCooldown(stack) >= 0)
			{
				this.setDamage(stack, 0);
			} // Back to inactive
		}
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return false;
	} // No point in showing the bar for this

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
		list.add(I18n.format(getUnlocalizedName() + ".description"));
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().build();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		if(!ArrayUtils.contains(this.getCreativeTabs(), tab)) return;
		subItems.add(new ItemStack(this, 1, 0));
	}
}
