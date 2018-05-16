package com.domochevsky.quiverbow.weapons;

import org.apache.commons.lang3.ArrayUtils;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.projectiles.SunLight;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Sunray extends WeaponBase
{
	public Sunray()
	{
		super("sunray", 1);
	}

	private int maxTicks;
	private int lightMin;
	private int fireDuration;

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		double dur = (1d / this.cooldown) * (this.cooldown - this.getCooldown(stack)); // Display
		// durability
		return 1d - dur; // Reverse again. Tch
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		this.doSingleFire(stack, world, player); // Handing it over to the
		// neutral firing function
		return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity) // Server
	// side
	{
		if (this.getCooldown(stack) > 0)
		{
			return;
		} // Hasn't cooled down yet

		Helper.knockUserBack(entity, this.kickback); // Kickback
		if (!world.isRemote)
		{
			// Firing a beam that goes through walls
			SunLight shot = new SunLight(world, entity, (float) this.speed);

			// Random Damage
			int dmg_range = this.damageMax - this.damageMin; // If max dmg is 20 and
														// min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 0
			// and 10
			dmg += this.damageMin; // Adding the min dmg of 10 back on top, giving
								// us
			// the proper damage range (10-20)

			// The moving end point
			shot.damage = dmg;
			shot.fireDuration = this.fireDuration;

			shot.ignoreFrustumCheck = true;
			shot.ticksInAirMax = this.maxTicks;

			world.spawnEntity(shot); // Firing!
		}

		// SFX
		entity.playSound(SoundEvents.ENTITY_BLAZE_DEATH, 0.7F, 2.0F);
		entity.playSound(SoundEvents.ENTITY_FIREWORK_BLAST, 2.0F, 0.1F);

		this.setCooldown(stack, this.cooldown);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) // Overhauled
	// default
	{
		int light = world.getLight(entity.getPosition());

		if (light >= this.lightMin)
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
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage are my arrows dealing, at least? (default 14)", 14).getInt();
		this.damageMax = config.get(this.name, "What damage are my arrows dealing, tops? (default 20)", 20).getInt();

		this.speed = 4.0f;
		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
				.getInt();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 120 ticks)", 120).getInt();

		this.fireDuration = config.get(this.name, "How long is what I hit on fire? (default 10s)", 10).getInt();
		this.maxTicks = config.get(this.name, "How long does my beam exist, tops? (default 60 ticks)", 60).getInt();
		this.lightMin = config.get(this.name, "What light level do I need to recharge, at least? (default 12)", 12)
				.getInt();

		this.isMobUsable = config
				.get(this.name, "Can I be used by QuiverMobs? (default false. Too damn bright for their taste.)", false)
				.getBoolean();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		if(!ArrayUtils.contains(this.getCreativeTabs(), tab)) return;
		subItems.add(new ItemStack(this, 1, 0)); // Only one, and it's full
	}
}
