package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.BoxOfFlintDust;
import com.domochevsky.quiverbow.projectiles.FlintDust;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FlintDuster  extends _WeaponBase
{
	public FlintDuster()
	{
		super("flint_duster", 256);
		this.setCreativeTab(CreativeTabs.tabTools);		// Tool, so on the tool tab
	}

	

	private int Dmg;
	private int MaxBlocks;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/FlintDrill");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/FlintDrill_Empty");
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) { return stack; }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return stack; }	// Is empty

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return stack;
	}


	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		// Ignoring cooldown for firing purposes

		// SFX
		world.playSoundAtEntity(entity, "mob.bat.takeoff", 0.5F, 0.6F);

		// Ready
		FlintDust shot = new FlintDust(world, entity, (float) this.Speed);

		// Properties
		shot.damage = this.Dmg;
		shot.ticksInAirMax = this.MaxBlocks;

		// Go
		world.spawnEntityInWorld(shot);

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, 4);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.Speed = 1.5f;	// Fixed value

		this.Dmg = config.get(this.name, "What damage am I dealing? (default 1)", 1).getInt();
		this.MaxBlocks = config.get(this.name, "How much range do I have? (default ~7 blocks)", 7).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false. They have no interest in dirt.)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Flint Duster (Empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "qhq", "qpq", "tsi",
					'p', Blocks.piston,
					's', Blocks.sticky_piston,
					'h', Blocks.hopper,
					'q', Blocks.quartz_block,
					'i', Items.iron_ingot,
					't', Blocks.tripwire_hook
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		ItemStack stack = Helper.getAmmoStack(BoxOfFlintDust.class, 0);
		GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(stack.getItem(), 32));
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "FlintDuster_empty"; }		// empty
		if (this.getCooldown(stack) > 0) { return "FlintDuster_hot"; }	// Firing

		return "FlintDuster";	// Regular
	}
}
