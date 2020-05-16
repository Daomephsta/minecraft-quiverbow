package com.domochevsky.quiverbow.weapons;

import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponCrossbow;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CrossbowDouble extends WeaponCrossbow
{
	public CrossbowDouble()
	{
		super("double_crossbow", 2, (world, weaponStack, entity, data, properties) ->
		{
			EntityArrow entityarrow = Helper.createArrow(world, entity);

			int dmg_range = properties.getDamageMax() - properties.getDamageMin();
			int dmg = properties.getDamageMin() + world.rand.nextInt(dmg_range + 1);

			entityarrow.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0F, properties.getProjectileSpeed(), 0.5F);
			entityarrow.setDamage(dmg);
			entityarrow.setKnockbackStrength(properties.getKnockback());

			return entityarrow;
		});
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity) // Server side
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.4F);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
		super.addInformation(stack, world, list, flags);
		if (this.getCooldown(stack) > 0)
			list.add(I18n.format(getUnlocalizedName() + ".cooldown",
					this.displayInSec(this.getCooldown(stack))));
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(14).maximumDamage(20).projectileSpeed(2.5F).knockback(2)
				.cooldown(25).mobUsable().build();
	}
}
