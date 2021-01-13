package com.domochevsky.quiverbow.miscitems;

import java.util.List;

import com.domochevsky.quiverbow.QuiverbowMain;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class QuiverBowItem extends Item
{
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(I18n.format(getUnlocalizedName() + ".description"));
    }

    public void addRecipes()
    {} // Called once after all items have been registered and initialized

    @Override
    public CreativeTabs getCreativeTab()
    {
        return QuiverbowMain.QUIVERBOW_TAB;
    }
}
