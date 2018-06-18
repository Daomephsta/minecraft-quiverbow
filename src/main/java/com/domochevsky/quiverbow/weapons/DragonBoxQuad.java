package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.SmallRocket;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour.SalvoData;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class DragonBoxQuad extends WeaponBase
{
	public DragonBoxQuad()
	{
		super("quad_dragonbox", 64);
		setFiringBehaviour(new SalvoFiringBehaviour<DragonBoxQuad>(this, 4, (world, weaponStack, entity, data, properties) ->
		{
			float distanceMod = 5.0f;

			int randNum = world.rand.nextInt(100) + 1; // 1-100

			if (randNum >= 95)
			{
				distanceMod = world.rand.nextInt(40);

				distanceMod -= 20; // Range of -20 to 20
			}

			switch (((SalvoData) data).shotCount)
			{
			case 0 :
				return this.fireRocket(world, entity, 0, 0); // Center 1
			case 1 :
				return this.fireRocket(world, entity, distanceMod, 0); // Right 2
			case 2 :
				return this.fireRocket(world, entity, -distanceMod, 0);// Left 3
			case 3 :
				return this.fireRocket(world, entity, 0, -distanceMod);// Top 4
			default :
				return null;
			}
		}));
	}

	private SmallRocket fireRocket(World world, Entity entity, float spreadHor, float spreadVert)
	{
		SmallRocket rocket = new SmallRocket(world, entity, getProjectileSpeed(), spreadHor, spreadVert);

		// Random Damage
		int dmg_range = this.getProperties().getDamageMin() - getProperties().getDamageMin(); // If max dmg is 20 and min
		// is 10, then the range will
		// be 10
		int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
		// and 10
		dmg += getProperties().getDamageMin(); // Adding the min dmg of 10 back on top, giving us
		// the proper damage range (10-20)

		// Properties
		rocket.damage = dmg;
		rocket.fireDuration = getProperties().getInt(CommonProperties.PROP_FIRE_DUR_ENTITY);
		rocket.explosionSize = getProperties().getFloat(CommonProperties.PROP_EXPLOSION_SIZE);
		rocket.dmgTerrain = getProperties().getBoolean(CommonProperties.PROP_DAMAGE_TERRAIN);

		return rocket;
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
				.kickback(1).cooldown(10)
				.intProperty(CommonProperties.PROP_FIRE_DUR_ENTITY, CommonProperties.COMMENT_FIRE_DUR_ENTITY, 6)
				.floatProperty(CommonProperties.PROP_EXPLOSION_SIZE, CommonProperties.COMMENT_EXPLOSION_SIZE, 1.0F)
				.booleanProperty(CommonProperties.PROP_DAMAGE_TERRAIN, CommonProperties.COMMENT_DAMAGE_TERRAIN, true)
				.build();
	}
}
