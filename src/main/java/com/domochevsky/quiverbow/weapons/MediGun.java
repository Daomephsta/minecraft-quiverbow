package com.domochevsky.quiverbow.weapons;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.RecipeSorter;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.HealthBeam;
import com.domochevsky.quiverbow.recipes.Recipe_RayOfHope_Reload;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MediGun extends _WeaponBase
{
	public MediGun() { super("ray_of_hope", 320); }	// 20 per regen potion, for 2x 8 potions (or 1x 8 Regen 2 potions)

	


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/MediGun");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/MediGun_Empty");
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
		// Good to go (already verified)

		// SFX
		entity.worldObj.playSoundAtEntity(entity, "random.fizz", 0.7F, 1.4F);

		HealthBeam beam = new HealthBeam(entity.worldObj, entity, (float) this.Speed);

		beam.ignoreFrustumCheck = true;
		beam.ticksInAirMax = 40;

		entity.worldObj.spawnEntityInWorld(beam); 	// Firing!

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.Speed = config.get(this.name, "How fast are my beams? (default 5.0 BPT (Blocks Per Tick))", 5.0).getDouble();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false. They don't know what friends are.)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// Use a beacon for this (+ obsidian, tripwire hook... what else)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "bi ", "ico", " ot",
					'b', Blocks.beacon,
					'o', Blocks.obsidian,
					't', Blocks.tripwire_hook,
					'c', Items.cauldron,
					'i', Items.iron_ingot
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		RecipeSorter.register("quiverchevsky:recipehandler_roh_reload", Recipe_RayOfHope_Reload.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

		ArrayList list = new ArrayList();

		list.add(new ItemStack(Items.potionitem, 1, 8193));
		list.add(new ItemStack(Items.potionitem, 1, 8225));

		GameRegistry.addRecipe(new Recipe_RayOfHope_Reload(new ItemStack(this), list, new ItemStack(Items.potionitem, 1, 8193), new ItemStack(Items.potionitem, 1, 8225)));
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "MediGun_empty"; }		// empty
		if (this.getCooldown(stack) > 0) { return "MediGun_hot"; }	// Cooling down

		return "MediGun";	// Regular
	}
}
