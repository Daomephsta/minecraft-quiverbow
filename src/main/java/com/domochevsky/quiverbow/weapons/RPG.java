package com.domochevsky.quiverbow.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.BigRocket;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RPG extends _WeaponBase
{
    public RPG()
    {
	super("rocket_launcher", 1);
    }

    public double ExplosionSize;
    private int travelTime; // How many ticks the rocket can travel before
    // exploding
    private boolean dmgTerrain; // Can our projectile damage terrain?

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
	if (this.getCooldown(stack) > 0)
	{
	    return;
	} // Hasn't cooled down yet

	Helper.knockUserBack(entity, this.Kickback); // Kickback

	if(!world.isRemote)
	{
	    // Firing
	    BigRocket rocket = new BigRocket(world, entity, (float) this.Speed); // Projectile
	    // Speed.
	    // Inaccuracy
	    // Hor/Vert
	    rocket.explosionSize = this.ExplosionSize;
	    rocket.travelTicksMax = this.travelTime;
	    rocket.dmgTerrain = this.dmgTerrain;

	    world.spawnEntity(rocket); // shoom.
	}

	// SFX
	entity.playSound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 2.0F, 0.6F);

	this.setCooldown(stack, 60);
	this.consumeAmmo(stack, entity, 1);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);
	this.Speed = config.get(this.name, "How fast are my projectiles? (default 2.0 BPT (Blocks Per Tick))", 2.0)
		.getDouble();
	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
		.getInt();
	this.ExplosionSize = config.get(this.name, "How big are my explosions? (default 4.0 blocks, like TNT)", 4.0)
		.getDouble();
	this.travelTime = config
		.get(this.name, "How many ticks can my rocket fly before exploding? (default 20 ticks)", 20).getInt();
	this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true)
		.getBoolean(true);

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One Firework Rocket Launcher (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "x  ", "yx ", "zyx", 'x', Blocks.PLANKS,
		    'y', Items.IRON_INGOT, 'z', Items.FLINT_AND_STEEL);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	// Fill the RPG with 1 rocket
	GameRegistry.addRecipe(new ItemStack(this), " ab", "zya", " x ", 'x',
		Helper.createEmptyWeaponOrAmmoStack(this, 1), 'y', Blocks.TNT, 'z', Blocks.PLANKS, 'a', Items.PAPER, 'b',
		Items.STRING);
    }
}
