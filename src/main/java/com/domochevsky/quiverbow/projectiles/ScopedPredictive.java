package com.domochevsky.quiverbow.projectiles;

import java.util.List;

import com.domochevsky.quiverbow.Helper_Client;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

//TODO: Make better way of displaying arrow trajectory
public class ScopedPredictive extends _ProjectileBase
{
	public ScopedPredictive(World world)
	{
		super(world);
		this.ticksInGroundMax = 120;
	}

	public ScopedPredictive(World world, Entity entity, float speed)
	{
		super(world);
		this.doSetup(entity, speed);

		this.ticksInGroundMax = 120;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D
					/ Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f) * 180.0D
					/ Math.PI);
		}

		BlockPos stuckPos = new BlockPos(this.stuckBlockX, this.stuckBlockY, this.stuckBlockZ);
		IBlockState state = this.world.getBlockState(stuckPos);

		if (state.getMaterial() != Material.AIR)
		{
			AxisAlignedBB potentialAABB = state.getCollisionBoundingBox(this.world, stuckPos);

			if (potentialAABB != null && potentialAABB.isVecInside(new Vec3d(this.posX, this.posY, this.posZ)))
			{
				this.inGround = true; // Hit a non-air block, so we're now stuck
				// in the ground
			}
		}

		if (this.arrowShake > 0)
		{
			--this.arrowShake;
		} // Likely animation-relevant

		if (this.inGround)
		{
			doInGroundSFX(); // Stuck in the ground, so ground SFX is go

			if (state == this.stuckBlock)
			{
				++this.ticksInGround;

				if (this.ticksInGroundMax != 0 && this.ticksInGround > this.ticksInGroundMax)
				{
					this.setDead();
				} // Ticks max is not 0, so we care about it
				else if (this.ticksInGround == 1200)
				{
					this.setDead();
				} // Generally overaged. You're done
			}
			else // Not in the same block anymore, so starting to move again
			{
				this.inGround = false;

				this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);

				this.ticksInGround = 0;
				this.ticksInAir = 0;
			}
		}
		else
		{
			++this.ticksInAir; // Aging along

			Vec3d currentVec3d = new Vec3d(this.posX, this.posY, this.posZ);
			Vec3d futureVec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

			RayTraceResult hitPos = this.world.rayTraceBlocks(currentVec3d, futureVec3d, false, true, false);

			// This seems to require a reset, since getRayTrace messes with
			// them?
			currentVec3d = new Vec3d(this.posX, this.posY, this.posZ);
			futureVec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

			if (hitPos != null) // Hit something
			{
				futureVec3d = new Vec3d(hitPos.hitVec.xCoord, hitPos.hitVec.yCoord, hitPos.hitVec.zCoord);
			}

			Entity hitEntity = null;
			List<?> candidateList = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox()
					.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));

			double d0 = 0.0D;
			int iteratori;
			float gravity = 0.3F;

			for (iteratori = 0; iteratori < candidateList.size(); ++iteratori)
			{
				Entity potentialEntity = (Entity) candidateList.get(iteratori);

				if (potentialEntity.canBeCollidedWith()
						&& (potentialEntity != this.shootingEntity || this.ticksInAir >= 5))
				{
					AxisAlignedBB axisalignedbb1 = potentialEntity.getEntityBoundingBox().expand((double) gravity,
							(double) gravity, (double) gravity);
					RayTraceResult potentialMovObj = axisalignedbb1.calculateIntercept(currentVec3d, futureVec3d);

					if (potentialMovObj != null)
					{
						double d1 = currentVec3d.distanceTo(potentialMovObj.hitVec);

						if (d1 < d0 || d0 == 0.0D)
						{
							hitEntity = potentialEntity;
							d0 = d1;
						}
					}
				}
			}

			if (hitEntity != null)
			{
				hitPos = new RayTraceResult(hitEntity);
			} // Hit an entity, so grabbing its position

			if (hitPos != null && hitPos.entityHit != null && hitPos.entityHit instanceof EntityPlayer)
			{
				EntityPlayer entityplayer = (EntityPlayer) hitPos.entityHit;

				if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer
						&& !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer))
				{
					hitPos = null; // Either his entity can't be damaged in
					// general or we can't attack them
				}
			}

			if (hitPos != null)
			{
				this.onImpact(hitPos);
			}

			this.doFlightSFX();

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;

			float distance = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

			this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

			// The fuck is this?
			for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) distance) * 180.0D
					/ Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
			{
				;
			}

			while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
			{
				this.prevRotationPitch += 360.0F;
			}
			while (this.rotationYaw - this.prevRotationYaw < -180.0F)
			{
				this.prevRotationYaw -= 360.0F;
			}
			while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
			{
				this.prevRotationYaw += 360.0F;
			}

			this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
			this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

			float speedDecrease = 0.99F;
			gravity = 0.05F;
			float sfxMod = 0.25F;

			if (this.isInWater())
			{
				for (int l = 0; l < 4; ++l)
				{
					this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double) sfxMod,
							this.posY - this.motionY * (double) sfxMod, this.posZ - this.motionZ * (double) sfxMod,
							this.motionX, this.motionY, this.motionZ);
				}

				speedDecrease = 0.8F;
			}

			this.motionX *= (double) speedDecrease;
			this.motionY *= (double) speedDecrease;
			this.motionZ *= (double) speedDecrease;

			this.motionY -= (double) gravity;

			this.setPosition(this.posX, this.posY, this.posZ); // Position
			// update
		}
	}

	@Override
	public void onImpact(RayTraceResult hitPos) // Client-side only
	{
		if (hitPos.entityHit != null)
		{
			this.setDead();
		}
		else
		{
			IBlockState stuckState = this.world.getBlockState(hitPos.getBlockPos());
			this.stuckBlock = stuckState.getBlock();

			this.motionX = (double) ((float) (hitPos.hitVec.xCoord - this.posX));
			this.motionY = (double) ((float) (hitPos.hitVec.yCoord - this.posY));
			this.motionZ = (double) ((float) (hitPos.hitVec.zCoord - this.posZ));

			float distance = MathHelper
					.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

			this.posX -= this.motionX / (double) distance * 0.05000000074505806D;
			this.posY -= this.motionY / (double) distance * 0.05000000074505806D;
			this.posZ -= this.motionZ / (double) distance * 0.05000000074505806D;

			// SFX
			this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

			this.inGround = true;

			this.arrowShake = 7;

			if (stuckState.getMaterial() != Material.AIR)
			{
				this.stuckBlock.onEntityCollidedWithBlock(this.world, hitPos.getBlockPos(), stuckState, this);
			}
		}
	}

	@Override
	public void doFlightSFX()
	{
		Helper_Client.displayParticles(this.getEntityId(), EnumParticleTypes.SPELL_WITCH, (byte) 1); // Client-side
	}

	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];

		type[0] = 4; // Type 6, scoped predictive projectile
		type[1] = 1; // Length
		type[2] = 1; // Width

		return type;
	}

	@Override
	public String getEntityTexturePath()
	{
		return "textures/entity/ender.png";
	} // Our projectile texture. Don't have one, since we're using an icon
}
