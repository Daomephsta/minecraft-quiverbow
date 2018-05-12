package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BurstFiringBehaviour<W extends MagazineFedWeapon> extends ProjectileFiringBehaviour<W>
{
	public BurstFiringBehaviour(W weapon, IProjectileFactory projectileFactory)
	{
		super(weapon, projectileFactory);
	}

	@Override
	public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
	{}

	@Override
	public void update(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
	{
		if (entity instanceof EntityLivingBase && weapon.getBurstFire(stack) <= 0)
			return;
		weapon.setBurstFire(stack, weapon.getBurstFire(stack) - 1); // One
		// done

		if (stack.getItemDamage() < stack.getMaxDamage() && holdingItem)
		{
			this.doBurstFire(stack, world, (EntityLivingBase) entity);

			if (weapon.consumeAmmo(stack, entity, 1))
			{
				weapon.dropMagazine(world, stack, entity);
			} // You're empty
		}
		// else, either not loaded or not held
	}

	protected void doBurstFire(ItemStack stack, World world, EntityLivingBase entity)
	{

	}
}
