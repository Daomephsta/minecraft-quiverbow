package com.domochevsky.quiverbow.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class EnderAno extends _ProjectileBase
{
    public int travelTicksMax;
    public int targetsHitMax;

    public EnderAno(World world)
    {
	super(world);
    }

    public EnderAno(World world, Entity entity, float speed)
    {
	super(world);
	this.doSetup(entity, speed);
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
			(float) this.damage); // Capable of hurting endermen
	    }
	    else
	    {
		target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this),
			(float) this.damage); // Anonymous
	    }

	    target.entityHit.hurtResistantTime = 0; // No immunity frames
	}
	else
	{
	    // Glass breaking
	    Helper.tryBlockBreak(this.world, this, target, 1); // Medium
							       // strength

	}

	// SFX
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.PORTAL, (byte) 8);
	this.playSound(SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, 0.7F, 0.5F);

	this.setDead(); // Hit something, so we're done here
    }

    @Override
    public byte[] getRenderType()
    {
	byte[] type = new byte[3];

	type[0] = 0; // Type 0, nothing
	type[1] = 2; // Length
	type[2] = 2; // Width

	return type;
    }
}
