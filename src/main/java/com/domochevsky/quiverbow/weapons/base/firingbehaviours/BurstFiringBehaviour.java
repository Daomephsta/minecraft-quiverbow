package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BurstFiringBehaviour<W extends MagazineFedWeapon> extends ProjectileFiringBehaviour<W>
{
    public BurstFiringBehaviour(W weapon, IProjectileFactory projectileFactory)
    {
	super(weapon, projectileFactory);
    }

    @Override
    public void fire(ItemStack stack, World world, Entity entity)
    {
    }

    @Override
    public void update(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
    {
	if (weapon.getBurstFire(stack) > 0)
	{
	    weapon.setBurstFire(stack, weapon.getBurstFire(stack) - 1); // One done

	    if (stack.getItemDamage() < stack.getMaxDamage() && holdingItem) // Can
		// only
		// do
		// it
		// if
		// we're
		// loaded
		// and
		// holding
		// the
		// weapon
	    {
		this.doBurstFire(stack, world, entity);

		if (weapon.consumeAmmo(stack, entity, 1))
		{
		    weapon.dropMagazine(world, stack, entity);
		} // You're empty
	    }
	    // else, either not loaded or not held
	}
    }

    protected void doBurstFire(ItemStack stack, World world, Entity entity)
    {

    }
}
