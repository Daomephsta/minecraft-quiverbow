package com.domochevsky.quiverbow.projectiles;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class LapisShot extends ProjectilePotionEffect
{
	public static final Pair<String, String> WEAKNESS_STRENGTH =
    Pair.of("weaknessStrength", "The strength of the Weakness effect applied");
    public static final Pair<String, String> WEAKNESS_DUR =
    Pair.of("weaknessDur", "The duration in ticks of the Weakness effect applied");
    public static final Pair<String, String> HUNGER_STRENGTH =
    Pair.of("hungerStrength", "The strength of the Hunger effect applied");
    public static final Pair<String, String> HUNGER_DUR =
    Pair.of("hungerDur", "The duration in ticks of the Hunger effect applied");

    public LapisShot(World world)
	{
		super(world);
	}

	public LapisShot(World world, Entity entity, WeaponProperties properties)
	{
		super(world, new PotionEffect(MobEffects.NAUSEA, properties.getInt(CommonProperties.NAUSEA_DUR), 1),
            new PotionEffect(MobEffects.HUNGER,
                properties.getInt(HUNGER_DUR), properties.getInt(HUNGER_STRENGTH)),
            new PotionEffect(MobEffects.WEAKNESS,
                properties.getInt(WEAKNESS_DUR), properties.getInt(WEAKNESS_STRENGTH)));
		this.doSetup(entity, properties.getProjectileSpeed());
		this.damage = properties.generateDamage(rand);
        this.ticksInGroundMax = properties.getInt(CommonProperties.DESPAWN_TIME);
	}

	@Override
	public void onImpact(RayTraceResult movPos) // Server-side
	{
		if (movPos.entityHit != null) // We hit a living thing!
		{
			super.onImpact(movPos);

			this.setDead(); // We've hit something, so begone with the
			// projectile
		}

		else // Hit the terrain
		{
			// Helper.tryBlockBreak(this.world, this, movPos, 1);

			if (Helper.tryBlockBreak(this.world, this, movPos.getBlockPos(), 1))
			{
				this.setDead();
			} // Going straight through a thing
			else // Didn't manage to break that block, so we're stuck now for a
			// short while
			{

				this.stuckBlockX = movPos.getBlockPos().getX();
				this.stuckBlockY = movPos.getBlockPos().getY();
				this.stuckBlockZ = movPos.getBlockPos().getZ();

				IBlockState stuckState = world.getBlockState(movPos.getBlockPos());
				this.stuckBlock = stuckState.getBlock();

				this.motionX = ((float) (movPos.hitVec.x - this.posX));
				this.motionY = ((float) (movPos.hitVec.y - this.posY));
				this.motionZ = ((float) (movPos.hitVec.z - this.posZ));

				float distance = MathHelper
						.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

				this.posX -= this.motionX / distance * 0.05000000074505806D;
				this.posY -= this.motionY / distance * 0.05000000074505806D;
				this.posZ -= this.motionZ / distance * 0.05000000074505806D;

				this.inGround = true;

				this.arrowShake = 7;

				if (stuckState.getMaterial() != Material.AIR)
				{
					this.stuckBlock.onEntityCollidedWithBlock(this.world, movPos.getBlockPos(), stuckState, this);
				}
			}
		}

		// SFX
		this.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SMOKE_NORMAL,
				(byte) 4);
	}

	@Override
	public void doFlightSFX()
	{
		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SPELL, (byte) 2);
	}
}
