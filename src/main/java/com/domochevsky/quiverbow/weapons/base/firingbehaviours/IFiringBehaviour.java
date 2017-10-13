package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IFiringBehaviour
{   
    public void fire(ItemStack stack, World world, Entity entity);
    
    public void update(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem);
}
