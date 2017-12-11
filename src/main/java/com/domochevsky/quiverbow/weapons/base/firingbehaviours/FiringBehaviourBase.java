package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import com.domochevsky.quiverbow.weapons.base._WeaponBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class FiringBehaviourBase<W extends _WeaponBase> implements IFiringBehaviour
{   
    protected final W weapon;
    
    protected FiringBehaviourBase(W weapon)
    {
	this.weapon = weapon;
    }
    
    @Override
    public void update(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) {}
    
    @Override
    public void onStopFiring(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {}
}
