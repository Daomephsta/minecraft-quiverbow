package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ai.AITargeting;
import com.domochevsky.quiverbow.projectiles.WaterShot;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class AquaAccelerator extends WeaponBase
{
	public AquaAccelerator()
	{
		super("aqua_accelerator", 1);
		this.setCreativeTab(CreativeTabs.TOOLS); // This is a tool
		setFiringBehaviour(new SingleShotFiringBehaviour<AquaAccelerator>(this,
				(world, weaponStack, entity, data) -> new WaterShot(world, entity,
						(float) ((WeaponBase) weaponStack.getItem()).speed)));
	}

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

		firingBehaviour.fire(stack, world, player, hand);
		// neutral firing function

		return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
	}

	private void checkReloadFromWater(ItemStack stack, World world, EntityPlayer player)
	{
		RayTraceResult movingobjectposition = AITargeting.getRayTraceResultFromPlayer(world, player, 8.0D);
		FillBucketEvent event = new FillBucketEvent(player, stack, world, movingobjectposition);

		if (MinecraftForge.EVENT_BUS.post(event))
		{
			return;
		}

		RayTraceResult movObj = AITargeting.getRayTraceResultFromPlayer(world, player, 8.0D);

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
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);
		this.speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5)
				.getDouble();
		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false)", false)
				.getBoolean(true);
	}
}
