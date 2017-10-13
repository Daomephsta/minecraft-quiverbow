package com.domochevsky.quiverbow.weapons;

import java.util.Collections;
import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ObsidianMagazine;
import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.OSR_Shot;
import com.domochevsky.quiverbow.projectiles._ProjectileBase;
import com.domochevsky.quiverbow.util.Newliner;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OSR extends MagazineFedWeapon
{
    public OSR(_AmmoBase ammo)
    {
	super("splinter_rifle", ammo, 16);
	setFiringBehaviour(new SingleShotFiringBehaviour<OSR>(this, (world, weaponStack, entity, data) -> 
	{
	    OSR weapon = (OSR) weaponStack.getItem();
	    _ProjectileBase projectile = new OSR_Shot(world, entity, (float) weapon.Speed,
		    new PotionEffect(MobEffects.WITHER, weapon.Wither_Duration, weapon.Wither_Strength));

	    // Random Damage
	    int dmg_range = weapon.DmgMax - weapon.DmgMin; // If max dmg is 20 and min
	    // is 10, then the range will
	    // be 10
	    int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
	    // and 10
	    dmg += weapon.DmgMin; // Adding the min dmg of 10 back on top, giving us
	    // the proper damage range (10-20)

	    projectile.damage = dmg;
	    return projectile;
	}));
    }

    public int Wither_Duration; // 20 ticks to a second, let's start with 3
    // seconds
    public int Wither_Strength; // 2 dmg per second for 3 seconds = 6 dmg total
    
    @Override
    protected void doUnloadFX(World world, Entity entity)
    {
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F);
    }

    @Override
    protected void doCooldownSFX(World world, Entity entity)
    {
	NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
		(byte) 4); // smoke
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.5F, 1.2F);
    }
    
    @Override
    public void doFireFX(World world, Entity entity)
    {
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXPLODE, 0.5F, 1.5F);
	NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
		(byte) 4); // smoke
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

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 3.0 BPT (Blocks Per Tick))", 3.0)
		.getDouble();

	this.Knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 2)", 2)
		.getInt();
	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 4)", 4)
		.getInt();

	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 100 ticks)", 100).getInt();

	this.Wither_Strength = config.get(this.name, "How strong is my Wither effect? (default 3)", 3).getInt();
	this.Wither_Duration = config.get(this.name, "How long does my Wither effect last? (default 61 ticks)", 61)
		.getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One obsidigun (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "x x", "zbz", "xyx", 'x',
		    Blocks.OBSIDIAN, 'y', Blocks.LEVER, 'z', Items.IRON_INGOT, 'a', Items.REPEATER, 'b', Blocks.PISTON);
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
