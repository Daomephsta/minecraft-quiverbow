package com.domochevsky.quiverbow.projectiles;

import java.util.HashSet;
import java.util.Set;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SoulShot extends ProjectileBase
{
	private static final Set<ResourceLocation> BLACKLIST = new HashSet<>();

	public SoulShot(World world)
	{
		super(world);
	}

	public SoulShot(World world, Entity entity, WeaponProperties properties)
	{
		super(world);
		this.doSetup(entity, properties.getProjectileSpeed());
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
						10);
				this.damageShooter();
				return;
			}
			else if (!target.entityHit.isNonBoss())
			{
				this.damageShooter();
				if (shootingEntity instanceof EntityPlayer)
					((EntityPlayer) shootingEntity).sendStatusMessage(
							new TextComponentTranslation(QuiverbowMain.MODID + ".soul_cairn.boss"), true);
				return;
			}
			//Check the blacklist
			if(BLACKLIST.contains(EntityList.getKey(target.entityHit.getClass())))
			{
				if (shootingEntity instanceof EntityPlayer)
					((EntityPlayer) shootingEntity).sendStatusMessage(
							new TextComponentTranslation(QuiverbowMain.MODID + ".soul_cairn.blacklisted"), true);
				return;
			}
			doCapture(target);

			NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SMOKE_LARGE,
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

		if (this.shootingEntity == null) // Owner doesn't exist, so this has likely been used by a mob. Dropping the egg at target location
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

	public static void blacklistEntity(ResourceLocation entityID)
	{
		if (EntityList.isRegistered(entityID)) BLACKLIST.add(entityID);
		else QuiverbowMain.logger.warn("No entity is registered with the id {}", entityID);
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

		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.PORTAL, (byte) 3);
	}

	void damageShooter()
	{
		if (this.shootingEntity == null)
		{
			return;
		} // Owner doesn't exist

		this.shootingEntity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.shootingEntity),
				10);
	}
}
