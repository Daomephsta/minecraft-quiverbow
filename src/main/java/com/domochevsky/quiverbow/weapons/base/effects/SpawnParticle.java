package com.domochevsky.quiverbow.weapons.base.effects;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon.Effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class SpawnParticle implements Effect
{
    private final EnumParticleTypes type;
    private final double offsetY;
    private final double velocityX, velocityY, velocityZ;
    private final int[] parameters;

    public SpawnParticle(EnumParticleTypes type, double offsetY, int... parameters)
    {
        this(type, offsetY, 0, 0, 0, parameters);
    }

    public SpawnParticle(EnumParticleTypes type, double offsetY,
        double velocityX, double velocityY, double velocityZ, int... parameters)
    {
        this.type = type;
        this.offsetY = offsetY;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.parameters = parameters;
    }

    @Override
    public void apply(World world, EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        world.spawnParticle(type, shooter.posX, shooter.posY + offsetY, shooter.posZ,
            velocityX, velocityY, velocityZ, parameters);
    }

}
