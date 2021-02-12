package com.domochevsky.quiverbow.projectiles;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ProxyThorn extends ProjectileBase
{
    public static final Pair<String, String>
        PROX_CHECK_INTERVAL = Pair.of("checkInterval", "The number of ticks inbetween proximity checks"),
        THORN_AMOUNT = Pair.of("thornAmount", "The number of thorns created when a proximity thorn detonates"),
        TRIGGER_DISTANCE = Pair.of("triggerDistance", "The distance proximity thorns trigger at");

    public int proxyDelay = 20; // Only checking every so often
    private double thornSpeed = 1.5;
    public double triggerDistance = 2.0;
    public int thornAmount = 32;

    private EnumFacing hitSide;

    public ProxyThorn(World world)
    {
        super(world);
    }

    public ProxyThorn(World world, Entity entity, WeaponProperties properties)
    {
        super(world);
        this.doSetup(entity, properties.getProjectileSpeed());
        this.thornSpeed = properties.getProjectileSpeed();
        this.damage = properties.generateDamage(rand);
        this.ticksInGroundMax = properties.getInt(CommonProperties.DESPAWN_TIME);
        this.triggerDistance = properties.getFloat(TRIGGER_DISTANCE);
        this.proxyDelay = properties.getInt(PROX_CHECK_INTERVAL);
        this.thornAmount = properties.getInt(THORN_AMOUNT);
    }

    @Override
    public void onImpact(RayTraceResult movPos) // Server-side
    {
        if (movPos.entityHit != null) // We hit a living thing!
        {
            movPos.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getShooter()),
                    this.damage); // Damage gets applied here
            movPos.entityHit.hurtResistantTime = 0; // No immunity frames

            this.goBoom();
            this.setDead(); // We've hit something, so begone with the // projectile
        }

        else // Hit the terrain
        {
            if (Helper.tryBlockBreak(this.world, this, movPos.getBlockPos(), 1))
            {
                // this.goBoom();
            }
            else // Didn't manage to break that block, so we're stuck now for a short while
            {
                IBlockState stuckState = this.world.getBlockState(movPos.getBlockPos());
                this.stuckBlock = stuckState.getBlock();
                // Only make an impact sound if the block this is stuck in has changed
                if (stuckBlockX != movPos.getBlockPos().getX() || stuckBlockY != movPos.getBlockPos().getY()
                        || stuckBlockZ != movPos.getBlockPos().getZ())
                {
                    this.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0F, 0.3F);
                    NetHelper.sendParticleMessageToAllPlayers(this.world, this,
                            EnumParticleTypes.SMOKE_NORMAL, (byte) 4);
                }
                stuckBlockX = movPos.getBlockPos().getX();
                stuckBlockY = movPos.getBlockPos().getY();
                stuckBlockZ = movPos.getBlockPos().getZ();

                this.motionX = movPos.hitVec.x - this.posX;
                this.motionY = movPos.hitVec.y - this.posY;
                this.motionZ = movPos.hitVec.z - this.posZ;

                this.hitSide = movPos.sideHit;

                float distance = MathHelper
                        .sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

                this.posX -= this.motionX / distance * 0.05000000074505806D;
                this.posY -= this.motionY / distance * 0.05000000074505806D;
                this.posZ -= this.motionZ / distance * 0.05000000074505806D;

                this.inGround = true;

                this.arrowShake = 7;

                if (stuckState.getMaterial() != Material.AIR)
                {
                    this.stuckBlock.onEntityCollidedWithBlock(this.world, movPos.getBlockPos(), stuckState, this);
                }
            }

            this.setEntityBoundingBox(new AxisAlignedBB(-0.2d, 0.0d, -0.2d, 0.2d, 0.2d, 0.2d)); // Attackable
        }
    }

    @Override
    public void doFlightSFX()
    {
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.CRIT, (byte) 2);
    }

    @Override
    public void doInGroundSFX() //Checking proximity for living entities, to see if I need to explode
    {
        if (this.ticksInGround == this.ticksInGroundMax - 1)
        {
            this.goBoom(); // Out of time
        }

        if (this.proxyDelay > 0)
        {
            this.proxyDelay -= 1;
            return; // Not yet
        }

        this.proxyDelay = 20; // Reset

        // Go time
        AxisAlignedBB box = this.getEntityBoundingBox().expand(this.triggerDistance, this.triggerDistance,
                this.triggerDistance);
        List<?> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, box);

        Entity potentialEntity;
        int counter = 0;
        boolean skip = false;

        while (counter < list.size())
        {
            skip = false;

            potentialEntity = (Entity) list.get(counter);

            if (potentialEntity instanceof EntityPlayer) // Not triggering for creative mode players
            {
                EntityPlayer player = (EntityPlayer) potentialEntity;
                if (player.capabilities.isCreativeMode)
                {
                    skip = true;
                }
            }

            if (!skip && Helper.canEntityBeSeen(this.world, this, potentialEntity))
            {
                this.goBoom(); // We can see them! Boom time!
                return;
            }

            // Next!
            counter += 1;
        }
    }

    // Spraying a bunch of thorns in random directions
    private void goBoom()
    {
        if (this.hitSide != null)
        {
            // Moving out of the block we're stuck in, to get a clear shot
            switch (this.hitSide)
            {
            case DOWN :
                this.posY -= 0.5;
                break;
            case UP :
                this.posY += 0.5;
                break;
            case EAST :
                this.posX += 0.5;
                break;
            case WEST :
                this.posX -= 0.5;
                break;
            case NORTH :
                this.posZ += 0.5;
                break;
            case SOUTH :
                this.posZ -= 0.5;
                break;
            }
        }

        int amount = this.thornAmount;

        while (amount > 0)
        {
            this.fireThorn();
            amount -= 1;
        }

        // SFX
        this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.3F, 2.0F);
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SMOKE_LARGE,
                (byte) 8);

        this.setDead(); // We're done here
    }

    // Blasts a thorn in a random direction
    private void fireThorn()
    {
        // Random dir
        int thornYaw = this.world.rand.nextInt(360) + 1; // Range will be between 1 and 360
        thornYaw -= 180; // Range between -180 and 180

        int thornPitch = this.world.rand.nextInt(360) + 1; // Range will be between 1 and 360
        thornPitch -= 180; // Range between -180 and 180

        int dmg = this.world.rand.nextInt(2) + 1; // Range will be between 1 and 2

        // Firing
        Thorn projectile = new Thorn(this.world, this, (float) this.thornSpeed, thornYaw, thornPitch);
        if (hasShooter())
            projectile.setShooter(getShooter());
        projectile.damage = dmg;

        this.world.spawnEntity(projectile);
    }

    @Override
    public boolean hitByEntity(Entity entity)
    {
        this.goBoom();
        return false;
    }
}
