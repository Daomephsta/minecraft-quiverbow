package com.domochevsky.quiverbow.weapons.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author Daomephsta
 * Interface for all weapons that have a scope and hence zoom capabilities
 */
public interface IScopedWeapon
{
	public int getMaxZoom();
	
	public boolean shouldZoom(World world, EntityPlayer player, ItemStack stack);
}
