package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.EnderQuartzClip;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderAno;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Endernymous extends _WeaponBase
{
    public Endernymous()
    {
	super("hidden_ender_pistol", 8);
    }

    private int MaxTicks;

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
									  // side,
									  // mob
									  // usable
    {
	if (this.getCooldown(stack) > 0)
	{
	    return;
	} // Hasn't cooled down yet

	Helper.knockUserBack(entity, this.Kickback); // Kickback

	// SFX
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, 1.4F, 0.5F);
	NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.PORTAL, (byte) 4);

	this.setCooldown(stack, this.Cooldown); // Cooling down now

	this.fireShot(world, entity); // Firing!

	if (this.consumeAmmo(stack, entity, 1)) // We're done here
	{
	    this.dropMagazine(world, stack, entity);
	}
    }

    // Single firing action for something that fires multiple per trigger
    private void fireShot(World world, Entity entity)
    {
	// Random Damage
	int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
						   // is 10, then the range will
						   // be 10
	int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 1
						     // and 10 (inclusive both)
	dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
			    // the proper damage range (10-20)

	EnderAno shot = new EnderAno(world, entity, (float) this.Speed);
	shot.damage = dmg;
	shot.ticksInAirMax = this.MaxTicks;

	world.spawnEntity(shot); // Firing
    }

    private void dropMagazine(World world, ItemStack stack, Entity entity)
    {
	if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
	{
	    this.setCooldown(stack, 60);
	    return;
	}

	ItemStack clipStack = Helper.getAmmoStack(EnderQuartzClip.class, stack.getItemDamage()); // Unloading
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
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.3F);
    }

    @Override
    void doCooldownSFX(World world, Entity entity)
    {
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_GLASS_BREAK, 0.3F, 0.3F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 16)", 16).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 24)", 24).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 5.0 BPT (Blocks Per Tick))", 5.0)
		.getDouble();
	this.MaxTicks = config.get(this.name, "How long does my projectile exist, tops? (default 40 ticks)", 40)
		.getInt();

	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 1)", 1)
		.getInt();

	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 20 ticks)", 20).getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false)", false)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "e e", "ofo", "oto", 'o',
		    Blocks.OBSIDIAN, 'e', Blocks.END_STONE, 't', Blocks.TRIPWIRE_HOOK, 'f', Items.FLINT_AND_STEEL);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	// Ammo
	Helper.registerAmmoRecipe(EnderQuartzClip.class, this);
    }
}
