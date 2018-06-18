package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.CoinShot;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class CoinTosser extends MagazineFedWeapon
{
	public CoinTosser(AmmoBase ammo)
	{
		this("coin_tosser", ammo, 72);
		setFiringBehaviour(new SalvoFiringBehaviour<CoinTosser>(this, 9, (world, weaponStack, entity, data, properties) ->
		{
			// http://www.anderswallin.net/2009/05/uniform-random-points-in-a-circle-using-polar-coordinates/
			int theta = world.rand.nextInt(361);
			float r = (float) (5 * Math.sqrt(Math.random()));
			float spreadHor = (float) (r * Math.cos(theta));
			float spreadVert = (float) (r * Math.sin(theta));

			CoinShot projectile = new CoinShot(world, entity, properties.getProjectileSpeed(), spreadHor, spreadVert);
			projectile.damage = Helper.randomIntInRange(world.rand, getProperties().getDamageMin(), getProperties().getDamageMax());;
			projectile.setDrop(properties.getBoolean(CommonProperties.PROP_SHOULD_DROP));
			return projectile;
		}));
	}

	protected CoinTosser(String name, AmmoBase ammo, int maxAmmo)
	{
		super(name, ammo, maxAmmo);
	}

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 3.0F);
	}
	
	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(1).maximumDamage(3).projectileSpeed(2.5F).kickback(1)
				.cooldown(15).mobUsable()
				.booleanProperty(CommonProperties.PROP_SHOULD_DROP, CommonProperties.COMMENT_SHOULD_DROP, true).build();
	}
}
