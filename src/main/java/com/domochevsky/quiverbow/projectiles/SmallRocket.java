package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SmallRocket extends ProjectileBase
{
    private boolean dmgTerrain;

    public SmallRocket(World world)
    {
        super(world);
    }

    public SmallRocket(World world, Entity entity, WeaponProperties properties)
    {
        this(world, entity, properties, 0, 0);
    }

    public SmallRocket(World world, Entity entity,
        WeaponProperties properties, float accHor, float accVert)
    {
        super(world);
        this.doSetup(entity, properties.getProjectileSpeed(), accHor, accVert);
        this.damage = properties.generateDamage(rand);
        this.fireDuration = properties.getInt(CommonProperties.FIRE_DUR_ENTITY);
        this.explosionSize = properties.getFloat(CommonProperties.EXPLOSION_SIZE);
        this.dmgTerrain = properties.getBoolean(CommonProperties.DAMAGE_TERRAIN);
    }

    public SmallRocket(World world, Entity entity, float speed, float accHor, float accVert)
    {
        super(world);
        this.doSetup(entity, speed, accHor, accVert);
    }

    @Override
    public void onImpact(RayTraceResult target) // Server-side
    {
        if (target.entityHit != null) // Hit a entity
        {
            // Damage
            target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getShooter()),
                    this.damage);
            target.entityHit.hurtResistantTime = 0; // No immunity frames

            // Knockback
            double f3 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (f3 > 0.0F)
            {
                target.entityHit.addVelocity(
                        this.motionX * this.knockbackStrength * 0.6000000238418579D / f3, 0.1D,
                        this.motionZ * this.knockbackStrength * 0.6000000238418579D / f3);
            }

            // Effect
            if (!(target.entityHit instanceof EntityEnderman))
            {
                target.entityHit.setFire(this.fireDuration);
            }

            this.setDead();

        }
        else // Hit a block
        {
            IBlockState state = this.world.getBlockState(target.getBlockPos());

            // Glass breaking, once
            if (Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 1) && this.targetsHit < 1)
            {
                this.targetsHit += 1;
            }
            else
            {
                this.setDead();
            } // else, either we didn't break that block or we already hit one entity

            // Let's ignite TNT explicitly here.
            if (state == Blocks.TNT)
            {
                this.world.setBlockToAir(target.getBlockPos()); // setBlockToAir

                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(this.world,
                        target.getBlockPos().getX() + 0.5F,
                        target.getBlockPos().getY() + 0.5F,
                        target.getBlockPos().getZ() + 0.5F, getShooter());

                this.world.spawnEntity(entitytntprimed);

                entitytntprimed.playSound(SoundEvents.ENTITY_TNT_PRIMED, 1.0F, 1.0F);
            }
            // else, block is not TNT
        }

        if (!this.isInWater()) // Only explode if we're not in water
        {
            boolean griefing = true; // Allowed by default

            if (getShooter() instanceof EntityPlayer)
            {
                griefing = this.dmgTerrain; // It's up to player settings to allow/forbid this
            }
            else
            {
                griefing = this.world.getGameRules().getBoolean("mobGriefing");
            }

            if (!world.isRemote)
                this.world.createExplosion(this, this.posX, this.posY, this.posZ, this.explosionSize, griefing);
        }
    }

    @Override
    public void doFlightSFX()
    {
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.FIREWORKS_SPARK,
                (byte) 4);
    }
}
