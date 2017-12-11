package com.domochevsky.quiverbow.projectiles;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class ProxyThorn extends _ProjectileBase
{
    public int proxyDelay = 20; // Only checking every so often
    private double thornSpeed = 1.5;
    public double triggerDistance = 2.0;
    public int ThornAmount = 32;

    private EnumFacing hitSide;

    public ProxyThorn(World world)
    {
	super(world);
    }

    public ProxyThorn(World world, Entity entity, float speed)
    {
	super(world);
	this.doSetup(entity, speed);
	this.thornSpeed = speed;
    }

    @Override
    public void onImpact(RayTraceResult movPos) // Server-side
    {
	if (movPos.entityHit != null) // We hit a living thing!
	{
	    movPos.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity),
		    (float) this.damage); // Damage gets applied here
	    movPos.entityHit.hurtResistantTime = 0; // No immunity frames

	    this.goBoom();
	    this.setDead(); // We've hit something, so begone with the
	    // projectile
	}

	else // Hit the terrain
	{
	    if (Helper.tryBlockBreak(this.world, this, movPos.getBlockPos(), 1)) // Going
		// straight
		// through a
		// thing
	    {
		// this.goBoom();
	    }
	    else // Didn't manage to break that block, so we're stuck now for a
		// short while
	    {
		IBlockState stuckState = this.world.getBlockState(movPos.getBlockPos());
		this.stuckBlock = stuckState.getBlock();
		//Only make an impact sound if the block this is stuck in has changed
		if(stuckBlockX != movPos.getBlockPos().getX() || stuckBlockY != movPos.getBlockPos().getY() || stuckBlockZ != movPos.getBlockPos().getZ())
		{
		    this.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0F, 0.3F);
		    NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
			    (byte) 4);
		}
		stuckBlockX = movPos.getBlockPos().getX();
		stuckBlockY = movPos.getBlockPos().getY();
		stuckBlockZ = movPos.getBlockPos().getZ();

		this.motionX = movPos.hitVec.xCoord - this.posX;
		this.motionY = movPos.hitVec.yCoord - this.posY;
		this.motionZ = movPos.hitVec.zCoord - this.posZ;

		this.hitSide = movPos.sideHit; // Keeping track of the side we
		// hit, for when we go boom

		float distance = MathHelper
			.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

		this.posX -= this.motionX / (double) distance * 0.05000000074505806D;
		this.posY -= this.motionY / (double) distance * 0.05000000074505806D;
		this.posZ -= this.motionZ / (double) distance * 0.05000000074505806D;

		this.inGround = true;

		this.arrowShake = 7;

		if (stuckState.getMaterial() != Material.AIR)
		{
		    this.stuckBlock.onEntityCollidedWithBlock(this.world, movPos.getBlockPos(), stuckState, this);
		}
	    }

	    this.setEntityBoundingBox(new AxisAlignedBB(-0.2d, 0.0d, -0.2d, 0.2d, 0.2d, 0.2d)); // Attackable
	}

	// SFX

	// this.setDead(); // We've hit something, so begone with the projectile
    }

    @Override
    public void doFlightSFX()
    {
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.CRIT, (byte) 2);
    }

    @Override
    public void doInGroundSFX() // Server side // Checking proximity for living
    // entities, to see if I need to explode
    {
	if (this.ticksInGround == this.ticksInGroundMax - 1)
	{
	    this.goBoom(); // Out of time
	}

	if (this.proxyDelay > 0)
	{
	    this.proxyDelay -= 1;
	    return; // Not yet
	}

	this.proxyDelay = 20; // Reset

	// Go time
	AxisAlignedBB box = this.getEntityBoundingBox().expand(this.triggerDistance, this.triggerDistance,
		this.triggerDistance);
	List<?> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, box);

	Entity potentialEntity;
	int counter = 0;
	boolean skip = false;

	while (counter < list.size())
	{
	    skip = false;

	    potentialEntity = (Entity) list.get(counter);

	    if (potentialEntity instanceof EntityPlayer) // Not triggering for
		// creative mode
		// players
	    {
		EntityPlayer player = (EntityPlayer) potentialEntity;
		if (player.capabilities.isCreativeMode)
		{
		    skip = true;
		}
	    }

	    if (!skip && Helper.canEntityBeSeen(this.world, this, potentialEntity)) // Only
		// if
		// we
		// have
		// a
		// line
		// of
		// sight
		// to
		// them
	    {
		this.goBoom(); // We can see them! Boom time!
		return;
	    }

	    // Next!
	    counter += 1;
	}
    }

    // Spraying a bunch of thorns in random directions
    private void goBoom()
    {
	if(this.hitSide != null)
	{
	    //Moving out of the block we're stuck in, to get a clear shot
	    switch (this.hitSide)
	    {
	    case DOWN:
		this.posY -= 0.5;
		break;
	    case UP:
		this.posY += 0.5;
		break;
	    case EAST:
		this.posX += 0.5;
		break;
	    case WEST:
		this.posX -= 0.5;
		break;
	    case NORTH:
		this.posZ += 0.5;
		break;
	    case SOUTH:
		this.posZ -= 0.5;
		break;
	    }
	}

	int amount = this.ThornAmount;

	while (amount > 0)
	{
	    this.fireThorn();
	    amount -= 1;
	}

	// SFX
	this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.3F, 2.0F);
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SMOKE_LARGE,
		(byte) 8);

	this.setDead(); // We're done here
    }

    // Blasts a thorn in a random direction
    private void fireThorn()
    {
	// Random dir
	int thornYaw = this.world.rand.nextInt(360) + 1; // Range will be
	// between 1 and 360
	thornYaw -= 180; // Range between -180 and 180

	int thornPitch = this.world.rand.nextInt(360) + 1; // Range will be
	// between 1 and 360
	thornPitch -= 180; // Range between -180 and 180

	int dmg = this.world.rand.nextInt(2) + 1; // Range will be between 1 and
	// 2

	// Firing
	Thorn projectile = new Thorn(this.world, this, (float) this.thornSpeed, (float) thornYaw, (float) thornPitch);
	projectile.damage = dmg;

	projectile.shootingEntity = this.shootingEntity; // Keeping that chain
	// alive

	this.world.spawnEntity(projectile);
    }

    @Override
    public boolean hitByEntity(Entity entity)
    {
	this.goBoom();
	return false;
    }

    @Override
    public byte[] getRenderType()
    {
	byte[] type = new byte[3];

	type[0] = 2; // Type 2, generic projectile
	type[1] = 8; // Length
	type[2] = 4; // Width

	return type; // Fallback, 0 0 0
    }

    @Override
    public String getEntityTexturePath()
    {
	return "textures/entity/thorn.png";
    } // Our projectile texture
}
