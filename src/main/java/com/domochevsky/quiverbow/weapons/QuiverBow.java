package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBow;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class QuiverBow extends WeaponBow
{
	public QuiverBow()
	{
		super("quiverbow", 256);
	}

	@Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft)
	{
	    // Reduces the durability by the ItemInUseCount (probably 1 for anything that isn't a tool)
		int j = this.getMaxItemUseDuration(stack) - timeLeft;

		if (entityLiving instanceof EntityPlayer)
		{
			ArrowLooseEvent event = new ArrowLooseEvent((EntityPlayer) entityLiving, stack, world, j, false);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled())
			{
				return;
			}
			j = event.getCharge();
		}

		if (this.getDamage(stack) == stack.getMaxDamage())
		{
			return;
		} // No arrows in the quiver? Getting out of here early

		float f = j / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;

		if (f < 0.1D)
		{
			return;
		}
		if (f > 1.0F)
		{
			f = 1.0F;
		}

		Helper.playSoundAtEntityPos(entityLiving, SoundEvents.ENTITY_ARROW_SHOOT, 1.0F,
				1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

		if (!world.isRemote)
		{
			EntityArrow entityarrow = Helper.createArrow(world, entityLiving);
			entityarrow.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, f * 3.0F,
					1.0F);
			if (f == 1.0F)
			{
				entityarrow.setIsCritical(true);
			}

			if (entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving).capabilities.isCreativeMode)
			{
				entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
			}
			else
			{
				entityarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
				stack.setItemDamage(this.getDamage(stack) + 1); // Reversed. MORE Damage for a shorter durability bar
			}

			world.spawnEntity(entityarrow);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		ArrowNockEvent event = new ArrowNockEvent(player, stack, hand, world, false);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled())
		{
			return event.getAction();
		}

		// Are there any arrows in the quiver?
		if (this.getDamage(stack) < stack.getMaxDamage())
		{
			player.setActiveHand(hand);
		}

		return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().build();
	}
}
