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
import com.domochevsky.quiverbow.ammo.GoldMagazine;
import com.domochevsky.quiverbow.projectiles.CoinShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CoinTosser extends _WeaponBase
{

    private boolean shouldDrop;

    public CoinTosser()
    {
	super("coin_tosser", 72);
    }

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
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 3.0F);

	this.setCooldown(stack, this.Cooldown); // Cooling down now

	int counter = 0;

	while (counter < 9) // Scatter 9
	{
	    this.fireShot(world, entity, counter); // Firing!

	    if (this.consumeAmmo(stack, entity, 1)) // We're done here
	    {
		this.dropMagazine(world, stack, entity);
		return;
	    }
	    // else, still has ammo left. Continue.

	    counter += 1;
	}
    }

    // Single firing action for something that fires multiple per trigger
    private void fireShot(World world, Entity entity, int shotCount)
    {
	// Random Damage
	int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
						   // is 10, then the range will
						   // be 10
	int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
						     // and 10
	dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
			    // the proper damage range (10-20)

	float spreadHor = 0;
	float spreadVert = 0;

	if (shotCount == 2)
	{
	    spreadHor = 10.0f;
	    spreadVert = 0f;
	}
	else if (shotCount == 3)
	{
	    spreadHor = -10.0f;
	    spreadVert = 0f;
	}
	else if (shotCount == 4)
	{
	    spreadHor = 0.0f;
	    spreadVert = -4.0f;
	}
	else if (shotCount == 5)
	{
	    spreadHor = 0.0f;
	    spreadVert = 2.0f;
	}
	else if (shotCount == 6)
	{
	    spreadHor = 5.0f;
	    spreadVert = 3.0f;
	}
	else if (shotCount == 7)
	{
	    spreadHor = -5.0f;
	    spreadVert = 3.0f;
	}
	else if (shotCount == 8)
	{
	    spreadHor = 5.0f;
	    spreadVert = 1.0f;
	}
	else if (shotCount == 9)
	{
	    spreadHor = -5.0f;
	    spreadVert = 1.0f;
	}

	CoinShot shot = new CoinShot(world, entity, (float) this.Speed, spreadHor, spreadVert);
	shot.damage = dmg;
	shot.setDrop(this.shouldDrop);

	world.spawnEntity(shot); // Firing
    }

    private void dropMagazine(World world, ItemStack stack, Entity entity)
    {
	if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
	{
	    this.setCooldown(stack, 60);
	    return;
	}

	ItemStack clipStack = Helper.getAmmoStack(GoldMagazine.class, stack.getItemDamage()); // Unloading
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

	this.DmgMin = config.get(this.name, "What damage am I dealing per nugget, at least? (default 1)", 1).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing per nugget, tops? (default 3)", 3).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5)
		.getDouble();

	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 1)", 1)
		.getInt();

	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 15 ticks)", 15).getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);

	this.shouldDrop = config.get(this.name, "Do I drop gold nuggets on misses? (default true)", true)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One coin tosser (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "z z", "zxz", " y ", 'x', Blocks.PISTON,
		    'y', Blocks.LEVER, 'z', Items.IRON_INGOT);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	// Ammo
	Helper.registerAmmoRecipe(GoldMagazine.class, this);
    }
}
