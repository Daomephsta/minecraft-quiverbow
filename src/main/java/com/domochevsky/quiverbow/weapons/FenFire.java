package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.FenGoop;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.weapons.base.ProjectileWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FenFire extends ProjectileWeapon
{
    private int FireDur;
    private int LightTick;
    
    public FenFire()
    {
	super("fen_fire", 32);
	this.setCreativeTab(CreativeTabs.TOOLS); // Tool, so on the tool tab
	setFiringBehaviour(new SingleShotFiringBehaviour<FenFire>(this, (world, weaponStack, entity, data) -> 
	{
	    FenGoop projectile = new FenGoop(world, entity, (float) this.Speed);
	    projectile.fireDuration = this.FireDur;

	    if (this.LightTick != 0)
	    {
		projectile.lightTick = this.LightTick;
	    } // Scheduled to turn off again
	    
	    return projectile;
	}));
    }
    
    @Override
    public void doFireFX(World world, Entity entity)
    {
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ARROW_SHOOT, 0.7F, 0.3F);
    }

    @Override
    protected void doCooldownSFX(World world, Entity entity)
    {
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.8F, 2.0F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5)
		.getDouble();
	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 20 ticks)", 20).getInt();
	this.FireDur = config.get(this.name, "How long is what I hit on fire? (default 1s)", 1).getInt();
	this.LightTick = config
		.get(this.name, "How long do my lights stay lit? (default 0 ticks for infinite. 20 ticks = 1 sec)", 0)
		.getInt();

	this.isMobUsable = config
		.get(this.name, "Can I be used by QuiverMobs? (default false. They despise light.)", false)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One Fen Fire (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "di ", "i i", " ts", 't',
		    Blocks.TRIPWIRE_HOOK, 'i', Items.IRON_INGOT, 's', Blocks.STICKY_PISTON, 'd', Blocks.TRAPDOOR);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(Blocks.GLOWSTONE, 4));
    }
}
