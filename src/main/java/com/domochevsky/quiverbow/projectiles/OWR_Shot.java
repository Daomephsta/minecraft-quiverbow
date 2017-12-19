package com.domochevsky.quiverbow.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class OWR_Shot extends ProjectilePotionEffect
{
	public int entitiesHit;
	public int damage_Magic;

	private int blocksHit;

	public OWR_Shot(World world)
	{
		super(world);
	}

	public OWR_Shot(World world, Entity entity, float speed, PotionEffect... effects)
	{
		super(world, effects);
		this.doSetup(entity, speed);
	}

	@Override
	public void doFlightSFX()
	{
		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.ENCHANTMENT_TABLE,
				(byte) 4);
	}

	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null) // We hit a living thing!
		{
			target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.shootingEntity),
					(float) this.damage_Magic);
			target.entityHit.hurtResistantTime = 0; // No immunity frames

			super.onImpact(target);

			this.setDead();
		}
		else // Hit the terrain
		{
			this.blocksHit += 1;

			// Glass breaking
			if (!Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 3))
			{
				this.setDead();
			} // Punching through anything without restriction

			if (this.blocksHit > 5)
			{
				this.setDead();
			} // Put an actual limit on that
		}

		// SFX
		this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
				(byte) 4);
	}

	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];

		type[0] = 2; // Type 2, generic projectile
		type[1] = 16; // Length
		type[2] = 2; // Width

		return type;
	}

	@Override
	public String getEntityTexturePath()
	{
		return "textures/entity/obsidian.png";
	} // Our projectile texture
}
