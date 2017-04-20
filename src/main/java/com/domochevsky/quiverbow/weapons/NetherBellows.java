package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.LargeNetherrackMagazine;
import com.domochevsky.quiverbow.projectiles.NetherFire;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NetherBellows extends _WeaponBase
{
	public NetherBellows()
	{
		super("nether_bellows", 200);

		ItemStack ammo = Helper.getAmmoStack(LargeNetherrackMagazine.class, 0);
		this.setMaxDamage(ammo.getMaxDamage());	// Fitting our max capacity to the magazine
	}


	

	private int Dmg;
	private int FireDur;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/NetherBellows");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/NetherBellows_Empty");
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
		// SFX
		entity.worldObj.playSoundAtEntity(entity, "random.fizz", 1.0F, 0.3F);

		this.setCooldown(stack, this.Cooldown);

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
		// Firing
		float spreadHor = world.rand.nextFloat() * 20 - 10;		// Spread between -10 and 10
		float spreadVert = world.rand.nextFloat() * 20 - 10;

		NetherFire shot = new NetherFire(world, entity, (float) this.Speed, spreadHor, spreadVert);
		shot.damage = this.Dmg;
		shot.fireDuration = this.FireDur;

		world.spawnEntityInWorld(shot);
	}


	private void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 60);
			return;
		}

		ItemStack clipStack = Helper.getAmmoStack(LargeNetherrackMagazine.class, stack.getItemDamage());	// Unloading all ammo into that clip

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
		this.Speed = config.get(this.name, "How fast are my projectiles? (default 0.75 BPT (Blocks Per Tick))", 0.75).getDouble();
		this.Dmg = config.get(this.name, "What damage am I dealing per projectile? (default 1)", 1).getInt();
		this.FireDur = config.get(this.name, "For how long do I set things on fire? (default 3 sec)", 3).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One redstone sprayer (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "zxz", "zbz", "cya",
					'x', Blocks.piston,
					'y', Blocks.tripwire_hook,
					'z', Blocks.obsidian,
					'a', Items.repeater,
					'b', Blocks.sticky_piston,
					'c', Items.flint_and_steel
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		Helper.registerAmmoRecipe(LargeNetherrackMagazine.class, this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "NetherBellows_empty"; }
		if (this.getCooldown(stack) > 0) { return "NetherBellows_hot"; }

		return "NetherBellows";
	}
}
