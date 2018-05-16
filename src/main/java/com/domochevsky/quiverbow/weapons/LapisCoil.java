package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.projectiles.LapisShot;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class LapisCoil extends MagazineFedWeapon
{
	int weaknessStrength;
	int weaknessDuration;
	int nauseaDuration;
	int hungerStrength;
	int hungerDuration;

	public LapisCoil(AmmoBase ammo)
	{
		super("lapis_coil", ammo, 100);
		setFiringBehaviour(new SingleShotFiringBehaviour<LapisCoil>(this, (world, weaponStack, entity, data) ->
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

			// Projectile
			LapisShot projectile = new LapisShot(world, entity, (float) this.speed,
					new PotionEffect(MobEffects.NAUSEA, this.nauseaDuration, 1),
					new PotionEffect(MobEffects.HUNGER, this.hungerDuration, this.hungerStrength),
					new PotionEffect(MobEffects.WEAKNESS, this.weaknessDuration, this.weaknessStrength));
			projectile.damage = dmg;

			projectile.ticksInGroundMax = 100; // 5 sec before it disappears

			return projectile;
		}));
		this.cooldown = 4;
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.5F);
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 3.0F);
	}

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 1)", 1).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 3)", 3).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5)
				.getDouble();

		this.weaknessStrength = config.get(this.name, "How strong is my Weakness effect? (default 2)", 2).getInt();
		this.weaknessDuration = config.get(this.name, "How long does my Weakness effect last? (default 40 ticks)", 40)
				.getInt();
		this.nauseaDuration = config.get(this.name, "How long does my Nausea effect last? (default 40 ticks)", 40)
				.getInt();
		this.hungerStrength = config.get(this.name, "How strong is my Hunger effect? (default 2)", 2).getInt();
		this.hungerDuration = config.get(this.name, "How long does my Hunger effect last? (default 40 ticks)", 40)
				.getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}
}