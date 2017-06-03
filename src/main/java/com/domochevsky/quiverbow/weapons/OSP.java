package com.domochevsky.quiverbow.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ObsidianMagazine;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.OSP_Shot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OSP extends _WeaponBase
{
    public OSP()
    {
	super("splinter_pistol", 16);
	ItemStack ammo = Helper.getAmmoStack(ObsidianMagazine.class, 0);
	this.setMaxDamage(ammo.getMaxDamage() * 2); // Fitting our max capacity
						    // to the magazine. EDIT:
						    // Experimental double
						    // capacity
    }

    private int Wither_Duration; // 20 ticks to a second, let's start with 3
				 // seconds
    private int Wither_Strength; // 2 dmg per second for 3 seconds = 6 dmg total

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
	this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/OSP");
	this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/OSP_Empty");
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
	if (this.getCooldown(stack) > 0)
	{
	    return;
	} // Hasn't cooled down yet

	// SFX
	world.playSoundAtEntity(entity, "random.explode", 0.4F, 1.5F);
	NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), (byte) 3, (byte) 1); // smoke

	// Firing
	OSP_Shot shot = new OSP_Shot(world, entity, (float) this.Speed,
		new PotionEffect(Potion.wither.id, this.Wither_Duration, this.Wither_Strength));

	// Random Damage
	int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
						   // is 10, then the range will
						   // be 10
	int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
						     // and 10
	dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
			    // the proper damage range (10-20)

	shot.damage = dmg;

	world.spawnEntity(shot); // Firing!

	this.setCooldown(stack, this.Cooldown);
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

	ItemStack clipStack = Helper.getAmmoStack(ObsidianMagazine.class,
		MathHelper.floor_double(stack.getItemDamage() / 2) + 1); // Unloading
									 // all
									 // ammo
									 // into
									 // that
									 // clip,
									 // with
									 // some
									 // loss

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
	world.playSoundAtEntity(entity, "random.click", 1.7F, 0.3F);
    }

    @Override
    void doCooldownSFX(World world, Entity entity)
    {
	world.playSoundAtEntity(entity, "tile.piston.out", 0.3F, 0.4F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 4)", 4).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 8)", 8).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.7 BPT (Blocks Per Tick))", 1.7)
		.getDouble();

	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 15 ticks)", 15).getInt();

	this.Wither_Strength = config.get(this.name, "How strong is my Wither effect? (default 1)", 1).getInt();
	this.Wither_Duration = config.get(this.name, "How long does my Wither effect last? (default 61 ticks)", 61)
		.getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One Obsidian Splinter
	    GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), " io", "ipi", "oft", 'o',
		    Blocks.OBSIDIAN, 'i', Items.IRON_INGOT, 'p', Blocks.PISTON, 'f', Items.FLINT_AND_STEEL, 't',
		    Blocks.TRIPWIRE_HOOK);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	// Reloading with obsidian magazine, setting its ammo metadata as ours
	// (Need to be empty for that)
	Helper.registerAmmoRecipe(ObsidianMagazine.class, this);
    }

    @Override
    public String getModelTexPath(ItemStack stack) // The model texture path
    {
	if (stack.getItemDamage() >= stack.getMaxDamage())
	{
	    return "OSP_empty";
	} // empty
	if (this.getCooldown(stack) > 0)
	{
	    return "OSP_hot";
	} // Cooling down

	return "OSP"; // Regular
    }
}
