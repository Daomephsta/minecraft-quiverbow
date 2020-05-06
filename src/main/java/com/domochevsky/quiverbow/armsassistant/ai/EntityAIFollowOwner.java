package com.domochevsky.quiverbow.armsassistant.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIFollowOwner<E extends EntityLiving & IEntityOwnable> extends EntityAIBase
{
    private final E entity;
    private final double maxDistance,
                         moveSpeed;

    public EntityAIFollowOwner(E entity, double maxDistance, double moveSpeed)
    {
        this.entity = entity;
        this.maxDistance = maxDistance;
        this.moveSpeed = moveSpeed;
    }

    @Override
    public boolean shouldExecute()
    {
        if (entity.getOwner() == null)
            return false;
        return entity.getDistanceSq(entity.getOwner()) > maxDistance * maxDistance;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return !entity.getNavigator().noPath();
    }

    @Override
    public void startExecuting()
    {
        entity.getNavigator().tryMoveToEntityLiving(entity.getOwner(), moveSpeed);
    }
}
