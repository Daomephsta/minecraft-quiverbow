package com.domochevsky.quiverbow.weapons.base;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.ProjectileFiringBehaviour.IProjectileFactory;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class WeaponCrossbow extends WeaponBase
{
	public WeaponCrossbow(String name, int maxAmmo)
	{
		super(name, maxAmmo);
	}

	public WeaponCrossbow(String name, int maxAmmo, IProjectileFactory projectileFactory)
	{
		super(name, maxAmmo);
		setFiringBehaviour(new SingleShotFiringBehaviour<WeaponCrossbow>(this, projectileFactory));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 0.5F);
	}
}
