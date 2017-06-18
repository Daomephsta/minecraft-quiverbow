package com.domochevsky.quiverbow.weapons;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.SoulShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SoulCairn extends _WeaponBase
{
    public SoulCairn()
    {
	super("soul_cairn", 1);
	this.setCreativeTab(CreativeTabs.TOOLS); // Tool, so on the tool tab
    }

    /*
     * @SideOnly(Side.CLIENT)
     * 
     * @Override public void registerIcons(IIconRegister par1IconRegister) {
     * this.Icon =
     * par1IconRegister.registerIcon("quiverchevsky:weapons/SoulCairn");
     * this.Icon_Empty =
     * par1IconRegister.registerIcon("quiverchevsky:weapons/SoulCairn_Empty"); }
     */

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

	// Self Harm
	entity.attackEntityFrom(DamageSource.causeThrownDamage(entity, entity), 2); // A
										    // sacrifice
										    // in
										    // blood

	// Projectile
	SoulShot projectile = new SoulShot(world, entity, (float) this.Speed);
	world.spawnEntity(projectile); // Firing!

	// SFX
	entity.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
	entity.playSound(SoundEvents.BLOCK_NOTE_BASS, 1.0F, 0.4F);

	this.consumeAmmo(stack, entity, 1);
	this.setCooldown(stack, 20);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);
	this.Speed = config.get(this.name, "How fast are my projectiles? (default 3.0 BPT (Blocks Per Tick))", 3.0)
		.getDouble();
	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 4)", 4)
		.getInt();

	this.isMobUsable = config
		.get(this.name, "Can I be used by QuiverMobs? (default false. This is easily abusable.)", false)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One Soul Cairn (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "e e", "epe", "oto", 'o',
		    Blocks.OBSIDIAN, 'e', Blocks.END_STONE, 't', Blocks.TRIPWIRE_HOOK, 'p', Blocks.PISTON);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	// Reload with 1 diamond
	GameRegistry.addShapelessRecipe(new ItemStack(this), Items.DIAMOND,
		Helper.createEmptyWeaponOrAmmoStack(this, 1));
    }

    @Override
    public String getModelTexPath(ItemStack stack) // The model texture path
    {
	if (stack.getItemDamage() >= stack.getMaxDamage())
	{
	    return "SoulCairn_empty";
	} // empty

	return "SoulCairn"; // Regular
    }
}
