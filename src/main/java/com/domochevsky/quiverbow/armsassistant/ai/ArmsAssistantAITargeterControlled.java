package com.domochevsky.quiverbow.armsassistant.ai;

import com.domochevsky.quiverbow.armsassistant.EntityArmsAssistant;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.domochevsky.quiverbow.util.InventoryHelper;
import com.domochevsky.quiverbow.util.Raytrace;
import com.domochevsky.quiverbow.weapons.AATargeter;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class ArmsAssistantAITargeterControlled extends EntityAIBase
{
    private final EntityArmsAssistant armsAssistant;

    public ArmsAssistantAITargeterControlled(EntityArmsAssistant armsAssistant)
    {
        this.armsAssistant = armsAssistant;
    }

    @Override
    public boolean shouldExecute()
    {
        if (armsAssistant.getOwner() instanceof EntityLivingBase)
        {
            EntityLivingBase owner = (EntityLivingBase) armsAssistant.getOwner();
            return owner.getDistanceSq(armsAssistant) <= AATargeter.TARGETING_DISTANCE * AATargeter.TARGETING_DISTANCE &&
                !InventoryHelper.findItemInHands(owner, ItemRegistry.AA_TARGET_ASSISTANT).isEmpty();
        }
        return false;
    }

    @Override
    public void updateTask()
    {
        EntityLivingBase owner = (EntityLivingBase) armsAssistant.getOwner();
        Vec3d eyeVec = owner.getPositionVector().addVector(0.0D, owner.getEyeHeight(), 0.0D);
        Vec3d rayEndVec = eyeVec.add(owner.getLookVec().scale(AATargeter.TARGETING_DISTANCE));
        RayTraceResult ownerTarget = Raytrace.closest(owner.world, owner, eyeVec, rayEndVec);
        if (ownerTarget == null)
            armsAssistant.getLookHelper().setLookPosition(rayEndVec.x, rayEndVec.y, rayEndVec.z, 10.0F, 10.0F);
        else switch (ownerTarget.typeOfHit)
        {
        case BLOCK:
        case ENTITY:
            armsAssistant.getLookHelper().setLookPosition(ownerTarget.hitVec.x, ownerTarget.hitVec.y,
                ownerTarget.hitVec.z, 10.0F, 10.0F);
            break;
        case MISS:
            break;
        }
        if (owner.getActiveItemStack().getItem() == ItemRegistry.AA_TARGET_ASSISTANT)
        {
            armsAssistant.tryFire();
        }
    }
}
