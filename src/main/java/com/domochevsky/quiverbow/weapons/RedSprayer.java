package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.LargeRedstoneMagazine;
import com.domochevsky.quiverbow.projectiles.RedSpray;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RedSprayer extends _WeaponBase
{
	public RedSprayer()
	{
		super("redstone_sprayer", 200);

		ItemStack ammo = Helper.getAmmoStack(LargeRedstoneMagazine.class, 0);
		this.setMaxDamage(ammo.getMaxDamage());	// Fitting our max capacity to the magazine
	}

	

	private int Wither_Strength;
	private int Wither_Duration;
	private int Blindness_Duration;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/RedSprayer");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/RedSprayer_Empty");
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) { return stack; }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return stack; }	// Is empty

		if (player.isSneaking())	// Dropping the magazine
		{
			this.dropMagazine(world, stack, player);
			return stack;
		}

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return stack;
	}


	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		this.setCooldown(stack, this.Cooldown);

		// SFX
		entity.worldObj.playSoundAtEntity(entity, "random.fizz", 0.7F, 1.5F);

		int counter = 0;

		while (counter < 5)
		{
			this.fireSingle(world, entity);

			if (this.consumeAmmo(stack, entity, 1)) 	// We're done here
			{
				this.dropMagazine(world, stack, entity);
				return;
			}
			// else, still has ammo left. Continue.

			counter += 1;
		}
	}


	private void fireSingle(World world, Entity entity)
	{
		// Spread
		float spreadHor = world.rand.nextFloat() * 20 - 10;								// Spread between -10 and 10
		float spreadVert = world.rand.nextFloat() * 20 - 10;

		RedSpray shot = new RedSpray(entity.worldObj, entity, (float) this.Speed, spreadHor, spreadVert, new PotionEffect(Potion.wither.id, this.Wither_Duration, this.Wither_Strength), new PotionEffect(Potion.blindness.id, this.Blindness_Duration, 1));

		entity.worldObj.spawnEntityInWorld(shot);
	}


	private void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 40);
			return;
		}

		ItemStack clipStack = Helper.getAmmoStack(LargeRedstoneMagazine.class, stack.getItemDamage());	// Unloading all ammo into that clip

		stack.setItemDamage(this.getMaxDamage());	// Emptying out

		// Creating the clip
		EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
		entityitem.delayBeforeCanPickup = 10;

		// And dropping it
		if (entity.captureDrops) { entity.capturedDrops.add(entityitem); }
		else { world.spawnEntityInWorld(entityitem); }

		// SFX
		world.playSoundAtEntity(entity, "random.break", 1.0F, 0.5F);
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.Speed = config.get(this.name, "How fast are my projectiles? (default 0.5 BPT (Blocks Per Tick))", 0.5).getDouble();

		this.Wither_Strength = config.get(this.name, "How strong is my Wither effect? (default 2)", 2).getInt();
		this.Wither_Duration = config.get(this.name, "How long does my Wither effect last? (default 20 ticks)", 20).getInt();
		this.Blindness_Duration = config.get(this.name, "How long does my Blindness effect last? (default 20 ticks)", 20).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One redstone sprayer (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "zxz", "aba", "zyz",
					'x', Blocks.piston,
					'y', Blocks.tripwire_hook,
					'z', Items.iron_ingot,
					'a', Items.repeater,
					'b', Blocks.sticky_piston
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		Helper.registerAmmoRecipe(LargeRedstoneMagazine.class, this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "RedSprayer_empty"; }
		if (this.getCooldown(stack) > 0) { return "RedSprayer_hot"; }

		return "RedSprayer";
	}
}
