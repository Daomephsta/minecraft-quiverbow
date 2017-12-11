package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ColdIron extends ProjectilePotionEffect
{
    public ColdIron(World world)
    {
	super(world);
    }

    public ColdIron(World world, Entity entity, float speed, PotionEffect... effects)
    {
	super(world, effects);
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

	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SNOW_SHOVEL,
		(byte) 1);
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SPELL, (byte) 2);
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
			    this.motionX * (double) this.knockbackStrength * 0.6000000238418579D / (double) velocity,
			    0.1D,
			    this.motionZ * (double) this.knockbackStrength * 0.6000000238418579D / (double) velocity);
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

    @Override
    public byte[] getRenderType()
    {
	byte[] type = new byte[3];

	type[0] = 2; // Type 2, generic projectile
	type[1] = 8; // Length
	type[2] = 2; // Width

	return type;
    }

    @Override
    public String getEntityTexturePath()
    {
	return "textures/entity/coldiron.png";
    } // Our projectile texture
}
