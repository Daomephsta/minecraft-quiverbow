package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.FiringBehaviourBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PowderKnuckle extends WeaponBase
{
	public PowderKnuckle()
	{
		super("powder_knuckles", 8);
		// Dummy behaviour, does nothing
		setFiringBehaviour(new FiringBehaviourBase<WeaponBase>(this)
		{
			@Override
			public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
			{}
		});
	}

	protected PowderKnuckle(String name, int maxAmmo)
	{
		super(name, maxAmmo);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		// Right click
		if (this.getDamage(stack) >= stack.getMaxDamage())
		{
			return EnumActionResult.FAIL;
		} // Not loaded

		if (!player.capabilities.isCreativeMode)
		{
			this.consumeAmmo(stack, player, 1);
		}

		world.createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), getProperties().getFloat(CommonProperties.PROP_EXPLOSION_SIZE), true);

		NetHelper.sendParticleMessageToAllPlayers(world, player, EnumParticleTypes.SMOKE_NORMAL,
				(byte) 4); // smoke

		return EnumActionResult.SUCCESS;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		if (this.getDamage(stack) >= stack.getMaxDamage())
		{
			entity.attackEntityFrom(DamageSource.causePlayerDamage(player), getProperties().getDamageMin());
			entity.hurtResistantTime = 0; // No invincibility frames

			return false; // We're not loaded, getting out of here with minimal damage
		}

		this.consumeAmmo(stack, entity, 1);

		// SFX
		NetHelper.sendParticleMessageToAllPlayers(entity.world, entity, EnumParticleTypes.SMOKE_NORMAL,
				(byte) 4); // smoke

		// Dmg
		entity.setFire(2); // Setting fire to them for 2 sec, so pigs can drop cooked porkchops
		entity.world.createExplosion(player, entity.posX, entity.posY + 0.5D, entity.posZ,
				getProperties().getFloat(CommonProperties.PROP_EXPLOSION_SIZE), getProperties().getBoolean(CommonProperties.PROP_DAMAGE_TERRAIN)); // 4.0F is TNT

		// Dealing damage directly. Screw weapon attributes
		entity.attackEntityFrom(DamageSource.causePlayerDamage(player), this.getProperties().getDamageMin());

		return false;
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(1).maximumDamage(18)
				.floatProperty(CommonProperties.PROP_EXPLOSION_SIZE, CommonProperties.COMMENT_EXPLOSION_SIZE, 1.5F)
				.booleanProperty(CommonProperties.PROP_DAMAGE_TERRAIN, CommonProperties.COMMENT_DAMAGE_TERRAIN, true)
				.build();
	}
}
