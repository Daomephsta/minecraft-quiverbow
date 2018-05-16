package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.projectiles.BigRocket;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class RPG extends WeaponBase
{
	public double explosionSize;
	protected int travelTime; // How many ticks the rocket can travel before
								// exploding
	protected boolean dmgTerrain; // Can our projectile damage terrain?

	public RPG()
	{
		this("rocket_launcher", 1);
	}

	protected RPG(String name, int maxAmmo)
	{
		super(name, maxAmmo);
		this.cooldown = 60;
		setFiringBehaviour(new SingleShotFiringBehaviour<WeaponBase>(this, (world, weaponStack, entity, data) ->
		{
			BigRocket rocket = new BigRocket(world, entity, (float) this.speed);
			rocket.explosionSize = this.explosionSize;
			rocket.travelTicksMax = this.travelTime;
			rocket.dmgTerrain = this.dmgTerrain;

			return rocket;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 2.0F, 0.6F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);
		this.speed = config.get(this.name, "How fast are my projectiles? (default 2.0 BPT (Blocks Per Tick))", 2.0)
				.getDouble();
		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
				.getInt();
		this.explosionSize = config.get(this.name, "How big are my explosions? (default 4.0 blocks, like TNT)", 4.0)
				.getDouble();
		this.travelTime = config
				.get(this.name, "How many ticks can my rocket fly before exploding? (default 20 ticks)", 20).getInt();
		this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true)
				.getBoolean(true);

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}
}
