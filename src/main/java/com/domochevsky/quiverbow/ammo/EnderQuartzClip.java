package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.util.InventoryHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EnderQuartzClip extends AmmoMagazine
{
	public EnderQuartzClip()
	{
	    	super();
		this.setMaxDamage(8);
		this.setCreativeTab(CreativeTabs.tabCombat);	// On the combat tab by default, since this is amunition
	}
	
	
	@Override
	public void addRecipes() 
	{
		GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "xxx", "ixi", "iii",
		         'x', Items.quartz, 
		         'i', Items.iron_ingot
		 );
	}	


	@Override
	public String getIconPath()
	{
	    return "EnderAmmo";
	}
	
	@Override
	protected boolean hasComponentItems(EntityPlayer player, int amount)
	{
	    return InventoryHelper.hasItem(player, Items.ender_pearl, amount) && InventoryHelper.hasItem(player, Items.quartz, amount);
	}
	
	@Override
	protected boolean consumeComponentItems(EntityPlayer player, int amount)
	{
	    player.getEntityWorld().playSoundAtEntity(player, "random.wood_click", 0.5F, 0.3F);
	    return InventoryHelper.consumeItem(player, Items.ender_pearl, amount) && InventoryHelper.consumeItem(player, Items.quartz, amount);
	}
}
