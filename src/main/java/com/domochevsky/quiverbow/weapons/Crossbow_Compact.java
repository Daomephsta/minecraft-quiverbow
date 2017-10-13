package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.RegularArrow;
import com.domochevsky.quiverbow.weapons.base.WeaponCrossbow;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Crossbow_Compact extends WeaponCrossbow
{
    public Crossbow_Compact()
    {
	super("compact_crossbow", 1, (world, weaponStack, entity, data) ->
	{
	    Crossbow_Compact weapon = (Crossbow_Compact) weaponStack.getItem();
	    RegularArrow entityarrow = new RegularArrow(world, entity, (float) weapon.Speed);

	    // Random Damage
	    int dmg_range = weapon.DmgMax - weapon.DmgMin; // If max dmg is 20 and min
	    // is 10, then the range will
	    // be 10
	    int dmg = world.rand.nextInt(dmg_range + 1);// Range will be between 0
	    // and 10
	    dmg += weapon.DmgMin; // Adding the min dmg of 10 back on top, giving us
	    // the proper damage range (10-20)

	    entityarrow.damage = dmg;
	    entityarrow.knockbackStrength = weapon.Knockback; // Comes with an inbuild
	    // knockback II
	    return entityarrow;
	});
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 14)", 14).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 20)", 20).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5)
		.getDouble();

	this.Knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 2)", 2)
		.getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One compact crossbow (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "zxy", "xzy", "zxy", 'x', Items.STICK,
		    'y', Items.STRING, 'z', Blocks.PLANKS);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	GameRegistry.addShapelessRecipe(new ItemStack(this), // Fill the empty
		// crossbow with
		// one arrow
		Items.ARROW, Helper.createEmptyWeaponOrAmmoStack(this, 1));
    }
}
