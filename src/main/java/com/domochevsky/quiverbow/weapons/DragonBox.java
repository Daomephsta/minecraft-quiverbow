package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.SmallRocket;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class DragonBox extends WeaponBase
{
	public DragonBox()
	{
		super("dragonbox", 64);
		setFiringBehaviour(new SingleShotFiringBehaviour<DragonBox>(this, (world, weaponStack, entity, data, properties) ->
		{ // Random Damage
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
			SmallRocket shot = new SmallRocket(world, entity, properties.getProjectileSpeed(), 0, 0);

			shot.damage = dmg;
			shot.fireDuration = properties.getInt(CommonProperties.PROP_FIRE_DUR_ENTITY);
			shot.explosionSize = properties.getFloat(CommonProperties.PROP_EXPLOSION_SIZE);
			shot.dmgTerrain = properties.getBoolean(CommonProperties.PROP_DAMAGE_TERRAIN);

			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 1.0F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(4).maximumDamage(6).projectileSpeed(1.3F).knockback(2)
				.kickback(1).mobUsable()
				.intProperty(CommonProperties.PROP_FIRE_DUR_ENTITY, CommonProperties.COMMENT_FIRE_DUR_ENTITY, 6)
				.floatProperty(CommonProperties.PROP_EXPLOSION_SIZE, CommonProperties.COMMENT_EXPLOSION_SIZE, 1.0F)
				.booleanProperty(CommonProperties.PROP_DAMAGE_TERRAIN, CommonProperties.COMMENT_DAMAGE_TERRAIN, true)
				.build();
	}
}
