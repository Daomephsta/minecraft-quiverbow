package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.SabotRocket;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class MortarDragon extends WeaponBase
{
	private int fireDuration;
	private double explosionSize;

	public MortarDragon()
	{
		super("dragon_mortar", 8);
		setFiringBehaviour(new SingleShotFiringBehaviour<WeaponBase>(this, (world, weaponStack, entity, data) ->
		{
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

			// Firing
			SabotRocket projectile = new SabotRocket(world, entity, (float) this.speed);
			projectile.damage = dmg;
			projectile.fireDuration = this.fireDuration;
			projectile.explosionSize = this.explosionSize;

			return projectile;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_LARGE, (byte) 1);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity) // Server side
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 2.0F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage are my rockets dealing, at least? (default 4)", 4).getInt();
		this.damageMax = config.get(this.name, "What damage are my rockets dealing, tops? (default 6)", 6).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5)
				.getDouble();
		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
				.getInt();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 20 ticks)", 20).getInt();

		this.fireDuration = config.get(this.name, "How long is what I hit on fire? (default 6s)", 6).getInt();
		this.explosionSize = config.get(this.name,
				"How big are my explosions? (default 1.0 blocks, for no terrain damage. TNT is 4.0 blocks)", 1.0)
				.getDouble();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}
}
