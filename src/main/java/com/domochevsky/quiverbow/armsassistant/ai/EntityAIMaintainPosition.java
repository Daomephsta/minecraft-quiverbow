package com.domochevsky.quiverbow.armsassistant.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIMaintainPosition extends EntityAIBase
{
    private final EntityLiving entity;
    private final BlockPos targetPosition;
    private final double maxDistance,
                         moveSpeed;

    public EntityAIMaintainPosition(EntityLiving entity, BlockPos targetPosition, double maxDistance, double moveSpeed)
    {
        this.entity = entity;
        this.targetPosition = targetPosition;
        this.maxDistance = maxDistance;
        this.moveSpeed = moveSpeed;
    }

    @Override
    public boolean shouldExecute()
    {
        return entity.getDistanceSq(targetPosition) > maxDistance * maxDistance;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return !entity.getNavigator().noPath();
    }

    @Override
    public void startExecuting()
    {
        entity.getNavigator().tryMoveToXYZ(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ(), moveSpeed);
    }
}
