package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.RocketBundle;
import com.domochevsky.quiverbow.projectiles.SmallRocket;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DragonBox_Quad extends _WeaponBase
{
    public DragonBox_Quad()
    {
	super("quad_dragonbox", 64);
    }

    private int FireDur;
    private double ExplosionSize;

    private boolean dmgTerrain;

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	if (this.getDamage(stack) >= stack.getMaxDamage())
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // Is empty

	this.doSingleFire(stack, world, player); // Handing it over to the
	// neutral firing function
	return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void doSingleFire(ItemStack stack, World world, Entity entity) // Server
    // side
    {
	if (this.getCooldown(stack) != 0)
	{
	    return;
	} // Hasn't cooled down yet

	Helper.knockUserBack(entity, this.Kickback); // Kickback

	// SFX
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 1.0F);

	// Potential failure randomizer here? Causing the rockets to flip out
	// and go off in random directions

	if(!world.isRemote)
	{
	    float distanceMod = 5.0f;

	    int randNum = world.rand.nextInt(100) + 1; // 1-100

	    if (randNum >= 95)
	    {
		distanceMod = world.rand.nextInt(40);

		distanceMod -= 20; // Range of -20 to 20
	    }

	    // Firing
	    this.fireRocket(world, entity, 0, 0); // Center 1
	    this.fireRocket(world, entity, distanceMod, 0); // Right 2
	    this.fireRocket(world, entity, -distanceMod, 0);// Left 3
	    this.fireRocket(world, entity, 0, -distanceMod);// Top 4
	}

	this.consumeAmmo(stack, entity, 4);
	this.setCooldown(stack, this.Cooldown);
    }

    private void fireRocket(World world, Entity entity, float spreadHor, float spreadVert)
    {
	SmallRocket rocket = new SmallRocket(world, entity, (float) this.Speed, spreadHor, spreadVert);

	// Random Damage
	int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
	// is 10, then the range will
	// be 10
	int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
	// and 10
	dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
	// the proper damage range (10-20)

	// Properties
	rocket.damage = dmg;
	rocket.fireDuration = this.FireDur;
	rocket.explosionSize = this.ExplosionSize;
	rocket.dmgTerrain = this.dmgTerrain;

	// Firing
	world.spawnEntity(rocket);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 4)", 4).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 6)", 6).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.3 BPT (Blocks Per Tick))", 1.3)
		.getDouble();

	this.Knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 2)", 2)
		.getInt();
	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 1)", 1)
		.getInt();

	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 10 ticks)", 10).getInt();

	this.FireDur = config.get(this.name, "How long is what I hit on fire? (default 6s)", 6).getInt();

	this.ExplosionSize = config.get(this.name,
		"How big are my explosions? (default 1.0 blocks, for no terrain damage. TNT is 4.0 blocks)", 1.0)
		.getDouble();
	this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true)
		.getBoolean(true);

	this.isMobUsable = config
		.get(this.name, "Can I be used by QuiverMobs? (default false. A bit too high-power for them.)", false)
		.getBoolean();
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One QuadBox (empty) An upgrade from the regular Dragonbox (so 3
	    // more flint&steel + Pistons for reloading mechanism + more
	    // barrels)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "ddd", "pdp", "sts", 'p', Blocks.PISTON,
		    's', Blocks.STICKY_PISTON, 't', Blocks.TRIPWIRE_HOOK, 'd',
		    Helper.getWeaponStackByClass(DragonBox.class, true));
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	ItemStack stack = Helper.getAmmoStack(RocketBundle.class, 0);

	GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(stack.getItem(), 8));
    }
}
