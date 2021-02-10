package com.domochevsky.quiverbow.projectiles;

import java.util.HashSet;
import java.util.Set;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.google.common.collect.Sets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SoulShot extends ProjectileBase
{
    private static final Set<ResourceLocation> BLACKLIST = new HashSet<>();

    public SoulShot(World world)
    {
        super(world);
    }

    public SoulShot(World world, Entity entity, WeaponProperties properties)
    {
        super(world);
        this.doSetup(entity, properties.getProjectileSpeed());
    }

    @Override
    public void onImpact(RayTraceResult target)
    {
        if (target.entityHit != null)
        {
            // Can't catch players or bosses
            if (target.entityHit instanceof EntityPlayer)
            {
                DamageSource magicDamage = DamageSource.causeIndirectMagicDamage(this, getShooter());
                target.entityHit.attackEntityFrom(magicDamage, 10);
                this.damageShooter();
                return;
            }
            if (!target.entityHit.isNonBoss())
            {
                this.damageShooter();
                Helper.trySendActionBarMessage(getShooter(), QuiverbowMain.MODID + ".soul_cairn.boss");
                return;
            }
            ResourceLocation entityKey = EntityList.getKey(target.entityHit);
            if(BLACKLIST.contains(entityKey) || !EntityList.ENTITY_EGGS.containsKey(entityKey))
            {
                Helper.trySendActionBarMessage(getShooter(), QuiverbowMain.MODID + ".soul_cairn.blacklisted");
                return;
            }
            getShooter().entityDropItem(eggFor(target.entityHit), 0);
            target.entityHit.setDead();

            NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.SMOKE_LARGE,
                    (byte) 4);

            this.setDead(); // We've hit something, so begone with the
            // projectile
        }
        else // Hit the terrain
        {
            // Glass breaking
            if (!Helper.tryBlockBreak(this.world, this, target.getBlockPos(), 1))
            {
                this.setDead();
            } // Only begone if we didn't hit glass
        }
    }

    private static final Set<String> WORLD_STATE_TAGS =
        Sets.newHashSet("Pos", "Motion", "Rotation", "FallDistance", "Fire", "Air", "OnGround", "Dimension");
    private ItemStack eggFor(Entity entity)
    {
        ItemStack egg = new ItemStack(Items.SPAWN_EGG);
        ItemMonsterPlacer.applyEntityIdToItemStack(egg, EntityList.getKey(entity));
        System.out.println(egg.getTagCompound());
        NBTTagCompound entityTag = egg.getSubCompound("EntityTag");
        entity.writeToNBT(entityTag);
        // Strip world state dependent tags
        for (String key : WORLD_STATE_TAGS)
            entityTag.removeTag(key);
        return egg;
    }

    public static void blacklistEntity(ResourceLocation entityID)
    {
        if (EntityList.isRegistered(entityID)) BLACKLIST.add(entityID);
        else QuiverbowMain.logger.warn("No entity is registered with the id {}", entityID);
    }

    @Override
    public boolean doDropOff()
    {
        return false;
    } // If this returns false then we won't care about gravity

    @Override
    public void doFlightSFX()
    {
        // Doing our own (reduced) gravity
        this.motionY -= 0.025; // Default is 0.05

        NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.PORTAL, (byte) 3);
    }

    void damageShooter()
    {
        if (getShooter() == null)
            return;

        getShooter().attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, getShooter()), 10);
    }
}
