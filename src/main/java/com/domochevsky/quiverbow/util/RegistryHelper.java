package com.domochevsky.quiverbow.util;

import com.domochevsky.quiverbow.Main.Constants;
import com.domochevsky.quiverbow.miscitems.QuiverBowItem;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class RegistryHelper
{
    public static Item registerItem(QuiverBowItem item, String name)
    {
	return registerItem(item, ".", name);
    }

    public static Item registerItem(QuiverBowItem item, String infix, String name)
    {
	item.setRegistryName(Constants.MODID + ":" + name);
	item.setUnlocalizedName(Constants.MODID + infix + name);
	return item;
    }

    public static Block registerBlock(Block block, String name)
    {
	return registerBlock(block, ".", name);
    }

    public static Block registerBlock(Block block, String infix, String name)
    {
	block.setRegistryName(Constants.MODID + ":" + name);
	block.setUnlocalizedName(Constants.MODID + infix + name);
	return block;
    }
}
