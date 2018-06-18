package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.BlazeShot;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponCrossbow;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class CrossbowBlaze extends WeaponCrossbow
{
	public CrossbowBlaze()
	{
		super("blaze_crossbow", 1, (world, weaponStack, entity, data, properties) ->
		{
			BlazeShot entityarrow = new BlazeShot(world, entity, properties.getProjectileSpeed());

			// Random Damage
			int dmg_range = properties.getDamageMin() - properties.getDamageMin(); // If max dmg is 20
															// and min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1);// Range will be between
														// 0
			// and 10
			dmg += properties.getDamageMin(); // Adding the min dmg of 10 back on top,
									// giving us
			// the proper damage range (10-20)

			entityarrow.damage = dmg;
			entityarrow.knockbackStrength = properties.getKnockback(); // Comes with an
																// inbuild
			// knockback II
			entityarrow.fireDuration = properties.getInt(CommonProperties.PROP_FIRE_DUR_ENTITY);
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
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(20).maximumDamage(30).projectileSpeed(3.0F).knockback(2)
				.intProperty(CommonProperties.PROP_FIRE_DUR_ENTITY, CommonProperties.COMMENT_FIRE_DUR_ENTITY, 15)
				.build();
	}
}
