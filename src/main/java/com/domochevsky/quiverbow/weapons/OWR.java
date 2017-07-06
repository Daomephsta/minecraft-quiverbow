package com.domochevsky.quiverbow.weapons;

import java.util.Collections;
import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ObsidianMagazine;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.OWR_Shot;
import com.domochevsky.quiverbow.util.Newliner;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OWR extends _WeaponBase
{
    public OWR()
    {
	super("wither_rifle", 16);
    }

    public int DmgMagicMin;
    public int DmgMagicMax;

    private int Wither_Duration; // 20 ticks to a second, let's start with 3
    // seconds
    private int Wither_Strength; // 2 dmg per second for 3 seconds = 6 dmg total

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

	Helper.knockUserBack(entity, this.Kickback); // Kickback

	if(!world.isRemote)
	{
	    // Firing
	    OWR_Shot projectile = new OWR_Shot(world, entity, (float) this.Speed,
		    new PotionEffect(MobEffects.WITHER, this.Wither_Duration, this.Wither_Strength));

	    // Random Damage
	    int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
	    // is 10, then the range will
	    // be 10
	    int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
	    // and 10
	    dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
	    // the proper damage range (10-20)

	    projectile.damage = dmg;

	    // Random Magic Damage
	    dmg_range = this.DmgMagicMax - this.DmgMagicMin; // If max dmg is 20 and
	    // min is 10, then the
	    // range will be 10
	    dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0 and
	    // 10
	    dmg += this.DmgMagicMin; // Adding the min dmg of 10 back on top, giving
	    // us the proper damage range (10-20)

	    projectile.damage_Magic = dmg;

	    world.spawnEntity(projectile); // Firing!
	}

	// SFX
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXPLODE, 0.5F, 1.5F);
	NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SPELL_INSTANT,
		(byte) 4); // instant
	// spell

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

	ItemStack clipStack = Helper.getAmmoStack(ObsidianMagazine.class, stack.getItemDamage()); // Unloading
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
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F);
    }

    @Override
    void doCooldownSFX(World world, Entity entity)
    {
	NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_LARGE, (byte) 4); // large
	// smoke
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.2F);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4)
    {
	super.addInformation(stack, player, list, par4);
	if (this.getCooldown(stack) > 0) Collections.addAll(list, Newliner
		.translateAndParse(getUnlocalizedName() + ".cooldown", this.displayInSec(this.getCooldown(stack))));
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 7)", 7).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 13)", 13).getInt();

	this.DmgMagicMin = config.get(this.name, "What magic damage am I dealing, at least? (default 6)", 6).getInt();
	this.DmgMagicMax = config.get(this.name, "What magic damage am I dealing, tops? (default 14)", 14).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 3.0 BPT (Blocks Per Tick))", 3.0)
		.getDouble();

	this.Knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 2)", 2)
		.getInt();
	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 6)", 6)
		.getInt();

	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 60 ticks)", 60).getInt();

	this.Wither_Strength = config.get(this.name, "How strong is my Wither effect? (default 3)", 3).getInt();
	this.Wither_Duration = config.get(this.name, "How long does my Wither effect last? (default 61 ticks)", 61)
		.getInt();

	this.isMobUsable = config
		.get(this.name, "Can I be used by QuiverMobs? (default false. Too high-power for them.)", false)
		.getBoolean();
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One wither rifle (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "odo", "owo", "oso", 'o',
		    Blocks.OBSIDIAN, 'd', Items.DIAMOND, 's', Items.NETHER_STAR, 'w',
		    Helper.getWeaponStackByClass(OSR.class, true));
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
