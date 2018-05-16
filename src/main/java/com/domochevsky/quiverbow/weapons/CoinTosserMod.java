package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.projectiles.CoinShot;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CoinTosserMod extends CoinTosser
{
	public CoinTosserMod(AmmoBase ammo)
	{
		super("coin_tosser_mod", ammo, 72);
		setFiringBehaviour(new SalvoFiringBehaviour<CoinTosserMod>(this, 3, (world, weaponStack, entity, data) ->
		{
			CoinTosser weapon = (CoinTosser) weaponStack.getItem();
			int dmg_range = weapon.damageMax - weapon.damageMin; // If max dmg is 20
															// and min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 0
			// and 10
			dmg += weapon.damageMin; // Adding the min dmg of 10 back on top,
									// giving us
			// the proper damage range (10-20)

			int spread = weapon instanceof CoinTosserMod ? 2 : 5;
			// http://www.anderswallin.net/2009/05/uniform-random-points-in-a-circle-using-polar-coordinates/
			int theta = world.rand.nextInt(361);
			float r = (float) (spread * Math.sqrt(Math.random()));
			float spreadHor = (float) (r * Math.cos(theta));
			float spreadVert = (float) (r * Math.sin(theta));

			CoinShot projectile = new CoinShot(world, entity, (float) weapon.speed, spreadHor, spreadVert);
			projectile.damage = dmg;
			projectile.setDrop(weapon.shouldDrop);
			return projectile;
		}));
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing per nugget, at least? (default 1)", 1).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing per nugget, tops? (default 3)", 3).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5)
				.getDouble();

		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 1)", 1)
				.getInt();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 15 ticks)", 15).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);

		this.shouldDrop = config.get(this.name, "Do I drop gold nuggets on misses? (default true)", true)
				.getBoolean(true);
	}
}
