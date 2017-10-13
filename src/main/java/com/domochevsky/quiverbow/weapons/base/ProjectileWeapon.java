package com.domochevsky.quiverbow.weapons.base;

import com.domochevsky.quiverbow.weapons.base.firingbehaviours.IFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ProjectileWeapon extends _WeaponBase
{
    protected IFiringBehaviour firingBehaviour = new IFiringBehaviour()
    {
	@Override
	public void update(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) {System.out.println("No firing behavior set");}

	@Override
	public void fire(ItemStack stack, World world, Entity entity) {System.out.println("No firing behavior set");}
    };
    
    public ProjectileWeapon(String name, int maxAmmo)
    {
	super(name, maxAmmo);
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	if (this.getDamage(stack) >= stack.getMaxDamage())
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // Is empty

	firingBehaviour.fire(stack, world, player);
	
	return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
    }
    
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
    {
        super.onUpdate(stack, world, entity, animTick, holdingItem);
	firingBehaviour.update(stack, world, entity, animTick, holdingItem);
    }

    public void doFireFX(World world, Entity entity)
    {

    }

    public void setFiringBehaviour(IFiringBehaviour firingBehaviour)
    {
	this.firingBehaviour = firingBehaviour;
    }

    public IFiringBehaviour getFiringBehaviour()
    {
	return firingBehaviour;
    }
}
