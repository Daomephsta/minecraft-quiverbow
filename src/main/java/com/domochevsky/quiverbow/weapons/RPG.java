package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.BigRocket;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class RPG extends WeaponBase
{
	private static final String PROP_TRAVEL_TIME = "maxFlightTime";
	
	public RPG()
	{
		this("rocket_launcher", 1);
	}

	protected RPG(String name, int maxAmmo)
	{
		super(name, maxAmmo);
		setFiringBehaviour(new SingleShotFiringBehaviour<WeaponBase>(this, (world, weaponStack, entity, data, properties) ->
		{
			BigRocket rocket = new BigRocket(world, entity, properties.getProjectileSpeed());
			rocket.explosionSize = properties.getFloat(CommonProperties.PROP_EXPLOSION_SIZE);
			rocket.travelTicksMax = properties.getInt(PROP_TRAVEL_TIME);
			rocket.dmgTerrain = properties.getBoolean(CommonProperties.PROP_DAMAGE_TERRAIN);

			return rocket;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 2.0F, 0.6F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().projectileSpeed(2.0F).kickback(3).cooldown(60).mobUsable()
				.floatProperty(CommonProperties.PROP_EXPLOSION_SIZE, CommonProperties.COMMENT_EXPLOSION_SIZE, 4.0F)
				.intProperty(PROP_TRAVEL_TIME, "The maximum flight time of the rocket. It will explode after this.", 20)
				.booleanProperty(CommonProperties.PROP_DAMAGE_TERRAIN, CommonProperties.COMMENT_DAMAGE_TERRAIN, true)
				.build();
	}
}
