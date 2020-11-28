package com.domochevsky.quiverbow.weapons.base.fireshape;

import com.domochevsky.quiverbow.config.WeaponProperties;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SingleShotFireShape implements FireShape
{
    private final ProjectileFactory projectileFactory;

    public SingleShotFireShape(ProjectileFactory projectileFactory)
    {
        this.projectileFactory = projectileFactory;
    }

    @Override
    public boolean fire(World world, EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (!world.isRemote)
            world.spawnEntity(projectileFactory.createProjectile(world, shooter, properties));
        return true;
    }
}
