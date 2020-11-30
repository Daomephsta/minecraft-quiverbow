package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EnderAno extends ProjectileBase
{
	public EnderAno(World world)
	{
		super(world);
	}

	public EnderAno(World world, Entity entity, WeaponProperties properties)
	{
		super(world);
		this.doSetup(entity, properties.getProjectileSpeed());
		this.damage = properties.generateDamage(rand);
        this.ticksInAirMax = properties.getInt(CommonProperties.DESPAWN_TIME);
	}

	@Override
	public boolean doDropOff()
	{
		return false;
	} // Affected by gravity?

	@Override
	public void doFlightSFX()
	{
		if (this.ticksExisted > this.ticksInAirMax)
		{
			this.setDead();
		} // There's only so long we can exist
	}

	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null) // We hit a living thing!
		{
			if (target.entityHit instanceof EntityEnderman && this.shootingEntity instanceof EntityPlayer)
			{
				target.entityHit.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) this.shootingEntity),
						this.damage); // Capable of hurting endermen
			}
			else
			{
				target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this),
						this.damage); // Anonymous
			}

			target.entityHit.hurtResistantTime = 0; // No immunity frames
			NetHelper.sendParticleMessageToAllPlayers(this.world, target.entityHit, EnumParticleTypes.PORTAL, (byte) 8);
		}
		else
		{
			// Glass breaking
			Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 1); // Medium strength
		}

		// SFX
		this.playSound(SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, 0.7F, 0.5F);

		this.setDead(); // Hit something, so we're done here
	}
}
