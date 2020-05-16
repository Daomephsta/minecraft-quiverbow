package com.domochevsky.quiverbow.weapons;

import org.apache.commons.lang3.ArrayUtils;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.SunLight;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class Sunray extends WeaponBase
{
	private static final String PROP_MIN_LIGHT = "minLight";

	public Sunray()
	{
		super("sunray", 1);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		double dur = (1D / getMaxCooldown()) * (getMaxCooldown() - this.getCooldown(stack)); // Display durability
		return 1D - dur; // Reverse again. Tch
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		this.doSingleFire(world, player, stack, hand);
		return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean doSingleFire(World world, EntityLivingBase entity, ItemStack stack, EnumHand hand)
	{
		boolean superResult = super.doSingleFire(world, entity, stack, hand);
		if (this.getCooldown(stack) > 0)
		{
			return false;
		}

		Helper.knockUserBack(entity, getKickback()); // Kickback
		if (!world.isRemote)
		{
			// Firing a beam that goes through walls
			SunLight shot = new SunLight(world, entity, getProjectileSpeed());

			int dmg_range = this.getProperties().getDamageMin() - getProperties().getDamageMin();
			int dmg = getProperties().getDamageMin() + world.rand.nextInt(dmg_range + 1);

			shot.damage = dmg;
			shot.fireDuration = getProperties().getInt(CommonProperties.PROP_FIRE_DUR_ENTITY);

			shot.ignoreFrustumCheck = true;

			world.spawnEntity(shot); // Firing!
		}

		// SFX
		entity.playSound(SoundEvents.ENTITY_BLAZE_DEATH, 0.7F, 2.0F);
		entity.playSound(SoundEvents.ENTITY_FIREWORK_BLAST, 2.0F, 0.1F);

		this.resetCooldown(stack);
		return superResult;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
	{
		int light = world.getLight(entity.getPosition());

		if (light >= getProperties().getInt(PROP_MIN_LIGHT))
		{
			if (this.getCooldown(stack) > 0)
			{
				this.setCooldown(stack, this.getCooldown(stack) - 1);
			} // Cooling down
			if (this.getCooldown(stack) == 1)
			{
				this.doCooldownSFX(world, entity);
			} // One tick before cooldown is done with, so SFX now
		}
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity) // Server side
	{
		entity.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 0.5F);
		entity.playSound(SoundEvents.ENTITY_CAT_HISS, 0.6F, 2.0F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(14).maximumDamage(20).kickback(3).cooldown(120)
				.intProperty(CommonProperties.PROP_FIRE_DUR_ENTITY, CommonProperties.COMMENT_FIRE_DUR_ENTITY, 10)
				.intProperty(PROP_MIN_LIGHT, "The minimum light level needed to recharge", 12).build();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		if(!ArrayUtils.contains(this.getCreativeTabs(), tab)) return;
		subItems.add(new ItemStack(this, 1, 0)); // Only one, and it's full
	}
}
