package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class PotatoShot extends ProjectileBase
{
    private boolean shouldDrop;

    public PotatoShot(World world)
    {
        super(world);
    }

    public PotatoShot(World world, Entity entity, WeaponProperties properties)
    {
        super(world);
        this.doSetup(entity, properties.getProjectileSpeed());
        this.damage = properties.generateDamage(rand);
        this.setDrop(properties.getBoolean(CommonProperties.SHOULD_DROP));
    }

    public void setDrop(boolean set)
    {
        this.shouldDrop = set;
    }

    @Override
    public void onImpact(RayTraceResult target)
    {
        if (target.entityHit != null)
        {
            // Damage
            target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getShooter()),
                    this.damage);
        }
        else
        {
            // Glass breaking
            Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 1);

            if (this.shouldDrop && this.canBePickedUp)
            {
                ItemStack nuggetStack = new ItemStack(Items.BAKED_POTATO);
                EntityItem entityitem = new EntityItem(this.world, target.getBlockPos().getX(),
                        target.getBlockPos().getY() + 0.5d, target.getBlockPos().getZ(), nuggetStack);
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
        }

        // SFX
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SMOKE_NORMAL,
                (byte) 2);
        this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.6F, 0.7F);

        this.setDead(); // We've hit something, so begone with the projectile
    }
}
