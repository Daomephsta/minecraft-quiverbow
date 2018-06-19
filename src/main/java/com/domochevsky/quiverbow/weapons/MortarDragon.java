package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.SabotRocket;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class MortarDragon extends WeaponBase
{
	public MortarDragon()
	{
		super("dragon_mortar", 8);
		setFiringBehaviour(new SingleShotFiringBehaviour<WeaponBase>(this, (world, weaponStack, entity, data, properties) ->
		{
			// Firing
			SabotRocket projectile = new SabotRocket(world, entity, properties.getProjectileSpeed());
			projectile.damage = Helper.randomIntInRange(world.rand, properties.getDamageMin(), properties.getDamageMax());
			projectile.fireDuration = properties.getInt(CommonProperties.PROP_FIRE_DUR_ENTITY);
			projectile.explosionSize = properties.getFloat(CommonProperties.PROP_EXPLOSION_SIZE);

			return projectile;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity, EnumParticleTypes.SMOKE_LARGE, (byte) 1);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity) // Server side
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 2.0F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(4).maximumDamage(6).projectileSpeed(3.0F).cooldown(20)
				.mobUsable()
				.intProperty(CommonProperties.PROP_FIRE_DUR_ENTITY, CommonProperties.COMMENT_FIRE_DUR_ENTITY, 6)
				.floatProperty(CommonProperties.PROP_EXPLOSION_SIZE, CommonProperties.COMMENT_EXPLOSION_SIZE, 1.0F)
				.build();
	}
}
