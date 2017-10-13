package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import javax.annotation.Nullable;

import com.domochevsky.quiverbow.projectiles._ProjectileBase;
import com.domochevsky.quiverbow.weapons.base.ProjectileWeapon;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class ProjectileFiringBehaviour<W extends ProjectileWeapon> extends FiringBehaviourBase<W>
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
