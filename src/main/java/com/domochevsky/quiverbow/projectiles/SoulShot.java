package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SoulShot extends ProjectileBase
{
	public SoulShot(World world)
	{
		super(world);
	}

	public SoulShot(World world, Entity entity, float speed)
	{
		super(world);
		this.doSetup(entity, speed);
	}

	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null)
		{
			// Can't catch players or bosses
			if (target.entityHit instanceof EntityPlayer)
			{
				target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.shootingEntity),
						(float) 10);
				this.damageShooter();
				return;
			}
			else if (!target.entityHit.isNonBoss())
			{
				this.damageShooter();
				return;
			}
			// TODO: Add blacklist. Should be modifiable via config and IMC
			doCapture(target);

			NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SMOKE_LARGE,
					(byte) 4);

			this.setDead(); // We've hit something, so begone with the
			// projectile
		}
		else // Hit the terrain
		{
			// Glass breaking
			if (!Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 1))
			{
				this.setDead();
			} // Only begone if we didn't hit glass
		}
	}

	void doCapture(RayTraceResult target)
	{
		// Make it dead and gimme the egg
		target.entityHit.setDead();

		ResourceLocation entityID = ForgeRegistries.ENTITIES
				.getKey(EntityRegistry.getEntry(target.entityHit.getClass()));
		ItemStack egg = new ItemStack(Items.SPAWN_EGG);
		ItemMonsterPlacer.applyEntityIdToItemStack(egg, entityID);

		if (this.shootingEntity == null) // Owner doesn't exist, so this has
		// likely been used by a mob. Dropping
		// the egg at target location
		{
			// System.out.println("[DEBUGCHEVSKY] Owner of SOUL SHOT is null.
			// Now'd that happen?");
			EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY + 1d, this.posZ, egg);
			entityitem.setDefaultPickupDelay();

			this.world.spawnEntity(entityitem);
		}
		else
		{
			EntityItem entityitem = new EntityItem(this.world, this.shootingEntity.posX, this.shootingEntity.posY + 1d,
					this.shootingEntity.posZ, egg);
			entityitem.setDefaultPickupDelay();

			this.world.spawnEntity(entityitem);
		}
	}

	@Override
	public boolean doDropOff()
	{
		return false;
	} // If this returns false then we won't care about gravity

	@Override
	public void doFlightSFX()
	{
		// Doing our own (reduced) gravity
		this.motionY -= 0.025; // Default is 0.05

		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.PORTAL, (byte) 3);
	}

	void damageShooter()
	{
		if (this.shootingEntity == null)
		{
			return;
		} // Owner doesn't exist

		this.shootingEntity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.shootingEntity),
				(float) 10);
	}

	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];

		type[0] = 2; // Type 2, generic projectile
		type[1] = 10; // Length
		type[2] = 2; // Width

		return type;
	}

	@Override
	public String getEntityTexturePath()
	{
		return "textures/entity/soulshot.png";
	} // Our projectile texture
}
