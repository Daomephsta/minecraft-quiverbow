package com.domochevsky.quiverbow.weapons.base;

import com.domochevsky.quiverbow.ammo._AmmoBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class MagazineFedWeapon extends _WeaponBase
{
	protected final Item ammo;

	public MagazineFedWeapon(String name, _AmmoBase ammo, int maxAmmo)
	{
		super(name, maxAmmo);
		this.ammo = ammo;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (this.getDamage(stack) >= stack.getMaxDamage())
		{
			return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
		} // Is empty

		if (player.isSneaking()) // Dropping the magazine
		{
			this.dropMagazine(world, stack, player);
			return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
		}

		firingBehaviour.fire(stack, world, player, hand);
		// neutral firing function
		return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	public void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 120);
			return;
		}

		if (!world.isRemote)
		{
			ItemStack clipStack = new ItemStack(getAmmo(), 1, stack.getItemDamage());

			// Creating the clip
			EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
			entityitem.setPickupDelay(40);
			world.spawnEntity(entityitem);
		}
		stack.setItemDamage(stack.getMaxDamage()); // Emptying out
		doUnloadFX(world, entity);
	}

	public Item getAmmo()
	{
		return ammo;
	}

	protected void doUnloadFX(World world, Entity entity)
	{

	}
}
