package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.FlintDust;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class FlintDuster extends WeaponBase
{
	private static final String PROP_MAX_RANGE = "maxRange";

	public FlintDuster()
	{
		super("flint_duster", 256);
		this.setCreativeTab(CreativeTabs.TOOLS); // Tool, so on the tool tab
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (this.getDamage(stack) >= stack.getMaxDamage())
		{
			return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
		} // Is empty

		this.doSingleFire(stack, world, player); // Handing it over to the
		// neutral firing function
		return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity) // Server
	// side
	{
		// Ignoring cooldown for firing purposes

		// SFX
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_BAT_TAKEOFF, 0.5F, 0.6F);

		if (!world.isRemote)
		{
			// Ready
			FlintDust shot = new FlintDust(world, entity, getProjectileSpeed());

			// Properties
			shot.damage = Helper.randomIntInRange(world.rand, getProperties().getDamageMin(), getProperties().getDamageMax());

			// Go
			world.spawnEntity(shot);
		}

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, 4);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().damage(1)
				.intProperty(PROP_MAX_RANGE, "The maximum range of this weapon in blocks", 7).build();
	}
}
