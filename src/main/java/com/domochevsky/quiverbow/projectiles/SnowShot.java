package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SnowShot extends ProjectilePotionEffect
{
	public SnowShot(World world)
	{
		super(world);
	}

	public SnowShot(World world, Entity entity, float speed, float accHor, float AccVert, PotionEffect... effects)
	{
		super(world, effects);
		this.doSetup(entity, speed, accHor, AccVert, entity.rotationYaw, entity.rotationPitch);
	}

	@Override
	public void onImpact(RayTraceResult target) // Server-side
	{
		if (target.entityHit != null)
		{
			super.onImpact(target);

			// Triple DMG vs Blazes, so applying twice more
			if (target.entityHit instanceof EntityBlaze)
			{
				target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity),
						(float) (this.damage * 2));
				target.entityHit.hurtResistantTime = 0;
			}
		}
		else
		{
			// Glass breaking
			Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 1);
		}

		// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SNOWBALL, (byte) 2);
		this.playSound(SoundEvents.BLOCK_LAVA_POP, 1.0F, 0.5F);

		this.setDead(); // We've hit something, so begone with the projectile
	}

	@Override
	public void doWaterEffect() // Called when this entity moves through water
	{
		// Checking for water here and turning it into ice
		BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);

		IBlockState state = this.world.getBlockState(pos);

		if (state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER)
		{
			// Hit a (flowing) water block, so turning that into ice now
			this.world.setBlockState(pos, Blocks.ICE.getDefaultState(), 3);
		}
	}

	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];

		type[0] = 3; // Type 3, item
		type[1] = 7; // Length, misused as item type. snowball
		type[2] = 2; // Width

		return type;
	}
}
