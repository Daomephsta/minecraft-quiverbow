package com.domochevsky.quiverbow.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.SeedJar;
import com.domochevsky.quiverbow.projectiles.Seed;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SeedSweeper extends _WeaponBase
{
    public SeedSweeper()
    {
	super("seed_sweeper", 512);
    }

    private int Dmg;
    private float Spread;

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
	if (this.getCooldown(stack) != 0)
	{
	    return;
	} // Hasn't cooled down yet

	// SFX
	entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.6F, 0.9F);

	this.setCooldown(stack, this.Cooldown); // Cooling down now, no matter
						// what

	int counter = 8;

	while (counter > 0 && this.getDamage(stack) < stack.getMaxDamage()) // Keep
									   // firing
									   // until
									   // you
									   // have
									   // done
									   // so
									   // 8
									   // times
									   // or
									   // run
									   // out
									   // of
									   // seeds
	{
	    this.fireShot(world, entity);

	    if (this.consumeAmmo(stack, entity, 1)) // We're done here
	    {
		this.dropMagazine(world, stack, entity);
		return;
	    }
	    // else, still has ammo left. Continue.

	    counter -= 1;
	}

	if (this.getDamage(stack) >= stack.getMaxDamage())
	{
	    this.dropMagazine(world, stack, entity);
	}
    }

    private void fireShot(World world, Entity entity)
    {
	if(world.isRemote) return;
	float spreadHor = world.rand.nextFloat() * this.Spread - (this.Spread / 2);
	float spreadVert = world.rand.nextFloat() * this.Spread - (this.Spread / 2);

	Seed shot = new Seed(world, entity, (float) this.Speed, spreadHor, spreadVert);
	shot.damage = this.Dmg;

	world.spawnEntity(shot); // Firing
    }

    private void dropMagazine(World world, ItemStack stack, Entity entity)
    {
	if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
	{
	    this.setCooldown(stack, 40);
	    return;
	}

	ItemStack clipStack = Helper.getAmmoStack(SeedJar.class, stack.getItemDamage()); // Unloading
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
	entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.Dmg = config.get(this.name, "What damage am I dealing per projectile? (default 1)", 1).getInt();
	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 15 ticks)", 15).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.6 BPT (Blocks Per Tick))", 1.6)
		.getDouble();
	this.Spread = (float) config.get(this.name, "How accurate am I? (default 26 spread)", 26).getDouble();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One Seed Sweeper (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), " i ", "ipi", " it", 'p', Blocks.PISTON,
		    'i', Items.IRON_INGOT, 't', Blocks.TRIPWIRE_HOOK);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	Helper.registerAmmoRecipe(SeedJar.class, this);
    }
}
