package com.domochevsky.quiverbow;

import com.domochevsky.quiverbow.projectiles._ProjectileBase;

import net.minecraft.item.Item;

public class CommonProxy
{
	public void registerItemProjectileRenderer(Class<? extends _ProjectileBase> entityClass, final Item item)
	{}

	public void registerTurretRenderer()
	{}
}
