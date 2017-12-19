package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import javax.annotation.Nullable;

import com.domochevsky.quiverbow.projectiles._ProjectileBase;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class ProjectileFiringBehaviour<W extends _WeaponBase> extends FiringBehaviourBase<W>
{
	public static interface IProjectileFactory
	{
		public _ProjectileBase createProjectile(World world, ItemStack weaponStack, Entity entity, @Nullable IProjectileData data);
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
