package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.net.NetHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EnderAccelerator extends ProjectileBase implements IEntityAdditionalSpawnData
{
	public boolean damageTerrain;
	public float explosionSize;

	public EnderAccelerator(World world)
	{
		super(world);
	}

	public EnderAccelerator(World world, Entity entity, float speed)
	{
		super(world);
		this.doSetup(entity, speed);

		this.ownerX = entity.posX;
		this.ownerY = entity.posY + entity.getEyeHeight();
		this.ownerZ = entity.posZ;
	}

	@Override
	public boolean doDropOff()
	{
		return false;
	} // Affected by gravity? Nope. Straight beam

	@Override
	public void doFlightSFX()
	{
		if (this.ticksExisted > this.ticksInAirMax) // There's only so long we
		// can exist
		{
			this.world.createExplosion(this, this.posX, this.posY, this.posZ, 8.0f, damageTerrain); // Ripping
			// itself
			// apart
			this.setDead();
		}

		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SPELL, (byte) 4);
		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.ENCHANTMENT_TABLE,
				(byte) 4);
	}

	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null) // We hit a living thing!
		{
			// Damage
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity),
					this.damage);
			target.entityHit.hurtResistantTime = 0; // No immunity frames
		}

		this.world.createExplosion(this, this.posX, this.posY, this.posZ, this.explosionSize, damageTerrain); // Big
		// baddaboom

		// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SPELL, (byte) 8);

		this.setDead(); // No matter what, we're done here
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) // save extra data on the server
	{
		buffer.writeDouble(this.ownerX);
		buffer.writeDouble(this.ownerY);
		buffer.writeDouble(this.ownerZ);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) // read it on the client
	{
		this.ownerX = additionalData.readDouble();
		this.ownerY = additionalData.readDouble();
		this.ownerZ = additionalData.readDouble();
	}
}
