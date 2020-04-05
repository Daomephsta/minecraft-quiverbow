package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EnderShot extends ProjectileBase
{
	public int damageMax; // How much damage we can deal, tops
	public double damageIncrease; // By how much we increase our current
	// damage, each tick

	public EnderShot(World world)
	{
		super(world);
	}

	public EnderShot(World world, Entity entity, float speed)
	{
		super(world);
		this.doSetup(entity, speed);
	}

	@Override
	public boolean doDropOff()
	{
		return false;
	} // If this returns false then we won't care about gravity

	@Override
	public void doFlightSFX()
	{
		// Doing our own (reduced) gravity
		this.motionY -= 0.025; // Default is 0.05

		if (this.damage < this.damageMax)
		{
			this.damage += this.damageIncrease;
		} // Increasing damage once per tick until we reach the max

		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.PORTAL, (byte) 3);
	}

	@Override
	public void onImpact(RayTraceResult target) // Server-side
	{
		if (target.entityHit != null) // We hit a living thing!
		{
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity),
					this.damage); // Damage gets applied here
			target.entityHit.hurtResistantTime = 0; // No immunity frames

			if (this.knockbackStrength > 0)
			{
				float f3 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
				if (f3 > 0.0F)
				{
					target.entityHit.addVelocity(
							this.motionX * this.knockbackStrength * 0.6000000238418579D / f3, 0.1D,
							this.motionZ * this.knockbackStrength * 0.6000000238418579D / f3);
				}
			}

			this.setDead(); // Hit something, so begone.
		}
		else // Hit the terrain
		{
			// Glass breaking
			if (Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 1) && this.targetsHit < 2)
			{
				this.targetsHit += 1;
			}
			else
			{
				this.setDead();
			} // Going straight through glass
		}
	}
}
