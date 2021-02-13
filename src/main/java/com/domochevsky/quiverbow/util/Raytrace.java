package com.domochevsky.quiverbow.util;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Raytrace
{
    public static final int LIQUID = 1 << 0,
                            BLOCK = 1 << 1,
                            ENTITY = 1 << 2;

    public static List<RayTraceResult> all(List<RayTraceResult> results, World world,
        @Nullable Entity exclude, Vec3d startVec, Vec3d endVec)
    {
        return all(results, world, exclude, startVec, endVec, BLOCK | ENTITY);
    }

    public static List<RayTraceResult> all(List<RayTraceResult> results, World world,
        @Nullable Entity exclude, Vec3d startVec, Vec3d endVec, int flags)
    {
        if ((flags & BLOCK) == BLOCK)
        {
            RayTraceResult blockRaytrace = world.rayTraceBlocks(startVec, endVec, (flags & LIQUID) == LIQUID);
            if (blockRaytrace != null) results.add(blockRaytrace);
        }
        if ((flags & ENTITY) == ENTITY)
        {
            // Encloses the entire area where entities that could collide with this ray exist
            AxisAlignedBB entitySearchArea = new AxisAlignedBB(startVec.x, startVec.y, startVec.z, endVec.x, endVec.y, endVec.z);
            for (Entity entity : world.getEntitiesInAABBexcluding(exclude, entitySearchArea, EntitySelectors.NOT_SPECTATING))
            {
                // The collision AABB of the entity expanded by the collision border size
                AxisAlignedBB collisionBB = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
                RayTraceResult intercept = collisionBB.calculateIntercept(startVec, endVec);
                if (intercept != null) results.add(new RayTraceResult(entity, intercept.hitVec));
            }
        }
        return results;
    }

    public static RayTraceResult closest(World world, @Nullable Entity exclude, Vec3d startVec, Vec3d endVec)
    {
        return closest(world, exclude, startVec, endVec, BLOCK | ENTITY);
    }

    public static RayTraceResult closest(World world, @Nullable Entity exclude, Vec3d startVec, Vec3d endVec, int flags)
    {
        RayTraceResult result = null;
        if ((flags & BLOCK) == BLOCK)
            result = world.rayTraceBlocks(startVec, endVec, (flags & LIQUID) == LIQUID);
        if ((flags & ENTITY) == ENTITY)
        {
            // Encloses the entire area where entities that could collide with this ray exist
            AxisAlignedBB entitySearchArea = new AxisAlignedBB(startVec.x, startVec.y, startVec.z, endVec.x, endVec.y, endVec.z);
            Entity hitEntity = null; // The closest entity that was hit
            for (Entity entity : world.getEntitiesInAABBexcluding(exclude, entitySearchArea, EntitySelectors.NOT_SPECTATING))
            {
                // The collision AABB of the entity expanded by the collision border size
                AxisAlignedBB collisionBB = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
                RayTraceResult intercept = collisionBB.calculateIntercept(startVec, endVec);
                if (intercept != null &&
                    (result == null || intercept.hitVec.lengthSquared() < result.hitVec.lengthSquared()))
                {
                    result = intercept;
                    hitEntity = entity;
                }
            }
            if (hitEntity != null) result = new RayTraceResult(hitEntity, hitEntity.getPositionVector());
        }
        return result;
    }
}
