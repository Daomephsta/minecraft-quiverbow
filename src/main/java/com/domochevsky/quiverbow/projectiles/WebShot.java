package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class WebShot extends _ProjectileBase
{
    public WebShot(World world)
    {
	super(world);
    }

    public WebShot(World world, Entity entity, float speed)
    {
	super(world);
	this.doSetup(entity, speed);
    }

    @Override
    public void onImpact(RayTraceResult target)
    {
	BlockPos pos;
	if (target.entityHit != null) // hit a entity
	    pos = new BlockPos(target.entityHit.posX, target.entityHit.posY, target.entityHit.posZ);
	else pos = target.getBlockPos().offset(target.sideHit);

	// Is the space free?
	IBlockState hitState = this.world.getBlockState(pos);
	if (hitState.getBlock().isReplaceable(world, pos) || hitState.getMaterial() == Material.PLANTS)
	{
	    // Putting a web there!
	    this.world.setBlockState(pos, Blocks.WEB.getDefaultState(), 3);
	}

	// SFX TODO: Find out which particle this should use
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SPELL_MOB,
		(byte) 4);
	this.playSound(SoundEvents.ENTITY_GENERIC_SPLASH, 0.4F, 2.0F);

	this.setDead(); // We've hit something, so begone with the projectile
    }

    @Override
    public byte[] getRenderType()
    {
	byte[] type = new byte[3];

	type[0] = 3; // Type 3, icon
	type[1] = 7; // Length, snowball (misused as web ball)
	type[2] = 2; // Width

	return type; // Fallback, 0 0 0
    }
}
