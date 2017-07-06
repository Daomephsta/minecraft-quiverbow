package com.domochevsky.quiverbow.miscitems;

import java.util.Collections;
import java.util.List;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.util.Newliner;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class QuiverBowItem extends Item
{
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean unknown)
    {
	Collections.addAll(list, Newliner.translateAndParse(getUnlocalizedName() + ".description"));
    }

    public void addRecipes()
    {} // Called once after all items have been registered and initialized
    
    @Override
    public CreativeTabs getCreativeTab()
    {
        return Main.QUIVERBOW_TAB;
    }
}
