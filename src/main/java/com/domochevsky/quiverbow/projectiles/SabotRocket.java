package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SabotRocket extends ProjectileBase
{
    private WeaponProperties smallRocketProperties;

    public SabotRocket(World world)
    {
        super(world);
    }

    public SabotRocket(World world, Entity entity, WeaponProperties properties)
    {
        super(world);
        this.smallRocketProperties = properties.getSubProjectileProperties();
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

        // Spawning a rose of rockets here
        this.fireRocket(1.0f, 0.0f);
        this.fireRocket(180.0f, 0.0f);
        this.fireRocket(90.0f, 0.0f);
        this.fireRocket(-90.0f, 0.0f);
        this.fireRocket(45.0f, -45.0f);
        this.fireRocket(-45.0f, -45.0f);
        this.fireRocket(135.0f, -45.0f);
        this.fireRocket(-135.0f, 45.0f);

        // SFX
        this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 3.0F);
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SMOKE_LARGE,
                (byte) 4);

        this.setDead(); // We've hit something, so begone with the projectile
    }

    private void fireRocket(float accHor, float accVert)
    {
        SmallRocket rocket = new SmallRocket(world, this, smallRocketProperties, accHor, accVert);
        rocket.setShooter(getShooter());
        this.world.spawnEntity(rocket);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float par2)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else //Big rockets can be swatted out of the way with a bit of expertise
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
                    setShooter((EntityLivingBase) source.getTrueSource());

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
        this.smallRocketProperties = WeaponProperties.readFromNBT(tag, "subProjectileProperties");
    }


    @Override
    protected void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);
        this.smallRocketProperties.writeToNBT(tag, "subProjectileProperties");
    }
}
