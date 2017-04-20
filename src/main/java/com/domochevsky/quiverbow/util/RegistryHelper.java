package com.domochevsky.quiverbow.util;

import com.domochevsky.quiverbow.Main.Constants;
import com.domochevsky.quiverbow.miscitems.QuiverBowItem;

import cpw.mods.fml.common.registry.GameRegistry;
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
	item.setTextureName(Constants.MODID + ":" + (item.getIconPath() == null ? name : item.getIconPath()));
	item.setUnlocalizedName(Constants.MODID + infix + name);
	GameRegistry.registerItem(item, name);
	return item;
    }
    
    public static Block registerBlock(Block block, String name)
    {
	return registerBlock(block, ".", name);
    }
    
    public static Block registerBlock(Block block, String infix, String name)
    {
	block.setBlockTextureName(Constants.MODID + ":" + name);
	block.setBlockName(Constants.MODID + infix + name);
	GameRegistry.registerBlock(block, name);
	return block;
    }
}
