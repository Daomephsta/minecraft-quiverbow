package com.domochevsky.quiverbow.weapons;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.FillBucketEvent;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.AI.AI_Targeting;
import com.domochevsky.quiverbow.projectiles.WaterShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class AquaAccelerator extends _WeaponBase
{
    public AquaAccelerator()
    {
	super("aqua_accelerator", 1);
	this.setCreativeTab(CreativeTabs.TOOLS); // This is a tool
    }

    /*
     * @SideOnly(Side.CLIENT)
     * 
     * @Override public void registerIcons(IIconRegister par1IconRegister) {
     * this.Icon =
     * par1IconRegister.registerIcon("quiverchevsky:weapons/WaterGun");
     * this.Icon_Empty =
     * par1IconRegister.registerIcon("quiverchevsky:weapons/WaterGun_Empty"); }
     */

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);

	// Not doing this on client side
	if (this.getDamage(stack) >= stack.getMaxDamage()) // Is empty
	{
	    this.checkReloadFromWater(stack, world, player);// See if you can
							    // reload
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	}

	this.doSingleFire(stack, world, player); // Handing it over to the
						 // neutral firing function

	return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void doSingleFire(ItemStack stack, World world, Entity entity) // Server
									  // side,
									  // mob
									  // usable
    {
	if (this.getCooldown(stack) > 0)
	{
	    return;
	} // Hasn't cooled down yet

	// SFX
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);

	// Firing
	WaterShot projectile = new WaterShot(world, entity, (float) Speed);
	world.spawnEntity(projectile);

	this.consumeAmmo(stack, entity, 1);
	this.setCooldown(stack, this.Cooldown); // Cooling down now
    }

    private void checkReloadFromWater(ItemStack stack, World world, EntityPlayer player)
    {
	RayTraceResult movingobjectposition = AI_Targeting.getRayTraceResultFromPlayer(world, player, 8.0D);
	FillBucketEvent event = new FillBucketEvent(player, stack, world, movingobjectposition);

	if (MinecraftForge.EVENT_BUS.post(event))
	{
	    return;
	}

	RayTraceResult movObj = AI_Targeting.getRayTraceResultFromPlayer(world, player, 8.0D);

	if (movObj == null)
	{
	    return;
	} // Didn't click on anything in particular
	else
	{
	    if (movObj.typeOfHit == RayTraceResult.Type.BLOCK)
	    {
		if (!world.canMineBlockBody(player, movObj.getBlockPos()))
		{
		    return;
		} // Not allowed to mine this, getting out of here
		if (!player.canPlayerEdit(movObj.getBlockPos(), movObj.sideHit, stack))
		{
		    return;
		} // Not allowed to edit this, getting out of here

		IBlockState state = world.getBlockState(movObj.getBlockPos());

		// Is this water?
		if (state.getBlock() == Blocks.WATER)
		{
		    world.setBlockToAir(movObj.getBlockPos());
		    stack.setItemDamage(0);

		    return;
		}
		// else, not water
	    }
	    // else, didn't click on a block
	}
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);
	this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5)
		.getDouble();
	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false)", false)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (Enabled)
	{
	    // One Aqua Accelerator (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "ihi", "gpg", "iti", 'p', Blocks.PISTON,
		    't', Blocks.TRIPWIRE_HOOK, 'i', Items.IRON_INGOT, 'h', Blocks.HOPPER, 'g', Blocks.GLASS_PANE);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	// Fill the AA with one water bucket
	GameRegistry.addShapelessRecipe(new ItemStack(this), Items.WATER_BUCKET,
		Helper.createEmptyWeaponOrAmmoStack(this, 1) // Empty
	);
    }
}
