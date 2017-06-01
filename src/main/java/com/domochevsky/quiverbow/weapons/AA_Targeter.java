package com.domochevsky.quiverbow.weapons;

import java.util.Collections;
import java.util.List;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.util.Newliner;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public class AA_Targeter extends _WeaponBase
{
	public AA_Targeter() 
	{ 
		super("aa_target_assist", 1); 
		this.setCreativeTab(CreativeTabs.tabTools);	// This is a tool
	}	// Not consuming ammo	
	
	
	
	public double targetingDistance = 64;
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister) { this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/AA_Targeter"); }
	
	
	@Override								
	public IIcon getIconFromDamage(int meta) { return this.Icon; }
	
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) 
	{
		if (world.isRemote) { return stack; }	// Not doing this on client side
		
		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		
		return stack;
	}
	
	
	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side, mob usable
	{
		this.setDamage(stack, 1);	// Set to be firing
		this.setCooldown(stack, 4);	// 4 ticks
		
		// SFX
        world.playSoundAtEntity(entity, "random.click", 0.3F, 2.0F);
	}
	
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) 	// Overhauled default
	{
		if (world.isRemote) { return; }	// Not doing this on client side
		
		if (this.getCooldown(stack) > 0) // Active right now, so ticking down
		{ 
			this.setCooldown(stack, this.getCooldown(stack) - 1); 
			
			if (this.getCooldown(stack) >= 0) { this.setDamage(stack, 0); }	// Back to inactive
		}
	}
	
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) { return false; }	// No point in showing the bar for this
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
	    Collections.addAll(list, Newliner.translateAndParse(getUnlocalizedName() + ".description"));
	}
	
	
	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config) 
	{ 
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);
		
		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false. They're not friends with AAs.)", false).getBoolean(true);
	}
    
	
	@Override
    public void addRecipes() 
	{ 
		if (Enabled)
        {
			GameRegistry.addRecipe(new ItemStack(this), "bi ", "iri", " it",
            		'b', Blocks.noteblock,
            		'r', Items.repeater,
            		't', Blocks.tripwire_hook,
            		'i', Items.iron_ingot
            );
        }
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list)
	{
		list.add(new ItemStack(item, 1, 0));
	}
	
	
	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{ 
		return "AATH";	// Regular
	}
}
