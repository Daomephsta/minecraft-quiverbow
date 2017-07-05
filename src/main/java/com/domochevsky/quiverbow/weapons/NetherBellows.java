package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.LargeNetherrackMagazine;
import com.domochevsky.quiverbow.projectiles.NetherFire;

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

public class NetherBellows extends _WeaponBase
{
    public NetherBellows()
    {
	super("nether_bellows", 200);
    }

    private int Dmg;
    private int FireDur;

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
	// SFX
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 0.3F);

	this.setCooldown(stack, this.Cooldown);

	int counter = 0;

	while (counter < 5)
	{
	    this.fireSingle(world, entity);

	    if (this.consumeAmmo(stack, entity, 1)) // We're done here
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
	float spreadHor = world.rand.nextFloat() * 20 - 10; // Spread between
							    // -10 and 10
	float spreadVert = world.rand.nextFloat() * 20 - 10;

	NetherFire shot = new NetherFire(world, entity, (float) this.Speed, spreadHor, spreadVert);
	shot.damage = this.Dmg;
	shot.fireDuration = this.FireDur;

	world.spawnEntity(shot);
    }

    private void dropMagazine(World world, ItemStack stack, Entity entity)
    {
	if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
	{
	    this.setCooldown(stack, 60);
	    return;
	}

	ItemStack clipStack = Helper.getAmmoStack(LargeNetherrackMagazine.class, stack.getItemDamage()); // Unloading
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
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);
	this.Speed = config.get(this.name, "How fast are my projectiles? (default 0.75 BPT (Blocks Per Tick))", 0.75)
		.getDouble();
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
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "zxz", "zbz", "cya", 'x', Blocks.PISTON,
		    'y', Blocks.TRIPWIRE_HOOK, 'z', Blocks.OBSIDIAN, 'a', Items.REPEATER, 'b', Blocks.STICKY_PISTON,
		    'c', Items.FLINT_AND_STEEL);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	Helper.registerAmmoRecipe(LargeNetherrackMagazine.class, this);
    }
}
