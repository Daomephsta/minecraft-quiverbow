package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.FiringBehaviourBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class PowderKnuckle extends WeaponBase
{
	protected double explosionSize;
	protected boolean dmgTerrain;

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

		world.createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), (float) this.explosionSize, true);

		NetHelper.sendParticleMessageToAllPlayers(world, player.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
				(byte) 4); // smoke

		return EnumActionResult.SUCCESS;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		if (this.getDamage(stack) >= stack.getMaxDamage())
		{
			entity.attackEntityFrom(DamageSource.causePlayerDamage(player), this.damageMin);
			entity.hurtResistantTime = 0; // No invincibility frames

			return false; // We're not loaded, getting out of here with minimal
			// damage
		}

		this.consumeAmmo(stack, entity, 1);

		// SFX
		NetHelper.sendParticleMessageToAllPlayers(entity.world, player.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
				(byte) 4); // smoke

		// Dmg
		entity.setFire(2); // Setting fire to them for 2 sec, so pigs can drop
		// cooked porkchops
		entity.world.createExplosion(player, entity.posX, entity.posY + 0.5D, entity.posZ, (float) this.explosionSize,
				this.dmgTerrain); // 4.0F is TNT

		entity.attackEntityFrom(DamageSource.causePlayerDamage(player), this.damageMax); // Dealing
		// damage
		// directly.
		// Screw
		// weapon
		// attributes

		return false;
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What's my minimum damage, when I'm empty? (default 1)", 1).getInt();
		this.damageMax = config.get(this.name, "What's my maximum damage when I explode? (default 18)", 18).getInt();

		this.explosionSize = config
				.get(this.name, "How big are my explosions? (default 1.5 blocks. TNT is 4.0 blocks)", 1.5).getDouble();
		this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true)
				.getBoolean(true);

		this.isMobUsable = config.get(this.name,
				"Can I be used by QuiverMobs? (default false. They don't know where the trigger on this thing is.)",
				false).getBoolean(false);
	}
}
