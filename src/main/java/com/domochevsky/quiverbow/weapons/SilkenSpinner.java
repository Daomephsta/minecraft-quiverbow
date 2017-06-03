package com.domochevsky.quiverbow.weapons;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.WebShot;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SilkenSpinner extends _WeaponBase
{
    public SilkenSpinner()
    {
	super("silken_spinner", 8);
	this.setCreativeTab(CreativeTabs.TOOLS); // This is a tool
    }

    /*
     * @SideOnly(Side.CLIENT)
     * 
     * @Override public void registerIcons(IIconRegister par1IconRegister) {
     * this.Icon =
     * par1IconRegister.registerIcon("quiverchevsky:weapons/WebGun");
     * this.Icon_Empty =
     * par1IconRegister.registerIcon("quiverchevsky:weapons/WebGun_Empty"); }
     */

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

	// SFX
	entity.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);

	// Firing
	WebShot projectile = new WebShot(world, entity, (float) this.Speed);
	world.spawnEntity(projectile); // Firing!

	this.consumeAmmo(stack, entity, 1);
	this.setCooldown(stack, this.Cooldown);
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
	    GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "ihi", "gpg", "tsi", 'p', Blocks.PISTON,
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

    @Override
    public String getModelTexPath(ItemStack stack) // The model texture path
    {
	if (stack.getItemDamage() >= stack.getMaxDamage())
	{
	    return "WebGun_empty";
	} // empty

	return "WebGun"; // Regular
    }
}
