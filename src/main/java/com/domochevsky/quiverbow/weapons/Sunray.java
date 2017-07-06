package com.domochevsky.quiverbow.weapons;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.SunLight;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Sunray extends _WeaponBase
{
    public Sunray()
    {
	super("sunray", 1);
    }

    private int MaxTicks;
    private int LightMin;
    private int FireDur;

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
	double dur = (1d / this.Cooldown) * (this.Cooldown - this.getCooldown(stack)); // Display
										       // durability
	return 1d - dur; // Reverse again. Tch
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);

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

	// Firing a beam that goes through walls
	SunLight shot = new SunLight(world, entity, (float) this.Speed);

	// Random Damage
	int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
						   // is 10, then the range will
						   // be 10
	int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
						     // and 10
	dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
			    // the proper damage range (10-20)

	// The moving end point
	shot.damage = dmg;
	shot.fireDuration = this.FireDur;

	shot.ignoreFrustumCheck = true;
	shot.ticksInAirMax = this.MaxTicks;

	world.spawnEntity(shot); // Firing!

	// SFX
	entity.playSound(SoundEvents.ENTITY_BLAZE_DEATH, 0.7F, 2.0F);
	entity.playSound(SoundEvents.ENTITY_FIREWORK_BLAST, 2.0F, 0.1F);

	this.setCooldown(stack, this.Cooldown);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) // Overhauled
													 // default
    {
	int light = world.getLight(entity.getPosition());

	if (light >= this.LightMin)
	{
	    if (this.getCooldown(stack) > 0)
	    {
		this.setCooldown(stack, this.getCooldown(stack) - 1);
	    } // Cooling down
	    if (this.getCooldown(stack) == 1)
	    {
		this.doCooldownSFX(world, entity);
	    } // One tick before cooldown is done with, so SFX now
	}
    }

    @Override
    void doCooldownSFX(World world, Entity entity) // Server side
    {
	entity.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 0.5F);
	entity.playSound(SoundEvents.ENTITY_CAT_HISS, 0.6F, 2.0F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage are my arrows dealing, at least? (default 14)", 14).getInt();
	this.DmgMax = config.get(this.name, "What damage are my arrows dealing, tops? (default 20)", 20).getInt();

	this.Speed = 4.0f;
	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
		.getInt();

	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 120 ticks)", 120).getInt();

	this.FireDur = config.get(this.name, "How long is what I hit on fire? (default 10s)", 10).getInt();
	this.MaxTicks = config.get(this.name, "How long does my beam exist, tops? (default 60 ticks)", 60).getInt();
	this.LightMin = config.get(this.name, "What light level do I need to recharge, at least? (default 12)", 12)
		.getInt();

	this.isMobUsable = config
		.get(this.name, "Can I be used by QuiverMobs? (default false. Too damn bright for their taste.)", false)
		.getBoolean();
    }

    @Override
    public void addRecipes()
    {
	if (Enabled)
	{
	    // Using a beacon and solar panels/Daylight Sensors, meaning a
	    // nether star is required. So this is a high power item
	    GameRegistry.addRecipe(new ItemStack(this), "bs ", "oos", " rt", 'b', Blocks.BEACON, 'o', Blocks.OBSIDIAN,
		    's', Blocks.DAYLIGHT_DETECTOR, 't', Blocks.TRIPWIRE_HOOK, 'r', Items.REPEATER);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
	subItems.add(new ItemStack(item, 1, 0)); // Only one, and it's full
    }
}
