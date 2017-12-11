package com.domochevsky.quiverbow.projectiles;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class SunLight extends _ProjectileBase implements IEntityAdditionalSpawnData
{
    public int travelTicksMax;
    public int targetsHitMax;

    public SunLight(World world)
    {
	super(world);
    }

    public SunLight(World world, Entity entity, float speed)
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
    } // Affected by gravity?

    @Override
    public void doFlightSFX()
    {
	if (this.ticksExisted > this.ticksInAirMax)
	{
	    this.setDead();
	} // There's only so long we can exist

	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.FLAME, (byte) 1);
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SPELL, (byte) 1);
    }

    @Override
    public void onImpact(RayTraceResult target)
    {
	if (target.entityHit != null) // We hit a living thing!
	{
	    // Damage
	    target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity),
		    (float) this.damage);
	    target.entityHit.hurtResistantTime = 0; // No immunity frames

	    // Fire
	    target.entityHit.setFire(fireDuration);
	}
	else
	{
	    IBlockState state = this.world.getBlockState(target.getBlockPos());

	    // Glass breaking
	    Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 2); // Strong

	    // Let's create fire here
	    if (state.getBlock() != Blocks.FIRE)
	    {
		BlockPos upPos = target.getBlockPos().up();
		IBlockState upState = world.getBlockState(upPos);
		if (upState.getBlock().isAir(upState, world, upPos))
		{
		    // the block above the block we hit is air, so let's set it
		    // on fire!
		    this.world.setBlockState(upPos, Blocks.FIRE.getDefaultState(), 3);
		}
		// else, not an airblock above this
	    }

	    // Have we hit snow? Turning that into snow layer
	    else if (state.getBlock() == Blocks.SNOW)
	    {
		this.world.setBlockState(target.getBlockPos(),
			Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, 7), 3);
	    }

	    // Have we hit snow layer? Melting that down into nothing
	    else if (state.getBlock() == Blocks.SNOW_LAYER)
	    {
		int currentLayers = state.getValue(BlockSnow.LAYERS);
		// Is this taller than 0? Melting it down then
		if (currentLayers > 0)
		{
		    this.world.setBlockState(target.getBlockPos(),
			    Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, currentLayers - 1), 3);
		}
		// Is this 0 already? Turning it into air
		else
		{
		    this.world.setBlockToAir(target.getBlockPos());
		}
	    }

	    // Have we hit ice? Turning that into water
	    else if (state.getBlock() == Blocks.ICE)
	    {
		this.world.setBlockState(target.getBlockPos(), Blocks.WATER.getDefaultState(), 3);
	    }

	    // Have we hit (flowing) water? Evaporating that
	    else if (state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER)
	    {
		this.world.setBlockToAir(target.getBlockPos());
	    }
	}

	// SFX
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.FLAME, (byte) 2);
	this.playSound(SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, 0.7F, 1.5F);

	// Going through terrain until our time runs out, but don't damage
	// anything
    }

    @Override
    public byte[] getRenderType()
    {
	byte[] type = new byte[3];

	type[0] = 7; // Type 7, beam weapon (Sunray)
	type[1] = 2; // Length
	type[2] = 2; // Width

	return type;
    }

    @Override
    public String getEntityTexturePath()
    {
	return "textures/entity/rod.png";
    } // Our projectile texture

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
