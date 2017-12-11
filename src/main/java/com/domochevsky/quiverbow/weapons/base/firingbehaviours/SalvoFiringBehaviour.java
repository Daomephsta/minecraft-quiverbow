package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class SalvoFiringBehaviour<W extends _WeaponBase> extends ProjectileFiringBehaviour<W>
{
    public static class SalvoData implements IProjectileData
    {
	public final int shotCount;

	public SalvoData(int shotCount)
	{
	    this.shotCount = shotCount;
	}
    }
    
    private final int shotQuantity;

    public SalvoFiringBehaviour(W weapon, int shotQuantity, IProjectileFactory projectileFactory)
    {
	super(weapon, projectileFactory);
	this.shotQuantity = shotQuantity;
    }

    @Override
    public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
    {
	if (weapon.getCooldown(stack) > 0)
	{
	    return;
	} // Hasn't cooled down yet

	Helper.knockUserBack(entity, weapon.Kickback); // Kickback
	
	weapon.setCooldown(stack, weapon.Cooldown); // Cooling down now

	for(int shot = 0; shot < shotQuantity; shot++)
	{
	    if(!world.isRemote)
		world.spawnEntity(projectileFactory.createProjectile(world, stack, entity, new SalvoData(shot)));
	    
	    weapon.doFireFX(world, entity);

	    if (weapon instanceof MagazineFedWeapon && weapon.consumeAmmo(stack, entity, 1))
	    {
		((MagazineFedWeapon) weapon).dropMagazine(world, stack, entity);
		return;
	    }
	    // else, still has ammo left. Continue.
	}
    }

    @Override
    public void update(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) {}
}
