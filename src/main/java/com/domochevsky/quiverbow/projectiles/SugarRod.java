package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SugarRod extends ProjectileBase
{
	public SugarRod(World world)
	{
		super(world);
	}

	public SugarRod(World world, Entity entity, float speed, float accHor, float accVert)
	{
		super(world);
		this.doSetup(entity, speed, accHor, accVert);
	}

	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null)
		{
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity),
					this.damage);
			target.entityHit.hurtResistantTime = 0;
		}
		else // Hit the terrain
		{
			Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 1); // Glass
																				// breaking
		}

		// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.CRIT, (byte) 1);

		this.setDead(); // We've hit something, so begone with the projectile
	}

	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];

		type[0] = 2; // Type 2, generic projectile
		type[1] = 5; // Length
		type[2] = 2; // Width

		return type;
	}

	@Override
	public String getEntityTexturePath()
	{
		return "textures/entity/sugar.png";
	} // Our projectile texture
}
