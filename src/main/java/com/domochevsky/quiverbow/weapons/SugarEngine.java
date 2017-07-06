package com.domochevsky.quiverbow.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.GatlingAmmo;
import com.domochevsky.quiverbow.projectiles.SugarRod;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class SugarEngine extends _WeaponBase
{
    public SugarEngine()
    {
	super("sugar_engine", 200);
    }

    public float Spread;

    int getSpinupTime()
    {
	return 30;
    } // Time in ticks until we can start firing

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	if (this.getDamage(stack) >= stack.getMaxDamage())
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // Is empty

	if (player.isSneaking()) // Dropping the magazine
	{
	    this.dropMagazine(world, stack, player);
	    return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	this.doSingleFire(stack, world, player); // Handing it over to the
	// neutral firing function
	return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void doSingleFire(ItemStack stack, World world, Entity entity) // Server
    // side
    {
	if (!stack.hasTagCompound())
	{
	    stack.setTagCompound(new NBTTagCompound());
	}
	// Weapon is ready, so we can spin up now. set spin-down immunity to x
	// ticks and spin up
	stack.getTagCompound().setInteger("spinDownImmunity", 20); // Can't spin
	// down for
	// 20 ticks.
	// Also
	// indicates
	// our desire
	// to spin up

	if (stack.getTagCompound().getInteger("spinning") < this.getSpinupTime())
	{
	    return;
	} // Not ready yet, so keep spinning up
	// else, we're ready

	this.setBurstFire(stack, 4); // Setting the rods left to fire to 4, then
	// going through that via onUpdate (Will be
	// constantly refreshed if we're still
	// spinning)
    }

    private void dropMagazine(World world, ItemStack stack, Entity entity)
    {
	if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
	{
	    this.setCooldown(stack, 80);
	    return;
	}

	ItemStack clipStack = Helper.getAmmoStack(GatlingAmmo.class, stack.getItemDamage()); // Unloading
	// all
	// ammo
	// into
	// that
	// clip

	stack.setItemDamage(stack.getMaxDamage()); // Emptying out

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
	entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) // Overhauled
    // default
    {
	if (world.isRemote)
	{
	    return;
	} // Not doing this on client side

	if (this.getCooldown(stack) > 0)
	{
	    this.setCooldown(stack, this.getCooldown(stack) - 1);
	} // Cooling down
	if (this.getCooldown(stack) == 1)
	{
	    this.doCooldownSFX(world, entity);
	} // One tick before cooldown is done with, so SFX now

	if (stack.getTagCompound() == null)
	{
	    stack.setTagCompound(new NBTTagCompound());
	} // Init

	if (stack.getTagCompound().getInteger("spinDownImmunity") == 0) // Not
	    // firing
	    // and
	    // no
	    // immunity
	    // left,
	    // so
	    // spinning
	    // down
	{
	    if (stack.getTagCompound().getInteger("spinning") > 0)
	    {
		stack.getTagCompound().setInteger("spinning", stack.getTagCompound().getInteger("spinning") - 1);

		this.doSpinSFX(stack, world, entity);
	    }
	    // else, not spinning
	}
	else // We're currently immune to spinning down, so decreasing that
	    // immunity time until we actually can
	{
	    stack.getTagCompound().setInteger("spinDownImmunity",
		    stack.getTagCompound().getInteger("spinDownImmunity") - 1);

	    // Also assuming that we're trying to fire, so spinning up (This is
	    // a workaround for the fact that onRightClick isn't called every
	    // tick)
	    if (stack.getTagCompound().getInteger("spinning") < this.getSpinupTime())
	    {
		stack.getTagCompound().setInteger("spinning", stack.getTagCompound().getInteger("spinning") + 1);
	    }
	    // else, we've reached full spin

	    this.doSpinSFX(stack, world, entity); // Spin down SFX
	}

	if (this.getBurstFire(stack) > 0)
	{
	    this.setBurstFire(stack, this.getBurstFire(stack) - 1); // One done

	    if (stack.getItemDamage() < stack.getMaxDamage() && holdingItem) // Can
		// only
		// do
		// it
		// if
		// we're
		// loaded
		// and
		// holding
		// the
		// weapon
	    {
		this.doBurstFire(stack, world, entity);

		if (this.consumeAmmo(stack, entity, 1))
		{
		    this.dropMagazine(world, stack, entity);
		} // You're empty
	    }
	    // else, either not loaded or not held
	}
    }

    private void doBurstFire(ItemStack stack, World world, Entity entity)
    {
	Helper.knockUserBack(entity, this.Kickback); // Kickback
	if(!world.isRemote)
	{
	    // Firing
	    float spreadHor = world.rand.nextFloat() * this.Spread - (this.Spread / 2); // Spread
	    // between
	    // -4
	    // and
	    // 4
	    // at
	    // (
	    // (0.0
	    // to
	    // 1.0)
	    // *
	    // 16
	    // -
	    // 8)
	    float spreadVert = world.rand.nextFloat() * this.Spread - (this.Spread / 2);

	    int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
	    // is 10, then the range will
	    // be 10
	    int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
	    // and 10
	    dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
	    // the proper damage range (10-20)

	    SugarRod projectile = new SugarRod(world, entity, (float) this.Speed, spreadHor, spreadVert);
	    projectile.damage = dmg;

	    world.spawnEntity(projectile);
	}

	// SFX
	entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.2F);
	entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.6F, 3.0F);
    }

    private void doSpinSFX(ItemStack stack, World world, Entity player)
    {
	// SFX
	int spin = stack.getTagCompound().getInteger("spinning");
	// Increasing in frequency as we spin up TODO: Clean up with formula
	switch (spin)
	{
	case 1:
	case 5:
	case 9:
	case 13:
	case 16:
	case 19:
	case 21:
	case 23:
	case 25:
	    Helper.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.8F, 1.8F);
	    break;
	default:
	    if (spin >= 27) Helper.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.8F, 1.8F);
	    break;
	}
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 1)", 1).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 3)", 3).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 2.0 BPT (Blocks Per Tick))", 2.0)
		.getDouble();

	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 1)", 1)
		.getInt();
	this.Spread = (float) config.get(this.name, "How accurate am I? (default 10 spread)", 10).getDouble();

	this.isMobUsable = config
		.get(this.name, "Can I be used by QuiverMobs? (default true. They'll probably figure it out.)", true)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	/*
	 * if (this.Enabled) { // One Sugar Gatling (empty)
	 * GameRegistry.addRecipe(new ItemStack(this, 1 , stack.getMaxDamage()),
	 * "b b", "b b", " m ", 'b',
	 * Helper.getAmmoStack(Part_GatlingBarrel.class, 0), 'm',
	 * Helper.getAmmoStack(Part_GatlingBody.class, 0) ); } else if
	 * (Main.noCreative) { this.setCreativeTab(null); } // Not enabled and
	 * not allowed to be in the creative menu
	 * 
	 * // Reloading with gatling ammo, setting its clip metadata as ours
	 * (Need to be empty for that)
	 * Helper.registerAmmoRecipe(GatlingAmmo.class, this);
	 */
    }
}
