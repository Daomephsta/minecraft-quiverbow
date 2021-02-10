package com.domochevsky.quiverbow.projectiles;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BigRocket extends ProjectileBase
{
    public static final Pair<String, String> TRAVEL_TIME = Pair.of("maxFlightTime", "The maximum flight time of the rocket. It will explode after this.");

    private int travelTicksMax;
    private boolean dmgTerrain;

    public BigRocket(World world)
    {
        super(world);
    }

    public BigRocket(World world, Entity entity, WeaponProperties properties)
    {
        super(world);
        this.doSetup(entity, properties.getProjectileSpeed());
        this.explosionSize = properties.getFloat(CommonProperties.EXPLOSION_SIZE);
        this.travelTicksMax = properties.getInt(BigRocket.TRAVEL_TIME);
        this.dmgTerrain = properties.getBoolean(CommonProperties.DAMAGE_TERRAIN);
    }

    @Override
    public void onImpact(RayTraceResult target) // Server-side
    {
        boolean griefing = true; // Allowed by default

        if (getShooter() instanceof EntityPlayer)
        {
            griefing = this.dmgTerrain; // It's up to player settings to
            // allow/forbid this
        }
        else
        {
            griefing = this.world.getGameRules().getBoolean("mobGriefing"); // Are
            // we
            // allowed
            // to
            // break
            // things?
        }

        if (!world.isRemote)
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, this.explosionSize, griefing); // Bewm

        this.setDead(); // We've hit something, so begone with the projectile
    }

    @Override
    public void doFlightSFX()
    {
        if (travelTicksMax > 0) // We have a fixed travel time, so lesse...
        {
            if (this.ticksExisted > this.travelTicksMax) // Our fuse has run out
            {
                boolean griefing = true; // Allowed by default

                if (getShooter() instanceof EntityPlayer)
                {
                    griefing = this.dmgTerrain; // It's up to player settings to
                    // allow/forbid this
                }
                else
                {
                    griefing = this.world.getGameRules().getBoolean("mobGriefing"); // Are
                    // we
                    // allowed
                    // to
                    // break
                    // things?
                }

                if (!world.isRemote)
                    this.world.createExplosion(this, this.posX, this.posY, this.posZ, this.explosionSize, griefing); // Bewm

                this.setDead(); // We've hit something, so begone with the
                // projectile
            }
        }
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.FIREWORKS_SPARK, (byte) 8);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float par2) // Big
    // rockets
    // can be
    // swatted
    // out of
    // the way
    // with a
    // bit of
    // expertise
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
                    setShooter((EntityLivingBase) source.getTrueSource());

                return true;
            }
            // else, not damaged by an entity
        }

        return false;
    }
}
