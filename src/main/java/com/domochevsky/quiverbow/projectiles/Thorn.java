package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class Thorn extends ProjectileBase
{
	public Thorn(World world)
	{
		super(world);
	}

	public Thorn(World world, Entity entity, float speed)
	{
		super(world);
		this.doSetup(entity, speed);
	}

	public Thorn(World world, Entity entity, float speed, float yaw, float pitch)
	{
		super(world);
		this.doSetup(entity, speed, 0, 0, yaw, pitch);
	}

	@Override
	public void onImpact(RayTraceResult movPos) // Server-side
	{
		if (movPos.entityHit != null)
		{
			movPos.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity),
					this.damage);
			movPos.entityHit.hurtResistantTime = 0; // No rest for the wicked
		}
		else // Hit the terrain
		{
			Helper.tryBlockBreak(this.world, this, movPos.getBlockPos(), 1);
		}

		// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.CRIT, (byte) 1);

		this.setDead(); // We've hit something, so begone with the projectile
	}
}
