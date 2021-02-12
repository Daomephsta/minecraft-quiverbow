package com.domochevsky.quiverbow;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.domochevsky.quiverbow.config.QuiverbowConfig;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.ProjectileBase;

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
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class Helper
{
    private static final ItemStack ARROW_STACK = new ItemStack(Items.ARROW);

    /**
     * Applies knockback with server-client syncing
     * @param target entity to knock back
     * @param strength force of knockback
     */
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
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            shot.canBePickedUp = !player.capabilities.isCreativeMode;
        }
        else
        {
            shot.canBePickedUp = false;
        }
    }

    /**
     * Applies a potion effect, extending any existing instance
     * @param entitylivingbase entity to apply effect to
     * @param effect effect to apply
     */
    public static void applyPotionEffectExtending(EntityLivingBase entitylivingbase, PotionEffect effect)
    {
        int duration = effect.getDuration();
        PotionEffect existing = entitylivingbase.getActivePotionEffect(effect.getPotion());
        if (existing != null)
            duration += existing.getDuration();
        entitylivingbase.addPotionEffect(
            new PotionEffect(effect.getPotion(), duration, effect.getAmplifier()));
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

            if (projectile.getShooter() != null && !(projectile.getShooter() instanceof EntityPlayer))
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
                breakBlock(world, entity, pos);
            }
            // else, not interested in sending such a event, so whatever

            world.destroyBlock(pos, true);

            return true; // Successfully broken
        }

        return false; // Couldn't break whatever's there
    }

    public static void breakBlock(World world, Entity breaker, BlockPos pos)
    {
        if (breaker instanceof ProjectileBase)
            breaker = ((ProjectileBase) breaker).getShooter();
        if (breaker instanceof EntityPlayerMP)
        {
            if (ForgeHooks.onBlockBreakEvent(breaker.world,
                breaker.world.getWorldInfo().getGameType(),
                (EntityPlayerMP) breaker, pos) == -1)
            {
                return;
            }
        }
        world.destroyBlock(pos, true);
    }

    // Unused, but it's useful
    public static boolean canEdit(World world, EntityLivingBase editor, BlockPos pos)
    {
        if (editor instanceof EntityPlayer)
            return world.isBlockModifiable((EntityPlayer) editor, pos);
        else
            return world.getGameRules().getBoolean("mobGriefing");
    }

    public static boolean canEntityBeSeen(World world, Entity observer, Entity entity)
    {
        return world.rayTraceBlocks(
                new Vec3d(observer.posX, observer.posY + observer.getEyeHeight(), observer.posZ),
                new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)) == null;
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

    public static List<RayTraceResult> raytraceAll(List<RayTraceResult> results, World world, @Nullable Entity exclude, Vec3d startVec, Vec3d endVec)
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
        return results;
    }

    public static int randomIntInRange(Random random, int min, int max)
    {
        return random.nextInt(max - min + 1) + min;
    }

    public static void causeSelfDamage(EntityLivingBase self, float amount)
    {
        if (self instanceof EntityPlayer)
            self.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) self), amount);
        else
            self.attackEntityFrom(DamageSource.causeMobDamage(self), amount);
    }

    public static boolean trySendActionBarMessage(EntityLivingBase recipient, String translationKey)
    {
        if (recipient instanceof EntityPlayer)
        {
            ((EntityPlayer) recipient).sendStatusMessage(new TextComponentTranslation(translationKey), true);
            return true;
        }
        return false;
    }
}
