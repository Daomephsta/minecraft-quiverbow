package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
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
        if (target.entityHit != null) // Hit a entity
        {
            target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), 3);
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
        SmallRocket smallRocket = new SmallRocket(this.world, this, smallRocketProperties, accHor, accVert);
        smallRocket.shootingEntity = this.shootingEntity;
        this.world.spawnEntity(smallRocket);
    }


    //Big rockets can be swatted out of the way with a bit of expertise
    @Override
    public boolean attackEntityFrom(DamageSource source, float par2)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else // Not invulnerable
        {
            this.markVelocityChanged();

            if (source.getTrueSource() != null) // Damaged by a entity
            {
                Vec3d vec3 = source.getTrueSource().getLookVec(); // Which is
                // looking that
                // way...

                if (vec3 != null)
                {
                    this.motionX = vec3.x;
                    this.motionY = vec3.y;
                    this.motionZ = vec3.z;
                }

                if (source.getTrueSource() instanceof EntityLivingBase)
                {
                    this.shootingEntity = (EntityLivingBase) source.getTrueSource();
                }

                return true;
            }
            // else, not damaged by an entity
        }

        return false;
    }
}
