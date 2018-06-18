package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.LapisShot;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class LapisCoil extends MagazineFedWeapon
{
	private static final String PROP_WEAKNESS_STRENGTH = "weaknessStrength";
	private static final String PROP_WEAKNESS_DUR = "weaknessDur";
	private static final String PROP_HUNGER_STRENGTH = "hungerStrength";
	private static final String PROP_HUNGER_DUR = "hungerDur";

	public LapisCoil(AmmoBase ammo)
	{
		super("lapis_coil", ammo, 100);
		setFiringBehaviour(new SingleShotFiringBehaviour<LapisCoil>(this, (world, weaponStack, entity, data, properties) ->
		{
			// Projectile
			LapisShot projectile = new LapisShot(world, entity, properties.getProjectileSpeed(),
					new PotionEffect(MobEffects.NAUSEA, properties.getInt(CommonProperties.PROP_NAUSEA_DUR), 1),
					new PotionEffect(MobEffects.HUNGER, properties.getInt(PROP_HUNGER_DUR), properties.getInt(PROP_HUNGER_STRENGTH)),
					new PotionEffect(MobEffects.WEAKNESS, properties.getInt(PROP_WEAKNESS_DUR), properties.getInt(PROP_WEAKNESS_STRENGTH)));
			projectile.damage = Helper.randomIntInRange(world.rand, getProperties().getDamageMin(), getProperties().getDamageMax());

			projectile.ticksInGroundMax = 100; // 5 sec before it disappears

			return projectile;
		}));
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
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(1).maximumDamage(3).projectileSpeed(2.5F).cooldown(4)
				.mobUsable()
				.intProperty(PROP_WEAKNESS_STRENGTH, "The strength of the Weakness effect applied", 2)
				.intProperty(PROP_WEAKNESS_DUR, "The duration in ticks of the Weakness effect applied", 40)
				.intProperty(CommonProperties.PROP_NAUSEA_DUR, CommonProperties.COMMENT_NAUSEA_DUR, 40)
				.intProperty(PROP_HUNGER_STRENGTH, "The strength of the Hunger effect applied", 2)
				.intProperty(PROP_HUNGER_DUR, "The duration in ticks of the Hunger effect applied", 40).build();
	}
}