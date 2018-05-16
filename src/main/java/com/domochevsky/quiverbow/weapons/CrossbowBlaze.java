package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.projectiles.BlazeShot;
import com.domochevsky.quiverbow.weapons.base.WeaponCrossbow;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CrossbowBlaze extends WeaponCrossbow
{
	private int fireDuration;

	public CrossbowBlaze()
	{
		super("blaze_crossbow", 1, (world, weaponStack, entity, data) ->
		{
			CrossbowBlaze weapon = (CrossbowBlaze) weaponStack.getItem();
			BlazeShot entityarrow = new BlazeShot(world, entity, (float) weapon.speed);

			// Random Damage
			int dmg_range = weapon.damageMax - weapon.damageMin; // If max dmg is 20
															// and min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1);// Range will be between
														// 0
			// and 10
			dmg += weapon.damageMin; // Adding the min dmg of 10 back on top,
									// giving us
			// the proper damage range (10-20)

			entityarrow.damage = dmg;
			entityarrow.knockbackStrength = weapon.knockback; // Comes with an
																// inbuild
			// knockback II
			entityarrow.fireDuration = weapon.fireDuration;
			entityarrow.ticksInGroundMax = 200; // 200 ticks for 10 sec

			return entityarrow;
		});
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		super.doFireFX(world, entity);
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.5F);
		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY + 0.5D, entity.posZ, 0.0D, 0.0D,
				0.0D);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 20)", 20).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 30)", 30).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 3.0 BPT (Blocks Per Tick))", 3.0)
				.getDouble();
		this.knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 2)", 2)
				.getInt();

		this.fireDuration = config.get(this.name, "How long is the target on fire? (default 15 sec)", 15).getInt();
		config.get(this.name, "How long do I keep burning when stuck in the ground? (default 10 sec)", 10).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false)", false)
				.getBoolean(true);
	}
}
