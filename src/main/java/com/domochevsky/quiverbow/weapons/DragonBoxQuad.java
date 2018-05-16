package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.projectiles.SmallRocket;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour.SalvoData;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class DragonBoxQuad extends WeaponBase
{
	private int fireDuration;
	private double explosionSize;
	private boolean dmgTerrain;

	public DragonBoxQuad()
	{
		super("quad_dragonbox", 64);
		setFiringBehaviour(new SalvoFiringBehaviour<DragonBoxQuad>(this, 4, (world, weaponStack, entity, data) ->
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
				return this.fireRocket(world, entity, distanceMod, 0); // Right
																		// 2
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
		SmallRocket rocket = new SmallRocket(world, entity, (float) this.speed, spreadHor, spreadVert);

		// Random Damage
		int dmg_range = this.damageMax - this.damageMin; // If max dmg is 20 and min
		// is 10, then the range will
		// be 10
		int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
		// and 10
		dmg += this.damageMin; // Adding the min dmg of 10 back on top, giving us
		// the proper damage range (10-20)

		// Properties
		rocket.damage = dmg;
		rocket.fireDuration = this.fireDuration;
		rocket.explosionSize = this.explosionSize;
		rocket.dmgTerrain = this.dmgTerrain;

		return rocket;
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 1.0F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 4)", 4).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 6)", 6).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 1.3 BPT (Blocks Per Tick))", 1.3)
				.getDouble();

		this.knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 2)", 2)
				.getInt();
		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 1)", 1)
				.getInt();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 10 ticks)", 10).getInt();

		this.fireDuration = config.get(this.name, "How long is what I hit on fire? (default 6s)", 6).getInt();

		this.explosionSize = config.get(this.name,
				"How big are my explosions? (default 1.0 blocks, for no terrain damage. TNT is 4.0 blocks)", 1.0)
				.getDouble();
		this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true)
				.getBoolean(true);

		this.isMobUsable = config
				.get(this.name, "Can I be used by QuiverMobs? (default false. A bit too high-power for them.)", false)
				.getBoolean();
	}
}
