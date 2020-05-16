package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.OSPShot;
import com.domochevsky.quiverbow.projectiles.ProjectileBase;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class OSP extends MagazineFedWeapon
{
	public OSP(AmmoBase ammo)
	{
		super("splinter_pistol", ammo, 16);
		setFiringBehaviour(new SingleShotFiringBehaviour<OSP>(this, (world, weaponStack, entity, data, properties) ->
		{
			ProjectileBase projectile = new OSPShot(world, entity, properties.getProjectileSpeed(),
					new PotionEffect(MobEffects.WITHER, properties.getInt(CommonProperties.PROP_WITHER_DUR), properties.getInt(CommonProperties.PROP_WITHER_STRENGTH)));

			int dmg_range = properties.getDamageMax() - properties.getDamageMin();
			int dmg = properties.getDamageMin() + world.rand.nextInt(dmg_range + 1);

			projectile.damage = dmg;
			return projectile;
		}));
	}

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 0.3F, 0.4F);
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXPLODE, 0.4F, 1.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity, EnumParticleTypes.SMOKE_NORMAL,
				(byte) 1); // smoke
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(4).maximumDamage(8).projectileSpeed(1.7F).cooldown(15)
				.mobUsable()
				.intProperty(CommonProperties.PROP_WITHER_STRENGTH, CommonProperties.COMMENT_WITHER_STRENGTH, 1)
				.intProperty(CommonProperties.PROP_WITHER_DUR, CommonProperties.COMMENT_WITHER_DUR, 61).build();
	}
}
