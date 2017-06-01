package com.domochevsky.quiverbow.weapons;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.SeedJar;
import com.domochevsky.quiverbow.projectiles.Seed;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SeedSweeper extends _WeaponBase
{
	public SeedSweeper() { super("seed_sweeper", 512); }

	

	private int Dmg;
	private float Spread;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/SeedSweeper");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/SeedSweeper_Empty");
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
		if (this.getCooldown(stack) != 0) { return; }	// Hasn't cooled down yet

		// SFX
		world.playSoundAtEntity(entity, "random.break", 1.6F, 0.9F);

		this.setCooldown(stack, this.Cooldown);	// Cooling down now, no matter what

		int counter = 8;

		while (counter > 0 && this.getDamage(stack) < this.getMaxDamage())	// Keep firing until you have done so 8 times or run out of seeds
		{
			this.fireShot(world, entity);

			if (this.consumeAmmo(stack, entity, 1)) 	// We're done here
			{
				this.dropMagazine(world, stack, entity);
				return;
			}
			// else, still has ammo left. Continue.

			counter -= 1;
		}

		if (this.getDamage(stack) >= this.getMaxDamage()) { this.dropMagazine(world, stack, entity); }
	}


	private void fireShot(World world, Entity entity)
	{
		float spreadHor = world.rand.nextFloat() * this.Spread - (this.Spread / 2);
		float spreadVert = world.rand.nextFloat() * this.Spread - (this.Spread / 2);

		Seed shot = new Seed(world, entity, (float) this.Speed, spreadHor, spreadVert);
		shot.damage = this.Dmg;

		world.spawnEntityInWorld(shot); 											// Firing
	}


	private void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 40);
			return;
		}

		ItemStack clipStack = Helper.getAmmoStack(SeedJar.class, stack.getItemDamage());	// Unloading all ammo into that clip

		stack.setItemDamage(this.getMaxDamage());	// Emptying out

		// Creating the clip
		EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
		entityitem.delayBeforeCanPickup = 10;

		// And dropping it
		if (entity.captureDrops) { entity.capturedDrops.add(entityitem); }
		else { world.spawnEntityInWorld(entityitem); }

		// SFX
		world.playSoundAtEntity(entity, "random.click", 1.7F, 0.3F);
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.Dmg = config.get(this.name, "What damage am I dealing per projectile? (default 1)", 1).getInt();
		this.Cooldown = config.get(this.name, "How long until I can fire again? (default 15 ticks)", 15).getInt();

		this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.6 BPT (Blocks Per Tick))", 1.6).getDouble();
		this.Spread = (float) config.get(this.name, "How accurate am I? (default 26 spread)", 26).getDouble();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Seed Sweeper (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), " i ", "ipi", " it",
					'p', Blocks.piston,
					'i', Items.iron_ingot,
					't', Blocks.tripwire_hook
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		Helper.registerAmmoRecipe(SeedJar.class, this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "SeedSweeper_empty"; }

		return "SeedSweeper";	// Regular
	}
}
