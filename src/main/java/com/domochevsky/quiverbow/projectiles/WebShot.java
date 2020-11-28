package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class WebShot extends ProjectileBase
{
	public WebShot(World world)
	{
		super(world);
	}

	public WebShot(World world, Entity entity, WeaponProperties properties)
	{
		super(world);
		this.doSetup(entity, properties.getProjectileSpeed());
	}

	@Override
	public void onImpact(RayTraceResult target)
	{
		BlockPos pos;
		if (target.entityHit != null) // hit a entity
			pos = new BlockPos(target.entityHit.posX, target.entityHit.posY, target.entityHit.posZ);
		else
			pos = target.getBlockPos().offset(target.sideHit);

		// Is the space free?
		IBlockState hitState = this.world.getBlockState(pos);
		if (hitState.getBlock().isReplaceable(world, pos) || hitState.getMaterial() == Material.PLANTS)
		{
			// Putting a web there!
			this.world.setBlockState(pos, Blocks.WEB.getDefaultState(), 3);
		}

		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SNOWBALL,
				(byte) 4);
		this.playSound(SoundEvents.ENTITY_GENERIC_SPLASH, 0.4F, 2.0F);

		this.setDead(); // We've hit something, so begone with the projectile
	}
}
