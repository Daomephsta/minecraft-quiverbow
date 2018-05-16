package com.domochevsky.quiverbow.util;

import com.domochevsky.quiverbow.QuiverbowMain;
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
		item.setRegistryName(QuiverbowMain.MODID + ":" + name);
		item.setUnlocalizedName(QuiverbowMain.MODID + infix + name);
		return item;
	}

	public static Block registerBlock(Block block, String name)
	{
		return registerBlock(block, ".", name);
	}

	public static Block registerBlock(Block block, String infix, String name)
	{
		block.setRegistryName(QuiverbowMain.MODID + ":" + name);
		block.setUnlocalizedName(QuiverbowMain.MODID + infix + name);
		return block;
	}
}
