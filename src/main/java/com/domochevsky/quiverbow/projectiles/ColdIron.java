package com.domochevsky.quiverbow.projectiles;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ColdIron extends ProjectilePotionEffect
{
    public static final Pair<String, String> NAUSEA_STRENGTH =
        Pair.of("nauseaStrength", "The strength of the Nausea effect applied");

	public ColdIron(World world)
	{
		super(world);
	}

	public ColdIron(World world, Entity entity, WeaponProperties properties)
	{
		super(world, new PotionEffect(MobEffects.SLOWNESS, properties.getInt(CommonProperties.SLOWNESS_DUR),
		    properties.getInt(CommonProperties.SLOWNESS_STRENGTH)),
	        new PotionEffect(MobEffects.NAUSEA, properties.getInt(CommonProperties.NAUSEA_DUR),
	            properties.getInt(NAUSEA_STRENGTH)));
		this.doSetup(entity, properties.getProjectileSpeed());
		this.damage = properties.generateDamage(world.rand);
		this.knockbackStrength = properties.getKnockback();
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

		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SNOW_SHOVEL, (byte) 1);
		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SPELL, (byte) 2);
	}

	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null) // We hit a living thing!
		{
			super.onImpact(target);

			// Knockback
			if (this.knockbackStrength > 0)
			{
				float velocity = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
				if (velocity > 0.0F)
				{
					target.entityHit.addVelocity(
							this.motionX * this.knockbackStrength * 0.6000000238418579D / velocity,
							0.1D,
							this.motionZ * this.knockbackStrength * 0.6000000238418579D / velocity);
				}
				// else, no velocity so no knockback
			}
			// else, no knockback

			this.setDead(); // Hit something, so begone.
		}
		else // Hit the terrain
		{
			// Glass breaking, 3 layers
			if (Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 1) && this.targetsHit < 3)
			{
				this.targetsHit += 1;
			}
			else
			{
				this.setDead();
			} // Going straight through glass, up to twice
		}
	}
}
