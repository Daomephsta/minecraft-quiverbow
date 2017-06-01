package com.domochevsky.quiverbow.weapons;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.PotatoShot;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Potatosser extends _WeaponBase
{
	public Potatosser() { super("potatosser", 14); }

	
	private boolean shouldDrop;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/Potatosser");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/Potatosser_Empty");
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
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet

		// SFX
		world.playSoundAtEntity(entity, "random.break", 0.7F, 0.4F);

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 						// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;											// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		// Firing
		PotatoShot shot = new PotatoShot(world, entity, (float) this.Speed);
		shot.damage = dmg;
		shot.setDrop(this.shouldDrop);

		world.spawnEntityInWorld(shot);

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@Override
	void doCooldownSFX(World world, Entity entity) // Server side
	{
		world.playSoundAtEntity(entity, "random.click", 0.3F, 3.0F);
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 2)", 2).getInt();
		this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 5)", 5).getInt();

		this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5).getDouble();
		this.Cooldown = config.get(this.name, "How long until I can fire again? (default 15)", 15).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);

		this.shouldDrop = config.get(this.name, "Do I drop naked potatoes on misses? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One potatosser (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "xax", "zbx", "cdy",
					'a', Blocks.trapdoor,
					'b', Blocks.piston,
					'c', Blocks.tripwire_hook,
					'd', Blocks.sticky_piston,
					'x', Blocks.iron_bars,
					'y', Items.iron_ingot,
					'z', Items.flint_and_steel
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(Items.coal, 0, 1, 1).addComponent(Items.potato, 1, 1, 7));
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		return "Potatosser";	// Regular
	}
}
