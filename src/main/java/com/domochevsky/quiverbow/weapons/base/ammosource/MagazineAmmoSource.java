package com.domochevsky.quiverbow.weapons.base.ammosource;

import com.domochevsky.quiverbow.ammo.AmmoMagazine;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.util.NBTags;
import com.domochevsky.quiverbow.weapons.base.Weapon.Effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MagazineAmmoSource extends SimpleAmmoSource
{
    private final AmmoMagazine magazine;
    private Effect[] unloadEffects;

    public MagazineAmmoSource(AmmoMagazine magazine)
    {
        this.magazine = magazine;
    }

    @Override
    public boolean consumeAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        boolean consumed = super.consumeAmmo(shooter, stack, properties);
        // Eject if no more ammo after firing
        if (!hasAmmo(shooter, stack, properties) && !NBTags.getOrCreate(stack).getBoolean("magazineless"))
            dropMagazine(shooter.getEntityWorld(), stack, shooter, properties);
        return consumed;
    }

    @Override
    public boolean alternateUse(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter.isSneaking() && !NBTags.getOrCreate(stack).getBoolean("magazineless"))
        {
            dropMagazine(shooter.getEntityWorld(), stack, shooter, properties);
            return true;
        }
        return false;
    }

    public void dropMagazine(World world, ItemStack stack,
        EntityLivingBase entity, WeaponProperties properties)
    {
        if (!world.isRemote)
            entity.entityDropItem(magazine.withAmmo(new ItemStack(magazine), getAmmo(stack)), 0.5F);
        removeAmmo(stack, getAmmo(stack));
        stack.setItemDamage(stack.getMaxDamage()); // Empty weapon
        NBTags.getOrCreate(stack).setBoolean("magazineless", true);
        if (unloadEffects != null)
        {
            for (Effect effect : unloadEffects)
                effect.apply(world, entity, stack, properties);
        }
    }

    public MagazineAmmoSource unloadEffects(Effect... unloadEffects)
    {
        this.unloadEffects = unloadEffects;
        return this;
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        return magazine.getAmmoCapacity();
    }
}
