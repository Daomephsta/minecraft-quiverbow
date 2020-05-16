package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.util.InventoryHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.IScopedWeapon;
import com.domochevsky.quiverbow.weapons.base.WeaponBow;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

// TODO: predictive OGL rendering
public class EnderBow extends WeaponBow implements IScopedWeapon
{
	public EnderBow()
	{
		super("ender_bow", 256);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder()
				.intProperty(CommonProperties.PROP_MAX_ZOOM, CommonProperties.COMMENT_MAX_ZOOM, 30).build();
	}

	@Override
	public int getMaxZoom()
	{
		return getProperties().getInt(CommonProperties.PROP_MAX_ZOOM);
	}

	@Override
	public boolean shouldZoom(World world, EntityPlayer player, ItemStack stack)
	{
		return player.getHeldItemMainhand().getItem() == this || player.getHeldItemOffhand().getItem() == this;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft)
	{
		int chargeTime = this.getMaxItemUseDuration(stack) - timeLeft;

		if (entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entityLiving;
			// Either creative mode or infinity enchantment is higher than 0. Not using arrows
			boolean freeShot = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;

			ArrowLooseEvent event = new ArrowLooseEvent(player, stack, world, chargeTime, false);
			MinecraftForge.EVENT_BUS.post(event);

			if (event.isCanceled())
			{
				return;
			} // Not having it

			chargeTime = event.getCharge();

			if (freeShot || player.inventory.hasItemStack(new ItemStack(Items.ARROW)))
			{
				float f = chargeTime / 20.0F;
				f = (f * f + f * 2.0F) / 3.0F;

				if (f < 0.1D)
				{
					return;
				}
				if (f > 1.0F)
				{
					f = 1.0F;
				}

				EntityArrow entityarrow = Helper.createArrow(world, player);
				entityarrow.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, f * 3.0F,
						0.5F);

				if (f == 1.0F)
				{
					entityarrow.setIsCritical(true);
				}

				stack.damageItem(1, player);
				Helper.playSoundAtEntityPos(player, SoundEvents.ENTITY_ARROW_SHOOT, 1.0F,
						1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

				if (freeShot)
				{
					entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
				}
				else
				{
					InventoryHelper.consumeItem(player, Items.ARROW, 1);
				}

				if (!world.isRemote)
				{
					world.spawnEntity(entityarrow);
				} // pew.
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		ArrowNockEvent event = new ArrowNockEvent(player, stack, hand, world, player.capabilities.isCreativeMode);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled())
		{
			return event.getAction();
		}
		player.setActiveHand(hand);

		return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		if(!isInCreativeTab(tab)) return;
		subItems.add(new ItemStack(this, 1, 0));
	}
}
