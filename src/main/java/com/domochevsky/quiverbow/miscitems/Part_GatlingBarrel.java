package com.domochevsky.quiverbow.miscitems;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.domochevsky.quiverbow.Main.Constants;
import com.domochevsky.quiverbow.util.Newliner;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Part_GatlingBarrel extends QuiverBowItem
{	
	public Part_GatlingBarrel()
	{
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.tabMaterials);	// On the combat tab by default, but this isn't ammo. It's a building part
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
	    	Collections.addAll(list, Newliner.translateAndParse(Constants.MODID + ".parts.gatling_barrel.description"));
	}
	
	
	@Override
	public void addRecipes() 
	{ 
        // Sugar Gatling, barrel
        // Piston accelerators? Sticky, regular + iron walls
        GameRegistry.addRecipe(new ItemStack(this), "i i", "ipi", "isi",
                'i', Items.iron_ingot,
                'p', Blocks.piston,
                's', Blocks.sticky_piston
    	);
	}
	
	@Override
	public String getIconPath()
	{
	    return "misc/Part_SEBarrel";
	}
}
