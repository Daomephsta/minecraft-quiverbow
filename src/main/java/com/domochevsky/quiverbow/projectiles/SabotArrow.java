package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SabotArrow extends ProjectileBase
{
    private WeaponProperties subArrowProperties;

    public SabotArrow(World world)
    {
        super(world);
    }

    public SabotArrow(World world, Entity entity, WeaponProperties properties)
    {
        super(world);
        this.subArrowProperties = properties.getSubProjectileProperties();
        this.doSetup(entity, properties.getProjectileSpeed());
    }

    @Override
    public void onImpact(RayTraceResult target) // Server-side
    {
        // Shooter can be null if shooter is offline
        if (!hasShooter())
        {
            setDead();
            return;
        }

        if (target.entityHit != null) // Hit a entity
        {
            target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getShooter()), 3);
            target.entityHit.hurtResistantTime = 0; // No immunity frames
        }
        else // Hit the terrain
        {
            // Glass breaking
            Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 1);
        }

        // Spawning a rose of arrows here
        for(int i = 0; i < 8; i++)
        {
            this.fireArrow(-45.0F, i * 45.0F);
        }

        // SFX
        this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 3.0F);
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SMOKE_LARGE,
                (byte) 4);

        this.setDead(); // We've hit something, so begone with the projectile
    }

    private void fireArrow(float pitch, float yaw)
    {
        EntityArrow arrow = Helper.createArrow(world, getShooter());
        arrow.setPosition(posX, posY + 1.0F, posZ);
        // Divide by speed because this base damage will be multiplied by the speed
        arrow.setDamage(Math.round(
            subArrowProperties.generateDamage(world.rand) / subArrowProperties.getProjectileSpeed()));
        arrow.shoot(this, pitch, yaw, 0.0F, subArrowProperties.getProjectileSpeed(), 0.5F);

        this.world.spawnEntity(arrow);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float par2)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else // Mortar shots can be swatted out of the way with a bit of expertise
        {
            this.markVelocityChanged();

            if (source.getTrueSource() != null) // Damaged by a entity
            {
                Vec3d look = source.getTrueSource().getLookVec();
                if (look != null)
                {
                    this.motionX = look.x;
                    this.motionY = look.y;
                    this.motionZ = look.z;
                }

                if (source.getTrueSource() instanceof EntityLivingBase)
                {
                    setShooter((EntityLivingBase) source.getTrueSource());
                }

                return true;
            }
            // else, not damaged by an entity
        }

        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);
        this.subArrowProperties = WeaponProperties.readFromNBT(tag, "subProjectileProperties");
    }


    @Override
    protected void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);
        this.subArrowProperties.writeToNBT(tag, "subProjectileProperties");
    }
}
