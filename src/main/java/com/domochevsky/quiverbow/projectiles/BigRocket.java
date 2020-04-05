package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BigRocket extends ProjectileBase
{
	public int travelTicksMax;
	public boolean dmgTerrain;

	public BigRocket(World world)
	{
		super(world);
	}

	public BigRocket(World world, Entity entity, float speed)
	{
		super(world);
		this.doSetup(entity, speed);
	}

	@Override
	public void onImpact(RayTraceResult target) // Server-side
	{
		boolean griefing = true; // Allowed by default

		if (this.shootingEntity instanceof EntityPlayer)
		{
			griefing = this.dmgTerrain; // It's up to player settings to
			// allow/forbid this
		}
		else
		{
			griefing = this.world.getGameRules().getBoolean("mobGriefing"); // Are
			// we
			// allowed
			// to
			// break
			// things?
		}

		this.world.createExplosion(this, this.posX, this.posY, this.posZ, (float) this.explosionSize, griefing); // Bewm

		this.setDead(); // We've hit something, so begone with the projectile
	}

	@Override
	public void doFlightSFX()
	{
		if (travelTicksMax > 0) // We have a fixed travel time, so lesse...
		{
			if (this.ticksExisted > this.travelTicksMax) // Our fuse has run out
			{
				boolean griefing = true; // Allowed by default

				if (this.shootingEntity instanceof EntityPlayer)
				{
					griefing = this.dmgTerrain; // It's up to player settings to
					// allow/forbid this
				}
				else
				{
					griefing = this.world.getGameRules().getBoolean("mobGriefing"); // Are
					// we
					// allowed
					// to
					// break
					// things?
				}

				this.world.createExplosion(this, this.posX, this.posY, this.posZ, (float) this.explosionSize, griefing); // Bewm

				this.setDead(); // We've hit something, so begone with the
				// projectile
			}
		}
		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.FIREWORKS_SPARK, (byte) 8);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float par2) // Big
	// rockets
	// can be
	// swatted
	// out of
	// the way
	// with a
	// bit of
	// expertise
	{
		if (this.isEntityInvulnerable(source))
		{
			return false;
		}
		else // Not invulnerable
		{
			this.markVelocityChanged();

			if (source.getTrueSource() != null) // Damaged by a entity
			{
				Vec3d vec3 = source.getTrueSource().getLookVec(); // Which is
				// looking that
				// way...

				if (vec3 != null)
				{
					this.motionX = vec3.x;
					this.motionY = vec3.y;
					this.motionZ = vec3.z;
				}

				if (source.getTrueSource() instanceof EntityLivingBase)
				{
					this.shootingEntity = (EntityLivingBase) source.getTrueSource();
				}

				return true;
			}
			// else, not damaged by an entity
		}

		return false;
	}
}
