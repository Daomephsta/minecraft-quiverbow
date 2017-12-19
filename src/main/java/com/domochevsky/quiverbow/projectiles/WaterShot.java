package com.domochevsky.quiverbow.projectiles;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.net.NetHelper;

public class WaterShot extends _ProjectileBase
{
    public WaterShot(World world)
    {
	super(world);
    }

    public WaterShot(World world, Entity entity, float speed)
    {
	super(world);
	this.doSetup(entity, speed);
    }

    @Override
    public void doFlightSFX()
    {
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.WATER_BUBBLE,
		(byte) 4);
    }

    @Override
    public void onImpact(RayTraceResult target)
    {
	BlockPos pos;
	if (target.entityHit != null) // hit a entity
	    pos = new BlockPos(target.entityHit.posX, target.entityHit.posY, target.entityHit.posZ);
	else // hit the terrain
	    pos = target.getBlockPos().offset(target.sideHit);

	// Nether Check
	if (this.world.provider.doesWaterVaporize())
	{
	    this.world.playSound((double) ((float) this.posX + 0.5F), (double) ((float) this.posY + 0.5F),
		    (double) ((float) this.posZ + 0.5F), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.AMBIENT, 0.5F,
		    2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F, false);

	    NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SMOKE_LARGE,
		    (byte) 4);

	    return; // No water in the nether, yo
	}

	// Is the space free?
	IBlockState hitState = this.world.getBlockState(pos);
	if (hitState.getBlock().isReplaceable(world, pos) || hitState.getMaterial() == Material.PLANTS)
	{
	    // Can we edit this block at all?
	    if (this.shootingEntity instanceof EntityPlayer)
	    {
		EntityPlayer player = (EntityPlayer) this.shootingEntity;
		if (!player.canPlayerEdit(pos, target.sideHit, ItemStack.EMPTY))
		{
		    return;
		} // Nope
	    }

	    // Putting water there!
	    this.world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState(), 3);
	}

	// SFX
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.WATER_BUBBLE,
		(byte) 4);
	this.playSound(SoundEvents.ENTITY_GENERIC_SPLASH, 1.0F, 1.0F);

	this.setDead(); // We've hit something, so begone with the projectile
    }

    @Override
    public byte[] getRenderType()
    {
	byte[] type = new byte[3];

	type[0] = 3; // Type 3, icon
	type[1] = 6; // Length, water bucket
	type[2] = 2; // Width

	return type; // Fallback, 0 0 0
    }
}
