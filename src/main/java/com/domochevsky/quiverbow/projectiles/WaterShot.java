package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class WaterShot extends ProjectileBase
{
    public WaterShot(World world)
    {
        super(world);
    }

    public WaterShot(World world, Entity entity, WeaponProperties properties)
    {
        super(world);
        this.doSetup(entity, properties.getProjectileSpeed());
    }

    @Override
    public void doFlightSFX()
    {
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.WATER_BUBBLE,
                (byte) 4);
    }

    @Override
    public void onImpact(RayTraceResult target)
    {
        BlockPos pos;
        if (target.entityHit != null) // hit a entity
            pos = new BlockPos(target.entityHit.posX, target.entityHit.posY, target.entityHit.posZ);
        else // hit the terrain
            pos = target.getBlockPos().offset(target.sideHit);

        // Nether Check
        if (this.world.provider.doesWaterVaporize())
        {
            this.world.playSound((float) this.posX + 0.5F, (float) this.posY + 0.5F,
                    (float) this.posZ + 0.5F, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.AMBIENT, 0.5F,
                    2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F, false);

            NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SMOKE_LARGE,
                    (byte) 4);

            return; // No water in the nether, yo
        }

        // Is the space free?
        IBlockState hitState = this.world.getBlockState(pos);
        if (hitState.getBlock().isReplaceable(world, pos) || hitState.getMaterial() == Material.PLANTS)
        {
            // Can we edit this block at all?
            if (getShooter() instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) getShooter();
                if (!player.canPlayerEdit(pos, target.sideHit, ItemStack.EMPTY))
                {
                    return;
                } // Nope
            }

            // Putting water there!
            this.world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState(), 3);
        }

        // SFX
        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.WATER_BUBBLE,
                (byte) 4);
        this.playSound(SoundEvents.ENTITY_GENERIC_SPLASH, 1.0F, 1.0F);

        this.setDead(); // We've hit something, so begone with the projectile
    }
}
