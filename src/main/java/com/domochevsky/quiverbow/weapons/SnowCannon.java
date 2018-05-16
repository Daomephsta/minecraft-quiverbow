package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.projectiles.SnowShot;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class SnowCannon extends WeaponBase
{
	private int slowStrength; // -15% speed per level. Lvl 3 = -45%
	private int slowDuration;

	public SnowCannon()
	{
		super("snow_cannon", 64);
		setFiringBehaviour(new SalvoFiringBehaviour<SnowCannon>(this, 4, (world, weaponStack, entity, data) ->
		{
			float spreadHor = world.rand.nextFloat() * 20 - 10; // Spread
																// between -5
																// and 5
			float spreadVert = world.rand.nextFloat() * 20 - 10;
			SnowShot snow = new SnowShot(world, entity, (float) this.speed, spreadHor, spreadVert,
					new PotionEffect(MobEffects.SLOWNESS, this.slowDuration, this.slowStrength));

			// Random Damage
			int dmg_range = this.damageMax - this.damageMin; // If max dmg is 20 and
														// min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 0
			// and 10
			dmg += this.damageMin; // Adding the min dmg of 10 back on top, giving
								// us
			// the proper damage range (10-20)
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
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 1)", 1).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 2)", 2).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5)
				.getDouble();
		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 2)", 2)
				.getInt();
		this.cooldown = config.get(this.name, "How long until I can fire again? (default 15 ticks)", 15).getInt();

		this.slowStrength = config.get(this.name, "How strong is my Slowness effect? (default 3)", 3).getInt();
		this.slowDuration = config.get(this.name, "How long does my Slowness effect last? (default 40 ticks)", 40)
				.getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}
}
