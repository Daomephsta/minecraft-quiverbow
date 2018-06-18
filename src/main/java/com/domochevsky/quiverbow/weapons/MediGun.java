package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.HealthBeam;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class MediGun extends WeaponBase
{
	public MediGun()
	{
		super("ray_of_hope", 320);
	}

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
		// Good to go (already verified)

		// SFX
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.7F, 1.4F);

		if (!world.isRemote)
		{
			HealthBeam beam = new HealthBeam(entity.world, entity, getProjectileSpeed());

			beam.ignoreFrustumCheck = true;
			beam.ticksInAirMax = 40;

			entity.world.spawnEntity(beam); // Firing!

			this.consumeAmmo(stack, entity, 1);
			this.resetCooldown(stack);
		}
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().build();
	}
}
