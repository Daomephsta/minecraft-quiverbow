package com.domochevsky.quiverbow.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryHelper
{
    public static boolean hasItem(EntityPlayer player, Item item, int amount)
    {
	for(int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
	{
	    ItemStack stack = player.inventory.getStackInSlot(slot);
	    if(stack != null && stack.getItem() == item && stack.stackSize >= amount)
		return true;
	}
	return false;
    }

    public static boolean hasBlock(EntityPlayer player, Block block, int amount)
    {
	return hasItem(player, Item.getItemFromBlock(block), amount);
    }

    //Omnomnom
    public static boolean consumeItem(EntityPlayer player, Item item, int amount)
    {
	for(int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
	{
	    ItemStack stack = player.inventory.getStackInSlot(slot);
	    if(stack != null && stack.getItem() == item)
	    {
		if(stack.stackSize - amount < 0)
		{
		    return false;
		}
		else
		{
		    stack.stackSize -= amount;
		    if(stack.stackSize <= 0) player.inventory.setInventorySlotContents(slot, null);
		    return true;
		}
	    }
	}
	return false;
    }

    //Omnomnom 2: Electric Boogaloo
    public static boolean consumeBlock(EntityPlayer player, Block block, int amount)
    {
	return consumeItem(player, Item.getItemFromBlock(block), amount);
    }
}
