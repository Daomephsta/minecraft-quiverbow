package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.FenGoop;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class FenFire extends WeaponBase
{
	public FenFire()
	{
		super("fen_fire", 32);
		this.setCreativeTab(CreativeTabs.TOOLS); // Tool, so on the tool tab
		setFiringBehaviour(new SingleShotFiringBehaviour<FenFire>(this, (world, weaponStack, entity, data, properties) ->
		{
			FenGoop projectile = new FenGoop(world, entity, properties.getProjectileSpeed());
			projectile.fireDuration = properties.getInt(CommonProperties.PROP_FIRE_DUR_ENTITY);
			projectile.lightTick = properties.getInt(CommonProperties.PROP_DESPAWN_TIME);

			return projectile;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ARROW_SHOOT, 0.7F, 0.3F);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.8F, 2.0F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().projectileSpeed(1.5F).cooldown(20)
				.intProperty(CommonProperties.PROP_FIRE_DUR_ENTITY, CommonProperties.COMMENT_FIRE_DUR_ENTITY, 1)
				.intProperty(CommonProperties.PROP_DESPAWN_TIME,
						"How long fen lights stay lit in ticks. Set to 0 for infinite time", 0)
				.build();
	}
}
