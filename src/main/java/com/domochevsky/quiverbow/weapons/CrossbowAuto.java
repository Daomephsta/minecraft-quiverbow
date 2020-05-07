package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponCrossbow;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class CrossbowAuto extends WeaponCrossbow
{
	public CrossbowAuto()
	{
		super("auto_crossbow", 8);
		setFiringBehaviour(new SingleShotFiringBehaviour<CrossbowAuto>(this, (world, weaponStack, entity, data, properties) ->
		{
			EntityArrow entityarrow = Helper.createArrow(world, entity);

			// Random Damage
			int dmg_range = properties.getDamageMin() - properties.getDamageMin(); // If max dmg is 20
															// and min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 0
			// and 10
			dmg += properties.getDamageMin(); // Adding the min dmg of 10 back on top,
									// giving us
			// the proper damage range (10-20)

			entityarrow.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0F, properties.getProjectileSpeed(), 0.5F);
			entityarrow.setDamage(dmg);
			entityarrow.setKnockbackStrength(properties.getKnockback());

			return entityarrow;
		})
		{
			@Override
			public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
			{
				super.fire(stack, world, entity, hand);
				CrossbowAuto.setChambered(stack, world, entity, false);
			}
		});
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (this.getDamage(stack) >= stack.getMaxDamage())
		{
			return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
		} // Is empty

		if (!CrossbowAuto.isChambered(stack)) // No arrow on the rail
		{
			if (player.isSneaking())
			{
				CrossbowAuto.setChambered(stack, world, player, true);
			} // Setting up a new arrow

			return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
		}

		if (player.isSneaking())
		{
			return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
		} // Still sneaking, even though you have an arrow on the rail? Not
			// having it

		firingBehaviour.fire(stack, world, player, hand);
		return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	public static boolean isChambered(ItemStack stack)
	{
		if (stack.getTagCompound() == null)
		{
			return false;
		} // Doesn't have a tag

		return stack.getTagCompound().getBoolean("isChambered");
	}

	private static void setChambered(ItemStack stack, World world, Entity entity, boolean toggle)
	{
		if (stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		} // Init

		stack.getTagCompound().setBoolean("isChambered", toggle); // Done, we're
		// good to go
		// again

		// SFX
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.8F, 0.5F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(10).maximumDamage(16).projectileSpeed(2.5F).knockback(1)
				.cooldown(10).build();
	}
}
