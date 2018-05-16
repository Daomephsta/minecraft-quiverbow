package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.projectiles.RedSpray;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class RedSprayer extends MagazineFedWeapon
{
	private int witherStrength;
	private int witherDuration;
	private int blindnessDuration;

	public RedSprayer(AmmoBase ammo)
	{
		super("redstone_sprayer", ammo, 200);
		setFiringBehaviour(new SalvoFiringBehaviour<RedSprayer>(this, 5, (world, waeponStack, entity, data) ->
		{
			// Spread
			float spreadHor = world.rand.nextFloat() * 20 - 10; // Spread
																// between
			// -10 and 10
			float spreadVert = world.rand.nextFloat() * 20 - 10;

			RedSpray shot = new RedSpray(entity.world, entity, (float) this.speed, spreadHor, spreadVert,
					new PotionEffect(MobEffects.WITHER, this.witherDuration, this.witherStrength),
					new PotionEffect(MobEffects.BLINDNESS, this.blindnessDuration, 1));
			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.7F, 1.5F);
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

		this.speed = config.get(this.name, "How fast are my projectiles? (default 0.5 BPT (Blocks Per Tick))", 0.5)
				.getDouble();

		this.witherStrength = config.get(this.name, "How strong is my Wither effect? (default 2)", 2).getInt();
		this.witherDuration = config.get(this.name, "How long does my Wither effect last? (default 20 ticks)", 20)
				.getInt();
		this.blindnessDuration = config
				.get(this.name, "How long does my Blindness effect last? (default 20 ticks)", 20).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}
}
