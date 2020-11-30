package com.domochevsky.quiverbow.projectiles;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class OWRShot extends ProjectilePotionEffect
{
    public static final Pair<String, String>
        MIN_MAGIC_DAMAGE = Pair.of("minDamageMagic", "The minimum magic damage this weapon does"),
        MAX_MAGIC_DAMAGE = Pair.of("maxDamageMagic", "The maximum magic damage this weapon does");
	private int damageMagic;

	public OWRShot(World world)
	{
		super(world);
	}

	public OWRShot(World world, Entity entity, WeaponProperties properties)
	{
		super(world, new PotionEffect(MobEffects.WITHER,
		    properties.getInt(CommonProperties.WITHER_DUR),
		    properties.getInt(CommonProperties.WITHER_STRENGTH)));
		this.doSetup(entity, properties.getProjectileSpeed());
		this.damage = properties.generateDamage(world.rand);
		this.damageMagic = Helper.randomIntInRange(world.rand,
		    properties.getInt(MIN_MAGIC_DAMAGE), properties.getInt(MAX_MAGIC_DAMAGE));
	}

	@Override
	public void doFlightSFX()
	{
		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.ENCHANTMENT_TABLE,
				(byte) 4);
	}

	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null) // We hit a living thing!
		{
			target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.shootingEntity),
					this.damageMagic);
			target.entityHit.hurtResistantTime = 0; // No immunity frames

			super.onImpact(target);

			this.setDead();
		}
		else // Hit the terrain
		{
			this.targetsHit += 1;

			// Glass breaking
			if (!Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 3))
			{
				this.setDead();
			} // Punching through anything without restriction

			if (this.targetsHit > 5)
			{
				this.setDead();
			} // Put an actual limit on that
		}

		// SFX
		this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SMOKE_NORMAL,
				(byte) 4);
	}
}