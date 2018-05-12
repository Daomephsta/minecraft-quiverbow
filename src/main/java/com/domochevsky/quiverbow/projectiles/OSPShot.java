package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class OSPShot extends ProjectilePotionEffect
{
	public int entitiesHit;

	public OSPShot(World world)
	{
		super(world);
	}

	public OSPShot(World world, Entity entity, float speed, PotionEffect... effects)
	{
		super(world, effects);
		this.doSetup(entity, speed);
	}

	@Override
	public void doFlightSFX()
	{
		// Doing our own (reduced) gravity

		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
				(byte) 1);
	}

	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null) // We hit a living thing!
		{
			super.onImpact(target);

			this.setDead(); // Hit a entity, so begone.
		}
		else // Hit the terrain
		{
			// Glass breaking, 1 layer
			if (Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 1) && this.targetsHit < 1)
			{
				this.targetsHit += 1;
			}
			else
			{
				this.setDead();
			} // Punching through glass
		}

		// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SPELL_MOB_AMBIENT,
				(byte) 2);
		this.playSound(SoundEvents.ENTITY_ARROW_HIT, 0.4F, 0.5F);
	}

	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];

		type[0] = 2; // Type 2, generic projectile
		type[1] = 4; // Length
		type[2] = 2; // Width

		return type;
	}

	@Override
	public String getEntityTexturePath()
	{
		return "textures/entity/obsidian.png";
	} // Our projectile texture
}
