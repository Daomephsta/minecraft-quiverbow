package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderShot;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.util.Utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EnderRifle extends _WeaponBase
{
    public EnderRifle()
    {
	super("ender_rifle", 8);
    }

    public int ZoomMax;
    private double DmgIncrease;

    /*@SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
	this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderRifle");
	this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderRifle_Empty");
    }*/

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	if (this.getDamage(stack) >= this.getMaxDamage())
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

	// Firing
	EnderShot shot = new EnderShot(world, entity, (float) this.Speed); // Create
									   // the
									   // projectile

	shot.damage = this.DmgMin;
	shot.damage_Max = this.DmgMax;
	shot.damage_Increase = this.DmgIncrease; // Increases damage each tick
						 // until the max has been
						 // reached

	shot.knockbackStrength = this.Knockback;

	world.spawnEntity(shot); // Pew.

	// SFX
	Utils.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
	NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_NORMAL, (byte) 1); // smoke

	this.consumeAmmo(stack, entity, 1);
	this.setCooldown(stack, this.Cooldown);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) // Overhauled
    {
	if (world.isRemote) // Not doing this on client side
	{
	    // ZoomInterface.checkClientZoom(world, entity, stack,
	    // this.ZoomMax); // client zoom
	    return;
	}

	if (this.getCooldown(stack) > 0)
	{
	    this.setCooldown(stack, this.getCooldown(stack) - 1);
	} // Cooling down
	if (this.getCooldown(stack) == 1)
	{
	    this.doCooldownSFX(world, entity);
	} // One tick before cooldown is done with, so SFX now
    }

    @Override
    void doCooldownSFX(World world, Entity entity)
    {
	Utils.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.7F, 0.2F);
	NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_NORMAL, (byte) 1); // smoke
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 4)", 4).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 16)", 16).getInt();

	this.DmgIncrease = config.get(this.name,
		"By what amount does my damage rise? (default 1.0, for +1.0 DMG per tick of flight)", 1.0).getDouble();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 3.0 BPT (Blocks Per Tick))", 3.0)
		.getDouble();

	this.Knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 1)", 1)
		.getInt();
	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
		.getInt();

	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 25 ticks)", 25).getInt();

	this.ZoomMax = (config.get(this.name, "How far can I zoom in? (default 30. Less means more zoom)", 30)
		.getInt());

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One ender rifle (empty)
	    GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "aza", "bcy", "xzx", 'x',
		    Blocks.OBSIDIAN, 'y', Blocks.TRIPWIRE_HOOK, 'z', Items.IRON_INGOT, 'a', Items.ENDER_EYE, 'b',
		    Blocks.PISTON, 'c', Blocks.STICKY_PISTON);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(Items.IRON_INGOT, 1));
    }

    @Override
    public String getModelTexPath(ItemStack stack) // The model texture path
    {
	if (stack.getItemDamage() >= stack.getMaxDamage())
	{
	    return "EnderRifle_empty";
	}
	if (this.getCooldown(stack) > 0)
	{
	    return "EnderRifle_hot";
	} // Cooling down

	return "EnderRifle"; // Regular
    }
}
