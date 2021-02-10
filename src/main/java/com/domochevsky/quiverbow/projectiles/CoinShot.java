package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class CoinShot extends ProjectileBase
{
    private boolean shouldDrop;

    public CoinShot(World world)
    {
        super(world);
    }

    public CoinShot(World world, Entity entity, WeaponProperties properties, float accHor, float accVert)
    {
        super(world);
        this.doSetup(entity, properties.getProjectileSpeed(), accHor, accVert);
        this.damage = properties.generateDamage(world.rand);
        this.setDrop(properties.getBoolean(CommonProperties.SHOULD_DROP));
    }

    public void setDrop(boolean set)
    {
        this.shouldDrop = set;
    }

    @Override
    public void onImpact(RayTraceResult hitPos) // Server-side
    {
        if (hitPos.entityHit != null)
        {
            // Firing
            hitPos.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getShooter()),
                    this.damage); // Damage gets applied here

            hitPos.entityHit.hurtResistantTime = 0;
        }
        else
        {
            // Glass breaking
            Helper.tryBlockBreak(this.world, this, hitPos.getBlockPos(), 1);

            if (getShooter() instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) getShooter();

                if (this.shouldDrop && !player.capabilities.isCreativeMode)
                {
                    ItemStack nuggetStack = new ItemStack(Items.GOLD_NUGGET);
                    EntityItem entityitem = new EntityItem(this.world, hitPos.getBlockPos().getX(),
                            hitPos.getBlockPos().getY() + (double) 0.5F, hitPos.getBlockPos().getZ(), nuggetStack);
                    entityitem.setDefaultPickupDelay();

                    if (captureDrops)
                    {
                        capturedDrops.add(entityitem);
                    }
                    else
                    {
                        this.world.spawnEntity(entityitem);
                    }
                }
                // else, they're in creative mode, so no dropping nuggets
            }
            // else, either we don't have a shooter or they're not a player
        }

        // SFX
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.CRIT, (byte) 1);
        this.playSound(SoundEvents.BLOCK_METAL_BREAK, 1.0F, 3.0F);

        this.setDead(); // We've hit something, so begone with the projectile
    }
}
