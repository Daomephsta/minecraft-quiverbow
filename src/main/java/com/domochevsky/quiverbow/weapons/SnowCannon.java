package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.SnowShot;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class SnowCannon extends WeaponBase
{
	public SnowCannon()
	{
		super("snow_cannon", 64);
		setFiringBehaviour(new SalvoFiringBehaviour<SnowCannon>(this, 4, (world, weaponStack, entity, data, properties) ->
		{
			float spreadHor = Helper.randomIntInRange(world.rand, -10, 10);
			float spreadVert = Helper.randomIntInRange(world.rand, -10, 10);
			SnowShot snow = new SnowShot(world, entity, properties.getProjectileSpeed(), spreadHor, spreadVert,
					new PotionEffect(MobEffects.SLOWNESS, properties.getInt(CommonProperties.PROP_SLOWNESS_DUR), properties.getInt(CommonProperties.PROP_SLOWNESS_STRENGTH)));

			int dmg_range = properties.getDamageMax() - properties.getDamageMin();
			int dmg = properties.getDamageMin() + world.rand.nextInt(dmg_range + 1);
			snow.damage = dmg;

			return snow;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(1).maximumDamage(2).projectileSpeed(1.5F).kickback(2)
				.cooldown(15).mobUsable()
				.intProperty(CommonProperties.PROP_SLOWNESS_STRENGTH, CommonProperties.COMMENT_SLOWNESS_STRENGTH, 3)
				.intProperty(CommonProperties.PROP_SLOWNESS_DUR, CommonProperties.COMMENT_SLOWNESS_DUR, 40).build();
	}
}
