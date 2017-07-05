package com.domochevsky.quiverbow.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.NeedleMagazine;
import com.domochevsky.quiverbow.projectiles.Thorn;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ThornSpitter extends _WeaponBase
{
    public ThornSpitter()
    {
	super("thorn_spitter", 64);
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
									  // side
    {
	if (this.getCooldown(stack) > 0)
	{
	    return;
	} // Hasn't cooled down yet
	this.setCooldown(stack, this.Cooldown);

	this.setBurstFire(stack, 4); // Setting the thorns left to shoot to 4,
				     // then going through that via onUpdate
    }

    private void dropMagazine(World world, ItemStack stack, Entity entity)
    {
	if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
	{
	    this.setCooldown(stack, 40);
	    return;
	}

	ItemStack clipStack = Helper.getAmmoStack(NeedleMagazine.class, stack.getItemDamage()); // Unloading
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
	entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 1.3F);
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

		if (this.consumeAmmo(stack, entity, 1)) // We're done here
		{
		    this.dropMagazine(world, stack, entity);
		    return;
		}
		// else, still has ammo left. Continue.
	    }
	    // else, either not loaded or not held
	}
    }

    private void doBurstFire(ItemStack stack, World world, Entity entity)
    {
	int dmg_range = DmgMax - DmgMin; // If max dmg is 20 and min is 10, then
					 // the range will be 10
	int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
						     // and 10
	dmg += DmgMin; // Adding the min dmg of 10 back on top, giving us the
		       // proper damage range (10-20)

	// Firing
	Thorn projectile = new Thorn(world, (EntityLivingBase) entity, (float) Speed);
	projectile.damage = dmg;

	world.spawnEntity(projectile);

	// SFX
	entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 0.6F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 1)", 1).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 2)", 2).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.75 BPT (Blocks Per Tick))", 1.75)
		.getDouble();

	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 10 ticks)", 10).getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (Enabled)
	{
	    // One Thorn Spitter (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "bib", "php", "sts", 't',
		    Blocks.TRIPWIRE_HOOK, 'b', Blocks.IRON_BARS, 'i', Items.IRON_INGOT, 'h', Blocks.HOPPER, 's',
		    Blocks.STICKY_PISTON, 'p', Blocks.PISTON);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	Helper.registerAmmoRecipe(NeedleMagazine.class, this);
    }
}