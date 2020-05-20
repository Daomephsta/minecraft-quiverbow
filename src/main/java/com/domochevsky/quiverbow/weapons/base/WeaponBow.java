package com.domochevsky.quiverbow.weapons.base;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class WeaponBow extends WeaponBase
{
	public WeaponBow(String name, int maxAmmo)
	{
		super(name, maxAmmo);
		// Copied from ItemBow L29-L44 and modified
		this.addPropertyOverride(new ResourceLocation("pull"), (ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) ->
    	{
    		if(entity == null) return 0.0F;
    		else
    		{
    			if(entity.getActiveItemStack().getItem() instanceof WeaponBow)
    				return stack.getMaxItemUseDuration() - entity.getItemInUseCount() / 20.0F;
    			else return 0.0F;
    		}
    	});
		this.addPropertyOverride(new ResourceLocation("pulling"), (ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) ->
		{
				return entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack
				        ? 1.0F
						: 0.0F;
		});
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.BOW;
	}
}
