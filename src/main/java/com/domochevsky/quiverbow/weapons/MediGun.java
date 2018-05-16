package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.projectiles.HealthBeam;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
			HealthBeam beam = new HealthBeam(entity.world, entity, (float) this.speed);

			beam.ignoreFrustumCheck = true;
			beam.ticksInAirMax = 40;

			entity.world.spawnEntity(beam); // Firing!

			this.consumeAmmo(stack, entity, 1);
			this.setCooldown(stack, this.cooldown);
		}
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.speed = config.get(this.name, "How fast are my beams? (default 5.0 BPT (Blocks Per Tick))", 5.0)
				.getDouble();

		this.isMobUsable = config.get(this.name,
				"Can I be used by QuiverMobs? (default false. They don't know what friends are.)", false)
				.getBoolean(true);
	}
}
