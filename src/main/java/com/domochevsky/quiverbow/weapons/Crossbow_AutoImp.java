package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.projectiles.RegularArrow;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.weapons.base.WeaponCrossbow;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Crossbow_AutoImp extends WeaponCrossbow
{
    public Crossbow_AutoImp()
    {
	super("auto_crossbow_imp", 16, (world, weaponStack, entity, data) ->
	{
	    Crossbow_AutoImp weapon = (Crossbow_AutoImp) weaponStack.getItem();
	    RegularArrow entityarrow = new RegularArrow(world, entity, (float) weapon.Speed);

	    // Random Damage
	    int dmg_range = weapon.DmgMax - weapon.DmgMin; // If max dmg is 20 and min
	    // is 10, then the range will
	    // be 10
	    int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
	    // and 10
	    dmg += weapon.DmgMin; // Adding the min dmg of 10 back on top, giving us
	    // the proper damage range (10-20)

	    entityarrow.damage = dmg;
	    entityarrow.knockbackStrength = weapon.Knockback; // Comes with an inbuild
	    // knockback I
	    
	    return entityarrow;
	});
    } // 2 bundles

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 10)", 10).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 16)", 16).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5)
		.getDouble();
	this.Knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 1)", 1)
		.getInt();

	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 8 ticks)", 8).getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false.)", false)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One auto-crossbow (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "iii", "scs", " i ", 'i',
		    Items.IRON_INGOT, 's', Blocks.STICKY_PISTON, 'c',
		    Helper.getWeaponStackByClass(Crossbow_Auto.class, true));
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	ItemStack ammo = Helper.getAmmoStack(ArrowBundle.class, 0);

	// Fill what can be filled. One arrow bundle for 8 shots, for up to 2
	// bundles
	GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(ammo.getItem(), 8, 1, 2));
    }
}
