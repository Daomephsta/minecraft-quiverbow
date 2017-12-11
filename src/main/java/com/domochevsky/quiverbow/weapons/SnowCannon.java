package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.SnowShot;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SnowCannon extends _WeaponBase
{    
    private int Slow_Strength; // -15% speed per level. Lvl 3 = -45%
    private int Slow_Duration;

    public SnowCannon()
    {
	super("snow_cannon", 64);
	setFiringBehaviour(new SalvoFiringBehaviour<SnowCannon>(this, 4, (world, weaponStack, entity, data) ->
	{
	    float spreadHor = world.rand.nextFloat() * 20 - 10; // Spread between -5 and 5
	    float spreadVert = world.rand.nextFloat() * 20 - 10;
	    SnowShot snow = new SnowShot(world, entity, (float) this.Speed, spreadHor, spreadVert,
		    new PotionEffect(MobEffects.SLOWNESS, this.Slow_Duration, this.Slow_Strength));

	    // Random Damage
	    int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
	    // is 10, then the range will
	    // be 10
	    int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
	    // and 10
	    dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
	    // the proper damage range (10-20)
	    snow.damage = dmg;
	    
	    return snow;
	}));
    }
    
    @Override
    public void doFireFX(World world, Entity entity)
    {
	entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 1)", 1).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 2)", 2).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5)
		.getDouble();
	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 2)", 2)
		.getInt();
	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 15 ticks)", 15).getInt();

	this.Slow_Strength = config.get(this.name, "How strong is my Slowness effect? (default 3)", 3).getInt();
	this.Slow_Duration = config.get(this.name, "How long does my Slowness effect last? (default 40 ticks)", 40)
		.getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One redstone sprayer (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "zxz", "zbz", "aya", 'x', Blocks.PISTON,
		    'y', Blocks.TRIPWIRE_HOOK, 'z', Blocks.WOOL, 'a', Blocks.OBSIDIAN, 'b', Blocks.STICKY_PISTON);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(Blocks.SNOW, 4));
    }
}
