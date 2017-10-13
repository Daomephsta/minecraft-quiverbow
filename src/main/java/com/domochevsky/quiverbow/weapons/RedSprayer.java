package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.LargeRedstoneMagazine;
import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.projectiles.RedSpray;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

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

public class RedSprayer extends MagazineFedWeapon
{
    private int Wither_Strength;
    private int Wither_Duration;
    private int Blindness_Duration;
    
    public RedSprayer(_AmmoBase ammo)
    {
	super("redstone_sprayer", ammo, 200);
	setFiringBehaviour(new SalvoFiringBehaviour<RedSprayer>(this, 5, (world, waeponStack, entity, data) ->
	{
	    // Spread
	    float spreadHor = world.rand.nextFloat() * 20 - 10; // Spread between
	    // -10 and 10
	    float spreadVert = world.rand.nextFloat() * 20 - 10;

	    RedSpray shot = new RedSpray(entity.world, entity, (float) this.Speed, spreadHor, spreadVert,
		    new PotionEffect(MobEffects.WITHER, this.Wither_Duration, this.Wither_Strength),
		    new PotionEffect(MobEffects.BLINDNESS, this.Blindness_Duration, 1));
	    return shot;
	}));
    }
    
    @Override
    public void doFireFX(World world, Entity entity)
    {
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.7F, 1.5F);
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

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 0.5 BPT (Blocks Per Tick))", 0.5)
		.getDouble();

	this.Wither_Strength = config.get(this.name, "How strong is my Wither effect? (default 2)", 2).getInt();
	this.Wither_Duration = config.get(this.name, "How long does my Wither effect last? (default 20 ticks)", 20)
		.getInt();
	this.Blindness_Duration = config
		.get(this.name, "How long does my Blindness effect last? (default 20 ticks)", 20).getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One redstone sprayer (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "zxz", "aba", "zyz", 'x', Blocks.PISTON,
		    'y', Blocks.TRIPWIRE_HOOK, 'z', Items.IRON_INGOT, 'a', Items.REPEATER, 'b', Blocks.STICKY_PISTON);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	Helper.registerAmmoRecipe(LargeRedstoneMagazine.class, this);
    }
}
