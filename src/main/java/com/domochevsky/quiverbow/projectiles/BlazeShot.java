package com.domochevsky.quiverbow.projectiles;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class BlazeShot extends _ProjectileBase
{
    public BlazeShot(World world)
    {
	super(world);
    }

    public BlazeShot(World world, Entity entity, float speed)
    {
	super(world);
	this.doSetup(entity, speed);
    }

    @Override
    public void onImpact(RayTraceResult hitPos) // Server-side
    {
	if (hitPos.entityHit != null)
	{
	    // Setting fire to the target here ...except for endermen? First
	    // fire, then damage
	    if (!(hitPos.entityHit instanceof EntityEnderman))
	    {
		hitPos.entityHit.setFire(this.fireDuration);
	    }

	    // Damage
	    hitPos.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), this.damage);
	    hitPos.entityHit.hurtResistantTime = 0; // No rest for the wicked

	    // Knockback
	    double f3 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

	    if (f3 > 0.0F)
	    {
		hitPos.entityHit.addVelocity(this.motionX * (double) this.knockbackStrength * 0.6D / (double) f3, 0.1D,
			this.motionZ * (double) this.knockbackStrength * 0.6D / (double) f3);
	    }

	    if (!(hitPos.entityHit instanceof EntityEnderman))
	    {
		this.setDead();
	    } // We've hit an entity (that's not an enderman), so begone with
	      // the projectile

	    this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.5F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F)); // Sizzling
														   // along...
	}
	else
	{
	    IBlockState state = this.world.getBlockState(hitPos.getBlockPos());

	    // Let's melt ice on contact
	    if (state.getBlock() == Blocks.ICE)
	    {
		this.world.setBlockState(hitPos.getBlockPos(), Blocks.FLOWING_WATER.getDefaultState(), 3);
		this.targetsHit += 1;
	    }

	    // Glass breaking, through 4 layers
	    if (Helper.tryBlockBreak(this.world, this, hitPos, 2) && this.targetsHit < 4)
	    {
		this.targetsHit += 1;
	    } // Going straight through most things
	    else // Either didn't manage to break that block or we already hit 4
		 // things
	    {

		this.stuckBlockX = hitPos.getBlockPos().getX();
		this.stuckBlockY = hitPos.getBlockPos().getY();
		this.stuckBlockZ = hitPos.getBlockPos().getZ();

		BlockPos stuckPos = new BlockPos(this.stuckBlockX, this.stuckBlockY, this.stuckBlockZ);
		IBlockState stuckState = this.world.getBlockState(stuckPos);
		this.stuckBlock = stuckState.getBlock();

		this.motionX = (double) ((float) (hitPos.hitVec.xCoord - this.posX));
		this.motionY = (double) ((float) (hitPos.hitVec.yCoord - this.posY));
		this.motionZ = (double) ((float) (hitPos.hitVec.zCoord - this.posZ));

		float distance = MathHelper
			.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

		this.posX -= this.motionX / (double) distance * 0.05000000074505806D;
		this.posY -= this.motionY / (double) distance * 0.05000000074505806D;
		this.posZ -= this.motionZ / (double) distance * 0.05000000074505806D;

		this.inGround = true;

		this.arrowShake = 7;

		if (stuckState.getMaterial() != Material.AIR)
		{
		    this.stuckBlock.onEntityCollidedWithBlock(this.world, stuckPos, stuckState, this);
		}
	    }
	}
    }

    @Override
    public void doFlightSFX()
    {
	// System.out.println("Caller is " + this + "/ worldObj is " +
	// this.world + " / entity ID is " + this.getEntityId());

	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
		(byte) 3);
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.FLAME, (byte) 1);
    }

    @Override
    public void doInGroundSFX() // Server side
    {
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
		(byte) 1);
	this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.1F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F)); // Sizzling
													       // along...

	this.targetsHit += 1; // Dissipating in strength each tick?
    }

    @Override
    public void doWaterEffect() // Called when this entity moves through water
    {
	// Checking for water here and turning it into ice
	BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);
	IBlockState state = this.world.getBlockState(pos);

	if (state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER)
	{
	    // Hit a (flowing) water block, so turning that into ice now
	    this.world.setBlockToAir(pos);

	    // SFX
	    NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
		    (byte) 4);
	    this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.1F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
	}
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) // Burning while stuck
							 // in the ground
    {
	if (this.world.isRemote)
	{
	    return;
	} // Not doing this on client side
	if (!this.inGround)
	{
	    return;
	} // Not stuck in the ground
	if (this.arrowShake > 0)
	{
	    return;
	} // Not... done shaking?

	// Ready to hurt someone!
	player.setFire(this.fireDuration / 2); // Half burn time. Let's be
					       // lenient here
    }

    @Override
    public byte[] getRenderType() // Called by the renderer. Expects a 3 item
				  // byte array
    {
	byte[] type = new byte[3];

	type[0] = 2; // Generic projectile
	type[1] = 6; // Length and width
	type[2] = 2;

	return type; // Fallback, 0 0 0
    }

    @Override
    public String getEntityTexturePath()
    {
	return "textures/entity/rod.png";
    }
}
