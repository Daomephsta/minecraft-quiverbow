package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class SingleShotFiringBehaviour<W extends WeaponBase> extends ProjectileFiringBehaviour<W>
{
	private final int ammoCost;

	public SingleShotFiringBehaviour(W weapon, IProjectileFactory projectileFactory)
	{
		this(weapon, 1, projectileFactory);
	}

	public SingleShotFiringBehaviour(W weapon, int ammoCost, IProjectileFactory projectileFactory)
	{
		super(weapon, projectileFactory);
		this.ammoCost = ammoCost;
	}

	@Override
	public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
	{
		if (weapon.getCooldown(stack) > 0) return;
		
		Helper.knockUserBack(entity, weapon.getKickback());

		if (!world.isRemote) world.spawnEntity(projectileFactory.createProjectile(world, stack, entity, null, weapon.getProperties()));

		weapon.doFireFX(world, entity);

		weapon.resetCooldown(stack);
		if (weapon.consumeAmmo(stack, entity, ammoCost) && weapon instanceof MagazineFedWeapon)
		{
			((MagazineFedWeapon) weapon).dropMagazine(world, stack, entity);
		}
	}

	@Override
	public void update(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
	{

	}
}
