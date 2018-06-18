package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.SabotArrow;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class MortarArrow extends WeaponBase
{
	public MortarArrow()
	{
		super("arrow_mortar", 8);
		setFiringBehaviour(new SingleShotFiringBehaviour<MortarArrow>(this, (world, weaponStack, entity, data, properties) ->
		{
			// Random Damage
			int dmg_range = properties.getDamageMin() - properties.getDamageMin(); // If max dmg is 20 and
														// min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 0
			// and 10
			dmg += properties.getDamageMin(); // Adding the min dmg of 10 back on top, giving
								// us
			// the proper damage range (10-20)

			// Firing
			SabotArrow projectile = new SabotArrow(world, entity, properties.getProjectileSpeed());
			projectile.damage = dmg;

			return projectile;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_LARGE, (byte) 1);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity) // Server side
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 2.0F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(2).maximumDamage(10).projectileSpeed(1.5F).kickback(3)
				.cooldown(20).mobUsable().build();
	}
}
