package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import javax.annotation.Nullable;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class ProjectileFiringBehaviour<W extends WeaponBase> extends FiringBehaviourBase<W>
{
	public static interface IProjectileFactory
	{
		public Entity createProjectile(World world, ItemStack weaponStack, EntityLivingBase entity, @Nullable IProjectileData data, WeaponProperties properties);
	}

	public static interface IProjectileData
	{

	}

	protected final IProjectileFactory projectileFactory;

	protected ProjectileFiringBehaviour(W weapon, IProjectileFactory projectileFactory)
	{
		super(weapon);
		this.projectileFactory = projectileFactory;
	}
}
