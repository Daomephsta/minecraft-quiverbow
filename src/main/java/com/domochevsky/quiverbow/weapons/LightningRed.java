package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.RedstoneMagazine;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.RedLight;
import com.domochevsky.quiverbow.util.Utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class LightningRed extends _WeaponBase
{
    public LightningRed() // At 4 redstone used per shot that's 64 redstone per
			  // magazine
    {
	super("lightning_red", 16);

	ItemStack ammo = Helper.getAmmoStack(RedstoneMagazine.class, 0);
	this.setMaxDamage(ammo.getMaxDamage()); // Fitting our max capacity to
						// the magazine
    }

    private int PassThroughMax;
    private int MaxTicks;

    /*@SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
	this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/LightningRed");
	this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/LightningRed_Empty");
    }*/

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	if (this.getDamage(stack) >= this.getMaxDamage())
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // Is empty

	if (player.isSneaking()) // Dropping the magazine
	{
	    this.dropMagazine(world, stack, player);
	    return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	if (this.getDamage(stack) >= this.getMaxDamage() - 3)
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // Needs at least 4 redstone per shot

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

	// SFX
	Utils.playSoundAtEntityPos(entity, SoundEvents.ENTITY_LIGHTNING_THUNDER, 1.0F, 0.5F);
	Utils.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_BLAST, 2.0F, 0.1F);

	NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.REDSTONE, (byte) 4);

	// Firing
	RedLight shot = new RedLight(world, entity, (float) this.Speed);

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
	shot.targetsHitMax = this.PassThroughMax; // The maximum number of
						  // entities to punch through
						  // before ending
	shot.ignoreFrustumCheck = true;
	shot.ticksInAirMax = this.MaxTicks;

	world.spawnEntity(shot); // Firing!

	this.setCooldown(stack, this.Cooldown);
	if (this.consumeAmmo(stack, entity, 4))
	{
	    this.dropMagazine(world, stack, entity);
	}
    }

    private void dropMagazine(World world, ItemStack stack, Entity entity)
    {
	if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
	{
	    this.setCooldown(stack, 60);
	    return;
	}

	ItemStack clipStack = Helper.getAmmoStack(RedstoneMagazine.class, stack.getItemDamage()); // Unloading
												  // all
												  // ammo
												  // into
												  // that
												  // clip

	stack.setItemDamage(this.getMaxDamage()); // Emptying out

	// Creating the clip
	EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
	entityitem.setDefaultPickupDelay();

	// And dropping it
	if (entity.captureDrops)
	{
	    entity.capturedDrops.add(entityitem);
	}
	else
	{
	    world.spawnEntity(entityitem);
	}

	// SFX
	Utils.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
    }

    @Override
    void doCooldownSFX(World world, Entity entity) // Server side. Only done
						   // when held
    {
	Utils.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 0.2F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 8)", 8).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 16)", 16).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 5.0 BPT (Blocks Per Tick))", 5.0)
		.getDouble();
	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
		.getInt();
	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 40 ticks. That's 2 sec)", 40)
		.getInt();

	this.PassThroughMax = config
		.get(this.name, "Through how many entities and blocks can I punch, tops? (default 5)", 5).getInt();
	this.MaxTicks = config.get(this.name, "How long does my beam exist, tops? (default 60 ticks)", 60).getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One Lightning Red (empty)
	    GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "q q", "qiq", "iti", 'q', Items.QUARTZ,
		    'i', Items.IRON_INGOT, 't', Blocks.TRIPWIRE_HOOK);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	Helper.registerAmmoRecipe(RedstoneMagazine.class, this);
    }

    @Override
    public String getModelTexPath(ItemStack stack) // The model texture path
    {
	if (stack.getItemDamage() >= stack.getMaxDamage())
	{
	    return "LightningRed_empty";
	} // empty
	if (this.getCooldown(stack) > 0)
	{
	    return "LightningRed_hot";
	} // Cooling down

	return "LightningRed"; // Regular
    }
}
