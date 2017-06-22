package com.domochevsky.quiverbow.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryHelper
{
    public static boolean hasItem(EntityPlayer player, Item item, int amount)
    {
	for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
	{
	    ItemStack stack = player.inventory.getStackInSlot(slot);
	    if (!stack.isEmpty() && stack.getItem() == item && stack.getCount() >= amount) return true;
	}
	return false;
    }

    public static boolean hasBlock(EntityPlayer player, Block block, int amount)
    {
	return hasItem(player, Item.getItemFromBlock(block), amount);
    }

    // Omnomnom
    public static boolean consumeItem(EntityPlayer player, Item item, int amount)
    {
	for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
	{
	    ItemStack stack = player.inventory.getStackInSlot(slot);
	    if (!stack.isEmpty() && stack.getItem() == item)
	    {
		if (stack.getCount() - amount < 0)
		{
		    return false;
		}
		else
		{
		    stack.shrink(amount);
		    if (stack.getCount() <= 0) player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
		    return true;
		}
	    }
	}
	return false;
    }

    // Omnomnom 2: Electric Boogaloo
    public static boolean consumeBlock(EntityPlayer player, Block block, int amount)
    {
	return consumeItem(player, Item.getItemFromBlock(block), amount);
    }

    // Utils for interacting with and checking both hands

    public static ItemStack findItemInHands(EntityPlayer player, Item item)
    {
	ItemStack mainHand = player.getHeldItemMainhand();
	ItemStack offHand = player.getHeldItemOffhand();

	if (mainHand.getItem() == item && offHand.getItem() == item)
	    return mainHand;
	else if (mainHand.getItem() == item)
	    return mainHand;
	else if (offHand.getItem() == item)
	    return offHand;
	else return ItemStack.EMPTY;
    }

    public static ItemStack findItemInHandsByClass(EntityPlayer player, Class<? extends Item> clazz)
    {
	ItemStack mainHand = player.getHeldItemMainhand();
	ItemStack offHand = player.getHeldItemOffhand();

	if (clazz.isInstance(mainHand.getItem()) && clazz.isInstance(offHand.getItem()))
	    return mainHand;
	else if (clazz.isInstance(mainHand.getItem()))
	    return mainHand;
	else if (clazz.isInstance(offHand.getItem()))
	    return offHand;
	else return ItemStack.EMPTY;
    }
}
