package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.PotatoShot;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.weapons.base.ProjectileWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Potatosser extends ProjectileWeapon
{
    private boolean shouldDrop;

    public Potatosser()
    {
	super("potatosser", 14);
	setFiringBehaviour(new SingleShotFiringBehaviour<Potatosser>(this, (world, weaponStack, entity, data) ->
	{
	    // Random Damage
	    int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
	    // is 10, then the range will
	    // be 10
	    int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
	    // and 10
	    dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
	    // the proper damage range (10-20)

	    // Firing
	    PotatoShot shot = new PotatoShot(world, entity, (float) this.Speed);
	    shot.damage = dmg;
	    shot.setDrop(this.shouldDrop);
	    return shot;
	}));
    }

    @Override
    public void doFireFX(World world, Entity entity)
    {
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 0.7F, 0.4F);
    }

    @Override
    protected void doCooldownSFX(World world, Entity entity) // Server side
    {
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.3F, 3.0F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 2)", 2).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 5)", 5).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5)
		.getDouble();
	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 15)", 15).getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);

	this.shouldDrop = config.get(this.name, "Do I drop naked potatoes on misses? (default true)", true)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One potatosser (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "xax", "zbx", "cdy", 'a',
		    Blocks.TRAPDOOR, 'b', Blocks.PISTON, 'c', Blocks.TRIPWIRE_HOOK, 'd', Blocks.STICKY_PISTON, 'x',
		    Blocks.IRON_BARS, 'y', Items.IRON_INGOT, 'z', Items.FLINT_AND_STEEL);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	GameRegistry.addRecipe(
		new RecipeLoadAmmo(this).addComponent(Items.COAL, 0, 1, 1).addComponent(Items.POTATO, 1, 1, 7));
    }
}
