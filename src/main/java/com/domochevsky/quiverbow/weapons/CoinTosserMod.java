package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.projectiles.CoinShot;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

public class CoinTosserMod extends CoinTosser
{
	public CoinTosserMod(AmmoBase ammo)
	{
		super("coin_tosser_mod", ammo, 72);
		setFiringBehaviour(new SalvoFiringBehaviour<CoinTosserMod>(this, 3, (world, weaponStack, entity, data, properties) ->
		{
			int dmg_range = properties.getDamageMin() - properties.getDamageMin(); // If max dmg is 20
															// and min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 0
			// and 10
			dmg += properties.getDamageMin(); // Adding the min dmg of 10 back on top,
									// giving us
			// the proper damage range (10-20)

			// http://www.anderswallin.net/2009/05/uniform-random-points-in-a-circle-using-polar-coordinates/
			int theta = world.rand.nextInt(361);
			float r = (float) (2 * Math.sqrt(Math.random()));
			float spreadHor = (float) (r * Math.cos(theta));
			float spreadVert = (float) (r * Math.sin(theta));

			CoinShot projectile = new CoinShot(world, entity, properties.getProjectileSpeed(), spreadHor, spreadVert);
			projectile.damage = dmg;
			projectile.setDrop(properties.getBoolean(CommonProperties.PROP_SHOULD_DROP));
			return projectile;
		}));
	}
}
