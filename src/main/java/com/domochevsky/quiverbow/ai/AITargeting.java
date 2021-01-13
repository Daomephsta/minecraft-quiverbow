package com.domochevsky.quiverbow.ai;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class AITargeting
{
    public static RayTraceResult getRayTraceResultFromPlayer(World world, EntityPlayer player, double targetingDistance)
    {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;

        double playerX = player.prevPosX + (player.posX - player.prevPosX) * f;
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * f
                + (world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()); // isRemote
        // check
        // to
        // revert
        // changes
        // to
        // ray
        // trace
        // position
        // due
        // to
        // adding
        // the
        // eye
        // height
        // clientside
        // and
        // player
        // yOffset
        // differences
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * f;

        Vec3d vecPlayer = new Vec3d(playerX, playerY, playerZ);

        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;

        double maxDistance = targetingDistance;

        Vec3d vecTarget = vecPlayer.addVector(f7 * maxDistance, f6 * maxDistance, f8 * maxDistance);

        return world.rayTraceBlocks(vecPlayer, vecTarget, false, false, true); // false,
        // true,
        // false
    }
}
