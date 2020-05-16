package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ai.AITargeting;
import com.domochevsky.quiverbow.config.WeaponProperties;
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
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class AquaAccelerator extends WeaponBase
{
	public AquaAccelerator()
	{
		super("aqua_accelerator", 1);
		this.setCreativeTab(CreativeTabs.TOOLS); // This is a tool
		setFiringBehaviour(new SingleShotFiringBehaviour<AquaAccelerator>(this,
				(world, weaponStack, entity, data, properties) -> new WaterShot(world, entity,
						properties.getProjectileSpeed())));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (this.getDamage(stack) >= stack.getMaxDamage()) // Is empty
		{
			this.checkReloadFromWater(stack, world, player);
			return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
		}
		firingBehaviour.fire(stack, world, player, hand);
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
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().projectileSpeed(1.5F).build();
	}
}
