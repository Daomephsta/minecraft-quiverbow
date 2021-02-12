package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class Seed extends ProjectileBase
{
    public Seed(World world)
    {
        super(world);
    }

    public Seed(World world, Entity entity, WeaponProperties properties, float accHor, float accVert)
    {
        super(world);
        this.doSetup(entity, properties.getProjectileSpeed(), accHor, accVert);
        this.damage = properties.generateDamage(rand);
    }

    @Override
    public void onImpact(RayTraceResult target)
    {
        if (target.entityHit != null)
        {
            target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getShooter()),
                    this.damage);

            target.entityHit.hurtResistantTime = 0; // No rest for the wicked

        }
        else
        {

            IBlockState state = this.world.getBlockState(target.getBlockPos());
            IBlockState stateAbove = this.world.getBlockState(target.getBlockPos().up());

            // Glass breaking
            Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 0);

            if (state.getBlock() == Blocks.FARMLAND && stateAbove.getMaterial() == Material.AIR)
            {
                this.world.setBlockState(target.getBlockPos().up(), Blocks.MELON_STEM.getDefaultState(), 3);
            }
        }

        this.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.2F, 3.0F);
        this.setDead(); // We've hit something, so begone with the projectile
    }
}
