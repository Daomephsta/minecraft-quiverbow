package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.LapisMagazine;
import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.projectiles.LapisShot;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class LapisCoil extends MagazineFedWeapon
{
    int Weakness_Strength;
    int Weakness_Duration;
    int Nausea_Duration;
    int Hunger_Strength;
    int Hunger_Duration;
    
    public LapisCoil(_AmmoBase ammo)
    {
	super("lapis_coil", ammo, 100);
	setFiringBehaviour(new SingleShotFiringBehaviour<LapisCoil>(this, (world, weaponStack, entity, data) -> 
	{
	    // Random Damage
	    int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
	    // is 10, then the range will
	    // be 10
	    int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
	    // and 10
	    dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
	    // the proper damage range (10-20)

	    // Projectile
	    LapisShot projectile = new LapisShot(world, entity, (float) this.Speed,
		    new PotionEffect(MobEffects.NAUSEA, this.Nausea_Duration, 1),
		    new PotionEffect(MobEffects.HUNGER, this.Hunger_Duration, this.Hunger_Strength),
		    new PotionEffect(MobEffects.WEAKNESS, this.Weakness_Duration, this.Weakness_Strength));
	    projectile.damage = dmg;

	    projectile.ticksInGroundMax = 100; // 5 sec before it disappears
	    
	    return projectile;
	}));
	this.Cooldown = 4;
    }
    
    @Override
    public void doFireFX(World world, Entity entity)
    {
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.5F);
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 3.0F);
    }
    
    @Override
    protected void doUnloadFX(World world, Entity entity)
    {
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 1)", 1).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 3)", 3).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5)
		.getDouble();

	this.Weakness_Strength = config.get(this.name, "How strong is my Weakness effect? (default 2)", 2).getInt();
	this.Weakness_Duration = config.get(this.name, "How long does my Weakness effect last? (default 40 ticks)", 40)
		.getInt();
	this.Nausea_Duration = config.get(this.name, "How long does my Nausea effect last? (default 40 ticks)", 40)
		.getInt();
	this.Hunger_Strength = config.get(this.name, "How strong is my Hunger effect? (default 2)", 2).getInt();
	this.Hunger_Duration = config.get(this.name, "How long does my Hunger effect last? (default 40 ticks)", 40)
		.getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One lapis coil (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "z z", "axa", " y ", 'x', Blocks.PISTON,
		    'y', Blocks.LEVER, 'z', Items.IRON_INGOT, 'a', Items.REPEATER);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	Helper.registerAmmoRecipe(LapisMagazine.class, this);
    }
}