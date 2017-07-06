package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ObsidianMagazine;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.OSP_Shot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class OSP extends _WeaponBase
{
    public OSP()
    {
	super("splinter_pistol", 16);
    }

    private int Wither_Duration; // 20 ticks to a second, let's start with 3
    // seconds
    private int Wither_Strength; // 2 dmg per second for 3 seconds = 6 dmg total

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

	// SFX
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXPLODE, 0.4F, 1.5F);
	NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
		(byte) 1); // smoke

	if(!world.isRemote)
	{
	    // Firing
	    OSP_Shot shot = new OSP_Shot(world, entity, (float) this.Speed,
		    new PotionEffect(MobEffects.WITHER, this.Wither_Duration, this.Wither_Strength));

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
	}

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
		MathHelper.floor(stack.getItemDamage() / 2) + 1); // Unloading
	// all
	// ammo
	// into
	// that
	// clip,
	// with
	// some
	// loss

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
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F);
    }

    @Override
    void doCooldownSFX(World world, Entity entity)
    {
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 0.3F, 0.4F);
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
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), " io", "ipi", "oft", 'o',
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
}
