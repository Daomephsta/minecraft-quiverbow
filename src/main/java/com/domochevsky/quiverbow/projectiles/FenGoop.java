package com.domochevsky.quiverbow.projectiles;

import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.blocks.FenLight;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class FenGoop extends _ProjectileBase
{
    public FenGoop(World world)
    {
	super(world);
    }

    public FenGoop(World world, Entity entity, float speed)
    {
	super(world);
	this.doSetup(entity, speed);
    }

    public int lightTick;

    @Override
    public void onImpact(RayTraceResult target)
    {
	if (target.entityHit != null) // hit a entity
	{
	    target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) 0); // No
														     // dmg,
														     // but
														     // knockback
	    target.entityHit.hurtResistantTime = 0;
	    target.entityHit.setFire(fireDuration); // Some minor fire, for
						    // flavor
	}
	else // hit the terrain
	{
	    BlockPos pos = target.getBlockPos().offset(target.sideHit.getOpposite());

	    // Block targetBlock = this.world.getBlock(posiX, posiY, posiZ);

	    // Is the attached block a valid material?
	    boolean canPlace = false;
	    if (world.isSideSolid(pos, target.sideHit, false))
	    {
		canPlace = true;
	    }

	    // Glass breaking
	    if (Helper.tryBlockBreak(this.world, this, target, 0))
	    {
		canPlace = false;
	    }

	    // Is the space free?
	    if (this.world.getBlockState(pos).getBlock().isReplaceable(world, pos))
	    {
		// Putting light there (if we can)
		if (canPlace)
		{
		    FenLight.placeFenLight(world, pos, target.sideHit);

		    if (this.lightTick != 0)
		    {
			this.world.scheduleBlockUpdate(pos, Main.fenLight, this.lightTick, 1);
		    }
		    // else, stays on indefinitely
		}
		// else, can't place. The block isn't of a valid material
	    }
	    // else, none of the allowed materials
	}

	// SFX
	for (int i = 0; i < 8; ++i)
	{
	    this.world.spawnParticle(EnumParticleTypes.SLIME, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
	}
	this.playSound(SoundType.GLASS.getBreakSound(), 1.0F, 1.0F);

	this.setDead(); // We've hit something, so begone with the projectile
    }

    @Override
    public byte[] getRenderType()
    {
	byte[] type = new byte[3];

	type[0] = 3; // Type 3, item
	type[1] = 5; // Length, misused as item type. glowstone dust
	type[2] = 2; // Width

	return type;
    }
}
