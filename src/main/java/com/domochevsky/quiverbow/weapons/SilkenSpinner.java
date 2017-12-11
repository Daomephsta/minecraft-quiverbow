package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.WebShot;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SilkenSpinner extends _WeaponBase
{
    public SilkenSpinner()
    {
	super("silken_spinner", 8);
	this.setCreativeTab(CreativeTabs.TOOLS); // This is a tool
	setFiringBehaviour(new SingleShotFiringBehaviour<SilkenSpinner>(this, (world, weaponStack, entity, data) -> new WebShot(world, entity, (float) ((_WeaponBase)weaponStack.getItem()).Speed)));
    }
    
    @Override
    public void doFireFX(World world, Entity entity)
    {
	entity.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5)
		.getDouble();

	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 20 ticks)", 20).getInt();

	this.isMobUsable = config.get(this.name,
		"Can I be used by QuiverMobs? (default false. Potentially abusable for free cobwebs.)", false)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "ihi", "gpg", "tsi", 'p', Blocks.PISTON,
		    's', Blocks.STICKY_PISTON, 't', Blocks.TRIPWIRE_HOOK, 'i', Items.IRON_INGOT, 'h', Blocks.HOPPER,
		    'g', Blocks.GLASS_PANE);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	// Making web out of string
	GameRegistry.addRecipe(new ItemStack(Blocks.WEB), "s s", " s ", "s s", 's', Items.STRING);

	GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(Blocks.WEB, 1));
    }
}
