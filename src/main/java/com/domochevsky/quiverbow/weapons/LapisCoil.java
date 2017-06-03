package com.domochevsky.quiverbow.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.LapisMagazine;
import com.domochevsky.quiverbow.projectiles.LapisShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LapisCoil extends _WeaponBase
{
    public LapisCoil()
    {
	super("lapis_coil", 100);

	ItemStack ammo = Helper.getAmmoStack(LapisMagazine.class, 0);
	this.setMaxDamage(ammo.getMaxDamage()); // Fitting our max capacity to
						// the magazine
    }

    int Weakness_Strength;
    int Weakness_Duration;
    int Nausea_Duration;
    int Hunger_Strength;
    int Hunger_Duration;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
	this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/LapisCoil");
	this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/LapisCoil_Empty");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
	if (world.isRemote)
	{
	    return stack;
	} // Not doing this on client side
	if (this.getDamage(stack) >= this.getMaxDamage())
	{
	    return stack;
	} // Is empty

	if (player.isSneaking()) // Dropping the magazine
	{
	    this.dropMagazine(world, stack, player);
	    return stack;
	}

	this.doSingleFire(stack, world, player); // Handing it over to the
						 // neutral firing function
	return stack;
    }

    @Override
    public void doSingleFire(ItemStack stack, World world, Entity entity) // Server
									  // side
    {
	// SFX
	world.playSoundAtEntity(entity, "random.wood_click", 1.0F, 0.5F);
	world.playSoundAtEntity(entity, "random.break", 1.0F, 3.0F);

	// Random Damage
	int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
						   // is 10, then the range will
						   // be 10
	int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
						     // and 10
	dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
			    // the proper damage range (10-20)

	// Projectile
	LapisShot projectile = new LapisShot(world, entity, (float) this.Speed,
		new PotionEffect(MobEffects.NAUSEA, this.Nausea_Duration, 1),
		new PotionEffect(MobEffects.HUNGER, this.Hunger_Duration, this.Hunger_Strength),
		new PotionEffect(Potion.weakness.id, this.Weakness_Duration, this.Weakness_Strength));
	projectile.damage = dmg;

	projectile.ticksInGroundMax = 100; // 5 sec before it disappears

	world.spawnEntity(projectile); // Firing!

	this.setCooldown(stack, 4); // For visual purposes
	if (this.consumeAmmo(stack, entity, 1))
	{
	    this.dropMagazine(world, stack, entity);
	}
    }

    private void dropMagazine(World world, ItemStack stack, Entity entity)
    {
	if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
	{
	    this.setCooldown(stack, 60);
	    return;
	}

	ItemStack clipStack = Helper.getAmmoStack(LapisMagazine.class, stack.getItemDamage()); // Unloading
											       // all
											       // ammo
											       // into
											       // that
											       // clip

	stack.setItemDamage(this.getMaxDamage()); // Emptying out

	// Creating the clip
	EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
	entityitem.setDefaultPickupDelay();

	// And dropping it
	if (entity.captureDrops)
	{
	    entity.capturedDrops.add(entityitem);
	}
	else
	{
	    world.spawnEntity(entityitem);
	}

	// SFX
	world.playSoundAtEntity(entity, "random.break", 1.0F, 0.5F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 1)", 1).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 3)", 3).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5)
		.getDouble();

	this.Weakness_Strength = config.get(this.name, "How strong is my Weakness effect? (default 2)", 2).getInt();
	this.Weakness_Duration = config.get(this.name, "How long does my Weakness effect last? (default 40 ticks)", 40)
		.getInt();
	this.Nausea_Duration = config.get(this.name, "How long does my Nausea effect last? (default 40 ticks)", 40)
		.getInt();
	this.Hunger_Strength = config.get(this.name, "How strong is my Hunger effect? (default 2)", 2).getInt();
	this.Hunger_Duration = config.get(this.name, "How long does my Hunger effect last? (default 40 ticks)", 40)
		.getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One lapis coil (empty)
	    GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "z z", "axa", " y ", 'x', Blocks.PISTON,
		    'y', Blocks.LEVER, 'z', Items.IRON_INGOT, 'a', Items.REPEATER);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	Helper.registerAmmoRecipe(LapisMagazine.class, this);
    }

    @Override
    public String getModelTexPath(ItemStack stack) // The model texture path
    {
	if (stack.getItemDamage() >= stack.getMaxDamage())
	{
	    return "LapisCoil2_empty";
	} // Empty
	if (this.getCooldown(stack) > 0)
	{
	    return "LapisCoil2_hot";
	} // Hot

	return "LapisCoil2"; // Regular
    }
}