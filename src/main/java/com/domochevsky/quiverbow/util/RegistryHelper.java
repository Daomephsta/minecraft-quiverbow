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
	item.setTextureName(Constants.MODID + ":" + item.getIconPath() == null ? name : item.getIconPath());
	item.setUnlocalizedName(Constants.MODID + ".misc." + name);
	GameRegistry.registerItem(item, name);
	return item;
    }
    
    public static Block registerBlock(Block block, String name)
    {
	block.setBlockTextureName(Constants.MODID + ":" + name);
	block.setBlockName(Constants.MODID + ".misc." + name);
	GameRegistry.registerBlock(block, name);
	return block;
    }
}
