package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.GoldMagazine;
import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.projectiles.CoinShot;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CoinTosser_Mod extends CoinTosser
{
	public CoinTosser_Mod(_AmmoBase ammo)
	{
		super("coin_tosser_mod", ammo, 72);
		setFiringBehaviour(new SalvoFiringBehaviour<CoinTosser_Mod>(this, 3, (world, weaponStack, entity, data) ->
		{
			CoinTosser weapon = (CoinTosser) weaponStack.getItem();
			int dmg_range = weapon.DmgMax - weapon.DmgMin; // If max dmg is 20
															// and min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 0
			// and 10
			dmg += weapon.DmgMin; // Adding the min dmg of 10 back on top,
									// giving us
			// the proper damage range (10-20)

			int spread = weapon instanceof CoinTosser_Mod ? 2 : 5;
			// http://www.anderswallin.net/2009/05/uniform-random-points-in-a-circle-using-polar-coordinates/
			int theta = world.rand.nextInt(361);
			float r = (float) (spread * Math.sqrt(Math.random()));
			float spreadHor = (float) (r * Math.cos(theta));
			float spreadVert = (float) (r * Math.sin(theta));

			CoinShot projectile = new CoinShot(world, entity, (float) weapon.Speed, spreadHor, spreadVert);
			projectile.damage = dmg;
			projectile.setDrop(weapon.shouldDrop);
			return projectile;
		}));
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.DmgMin = config.get(this.name, "What damage am I dealing per nugget, at least? (default 1)", 1).getInt();
		this.DmgMax = config.get(this.name, "What damage am I dealing per nugget, tops? (default 3)", 3).getInt();

		this.Speed = config.get(this.name, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5)
				.getDouble();

		this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 1)", 1)
				.getInt();

		this.Cooldown = config.get(this.name, "How long until I can fire again? (default 15 ticks)", 15).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);

		this.shouldDrop = config.get(this.name, "Do I drop gold nuggets on misses? (default true)", true)
				.getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// Modifying the Coin Tosser with double piston tech
			GameRegistry.addShapelessRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1),
					Helper.getWeaponStackByClass(CoinTosser.class, true), Blocks.STICKY_PISTON, Blocks.TRIPWIRE_HOOK,
					Items.IRON_INGOT, Items.IRON_INGOT);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		// Ammo
		Helper.registerAmmoRecipe(GoldMagazine.class, this);
	}
}
