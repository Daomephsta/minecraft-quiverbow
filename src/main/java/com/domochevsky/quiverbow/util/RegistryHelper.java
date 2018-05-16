package com.domochevsky.quiverbow.util;

import com.domochevsky.quiverbow.Quiverbow;
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
		item.setRegistryName(Quiverbow.MODID + ":" + name);
		item.setUnlocalizedName(Quiverbow.MODID + infix + name);
		return item;
	}

	public static Block registerBlock(Block block, String name)
	{
		return registerBlock(block, ".", name);
	}

	public static Block registerBlock(Block block, String infix, String name)
	{
		block.setRegistryName(Quiverbow.MODID + ":" + name);
		block.setUnlocalizedName(Quiverbow.MODID + infix + name);
		return block;
	}
}
