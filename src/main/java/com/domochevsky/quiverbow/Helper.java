package com.domochevsky.quiverbow;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.domochevsky.quiverbow.config.QuiverbowConfig;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.ProjectileBase;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class Helper
{
	private static final ItemStack ARROW_STACK = new ItemStack(Items.ARROW);

	/** Kicks the passed in entity backwards, relative to the passed in strength
	  * Needs to be done both on client and server, because the server doesn't
	  * inform clients about small movement changes
	  * This is the server-side part **/
	public static void knockUserBack(Entity user, int strength)
	{
		user.motionZ += -MathHelper.cos((user.rotationYaw) * (float) Math.PI / 180.0F) * (strength * 0.08F);
		user.motionX += MathHelper.sin((user.rotationYaw) * (float) Math.PI / 180.0F) * (strength * 0.08F);

		NetHelper.sendKickbackMessage(user, strength);
	}

	/** Sets the projectile to be pickupable depending on player creative mode
	*   Used for throwable entities **/
	public static void setThrownPickup(EntityLivingBase entity, ProjectileBase shot)
	{
		if (entity instanceof EntityPlayer) // Is a player
		{
			// Creative mode?
			EntityPlayer player = (EntityPlayer) entity;

			if (player.capabilities.isCreativeMode)
			{
				shot.canBePickedUp = false;
			} // In creative mode, no drop
			else
			{
				shot.canBePickedUp = true;
			} // Not in creative, so dropping is permitted

		}
		else
		{
			shot.canBePickedUp = false;
		} // Not a player, so not dropping anything
	}

	// Unified appliance of potion effects
	public static void applyPotionEffect(EntityLivingBase entitylivingbase, PotionEffect effect)
	{
		if (entitylivingbase == null)
		{
			return;
		} // Not a valid entity, for some reason

		if (effect == null)
		{
			return;
		} // Nothing to apply

		PotionEffect potion = entitylivingbase.getActivePotionEffect(effect.getPotion());
		if (potion != null) // Already exists. Extending it
		{
			int dur = potion.getDuration();

			entitylivingbase.addPotionEffect(
					new PotionEffect(effect.getPotion(), effect.getDuration() + dur, effect.getAmplifier() - 1));
		}
		else
		{
			entitylivingbase.addPotionEffect(
					new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier() - 1));
		} // Fresh
	}

	// Time to make a mess!
	/** Checking if the block hit can be broken
	*   stronger weapons can break more block types **/
	public static boolean tryBlockBreak(World world, Entity entity, BlockPos pos, int strength)
	{
		if (!QuiverbowConfig.breakGlass)
		{
			return false;
		} // Not allowed to break anything in general

		if (entity instanceof ProjectileBase)
		{
			ProjectileBase projectile = (ProjectileBase) entity;

			if (projectile.shootingEntity != null && !(projectile.shootingEntity instanceof EntityPlayer))
			{
				// Not shot by a player, so checking for mob griefing
				if (!world.getGameRules().getBoolean("mobGriefing"))
				{
					return false;
				} // Not allowed to break things
			}
		}

		IBlockState state = world.getBlockState(pos);
		if (state == Blocks.AIR.getDefaultState())
		{
			return false;
		} // Didn't hit a valid block? Do we continue? Stop?
		// Unbreakable block
		if (state.getBlockHardness(world, pos) == -1) return false;

		boolean breakThis = false;

		if (strength >= 0) // Weak stuff
		{
			if (state.getMaterial() == Material.CAKE || state.getMaterial() == Material.GOURD)
			{
				breakThis = true;
			}
		}

		if (strength >= 1) // Medium stuff
		{
			if (state.getMaterial() == Material.GLASS || state.getMaterial() == Material.WEB
			    || state == Blocks.TORCH || state == Blocks.FLOWER_POT)
			{
				breakThis = true;
			}
		}

		if (strength >= 2) // Strong stuff
		{
			if (state.getMaterial() == Material.LEAVES || state.getMaterial() == Material.ICE) breakThis = true;
		}

		if (strength >= 3) // Super strong stuff
		{
			breakThis = true; // Default breakage, then negating what doesn't work

			if (state instanceof BlockLiquid || state.getMaterial() == Material.PORTAL || state == Blocks.MOB_SPAWNER
					|| state == Blocks.BEDROCK || state == Blocks.OBSIDIAN)
				breakThis = false;
		}

		if (state == Blocks.BEACON)
		{
			breakThis = false;
		} // ...beacons are made out of glass, too. Not breaking those.

		if (breakThis) // Breaking? Breaking!
		{
			if (QuiverbowConfig.sendBlockBreak)
			{
				if (entity instanceof ProjectileBase)
				{
					ProjectileBase projectile = (ProjectileBase) entity;

					// If you were shot by a player, are they allowed to break this block?
					Entity shooter = projectile.getShooter();

					if (shooter instanceof EntityPlayerMP)
					{
						GameType gametype = world.getWorldInfo().getGameType();
						int result = ForgeHooks.onBlockBreakEvent(world, gametype, (EntityPlayerMP) shooter, pos);

						if (result == -1)
						{
							return false;
						} // Not allowed to do this
					}
				}
				else if (entity instanceof EntityPlayerMP)
				{
					GameType gametype = entity.world.getWorldInfo().getGameType();
					int result = ForgeHooks.onBlockBreakEvent(entity.world, gametype, (EntityPlayerMP) entity, pos);

					if (result == -1)
					{
						return false;
					} // Not allowed to do this
				}
			}
			// else, not interested in sending such a event, so whatever

			world.destroyBlock(pos, true);

			return true; // Successfully broken
		}

		return false; // Couldn't break whatever's there
	}

	// Does the weapon have a custom name or other upgrades? If so then we're
	// transfering that to the new item
	public static void copyProps(IInventory craftMatrix, ItemStack newItem)
	{
		// Step 1, find the actual item (It's possible that this is not a
		// reloading action, meaning there is no weapon to copy the name from)

		int slot = 0;

		while (slot < 9)
		{
			ItemStack stack = craftMatrix.getStackInSlot(slot);

			if (!stack.isEmpty() && stack.getItem() instanceof WeaponBase) // Found it. Does it have a name tag?
			{
				if (stack.hasDisplayName() && !newItem.hasDisplayName())
				{
					newItem.setStackDisplayName(stack.getDisplayName());
				}
				// else, has no custom display name or the new item already has
				// one. Fine with me either way.

				// Upgrades
				if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("hasEmeraldMuzzle"))
				{
					if (!newItem.hasTagCompound())
					{
						newItem.setTagCompound(new NBTTagCompound());
					}
					newItem.getTagCompound().setBoolean("hasEmeraldMuzzle", true); // Keeping
				}

				return; // Either way, we're done here
			}
			// else, either doesn't exist or not what I'm looking for

			slot += 1;
		}
	}

	public static boolean canEntityBeSeen(World world, Entity observer, Entity entity)
	{
		return world.rayTraceBlocks(
				new Vec3d(observer.posX, observer.posY + observer.getEyeHeight(), observer.posZ),
				new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)) == null;
	}

	public static ItemStack createEmptyWeaponOrAmmoStack(Item item, int count)
	{
		ItemStack stack = new ItemStack(item, count);
		stack.setItemDamage(stack.getMaxDamage());
		return stack;
	}

	public static void playSoundAtEntityPos(Entity entity, SoundEvent sound, float volume, float pitch)
	{
		entity.world.playSound(entity.posX, entity.posY, entity.posZ, sound, entity.getSoundCategory(), volume, pitch,
				false);
	}

	public static EntityArrow createArrow(World world, EntityLivingBase shooter)
	{
		return ((ItemArrow) Items.ARROW).createArrow(world, ARROW_STACK, shooter);
	}

	public static RayTraceResult raytraceClosestObject(World world, @Nullable Entity exclude, Vec3d startVec, Vec3d endVec)
	{
		RayTraceResult result = world.rayTraceBlocks(startVec, endVec);
		double blockHitDistance = 0.0D; // The distance to the block that was hit
		if (result != null) blockHitDistance = result.hitVec.distanceTo(startVec);

		// Encloses the entire area where entities that could collide with this ray exist
		AxisAlignedBB entitySearchArea = new AxisAlignedBB(startVec.x, startVec.y, startVec.z,
				endVec.x, endVec.y, endVec.z);
		Entity hitEntity = null; // The closest entity that was hit
		double entityHitDistance = 0.0D; // The squared distance to the closest entity that was hit
		for (Entity entity : world.getEntitiesInAABBexcluding(exclude, entitySearchArea,
				EntitySelectors.NOT_SPECTATING))
		{
			// The collision AABB of the entity expanded by the collision border size
			AxisAlignedBB collisionBB = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
			RayTraceResult intercept = collisionBB.calculateIntercept(startVec, endVec);
			if (intercept != null)
			{
				double distance = startVec.distanceTo(intercept.hitVec);

				if ((distance < blockHitDistance || blockHitDistance == 0)
						&& (distance < entityHitDistance || entityHitDistance == 0.0D))
				{
					entityHitDistance = distance;
					hitEntity = entity;
				}
			}
		}

		if (hitEntity != null) result = new RayTraceResult(hitEntity, hitEntity.getPositionVector());

		return result;
	}

	public static void raytraceAll(List<RayTraceResult> results, World world, @Nullable Entity exclude, Vec3d startVec, Vec3d endVec)
	{
		RayTraceResult blockRaytrace = world.rayTraceBlocks(startVec, endVec);
		if (blockRaytrace != null) results.add(blockRaytrace);

		// Encloses the entire area where entities that could collide with this ray exist
		AxisAlignedBB entitySearchArea = new AxisAlignedBB(startVec.x, startVec.y, startVec.z,
				endVec.x, endVec.y, endVec.z);
		for (Entity entity : world.getEntitiesInAABBexcluding(exclude, entitySearchArea,
				EntitySelectors.NOT_SPECTATING))
		{
			// The collision AABB of the entity expanded by the collision border size
			AxisAlignedBB collisionBB = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
			RayTraceResult intercept = collisionBB.calculateIntercept(startVec, endVec);
			if (intercept != null) results.add(new RayTraceResult(entity, intercept.hitVec));
		}
	}

	public static int randomIntInRange(Random random, int min, int max)
	{
		return random.nextInt(max - min + 1) + min;
	}
}
