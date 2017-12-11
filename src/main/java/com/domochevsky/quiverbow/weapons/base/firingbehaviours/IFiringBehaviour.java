package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public interface IFiringBehaviour
{   
    public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand);
    
    public void update(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem);
    
    public void onStopFiring(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft);
}
