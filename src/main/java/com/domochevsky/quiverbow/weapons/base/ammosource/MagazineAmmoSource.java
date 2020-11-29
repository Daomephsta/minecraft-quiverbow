package com.domochevsky.quiverbow.weapons.base.ammosource;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon.Effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MagazineAmmoSource extends SimpleAmmoSource
{
    private final Item magazine;
    private Effect[] unloadEffects;

    public MagazineAmmoSource(Item magazine)
    {
        super(new ItemStack(magazine).getMaxDamage());
        this.magazine = magazine;
    }

    @Override
    public boolean consumeAmmo(EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (shooter.isSneaking())
        {
            dropMagazine(shooter.getEntityWorld(), stack, shooter, properties);
            return false;
        }
        return super.consumeAmmo(shooter, stack, properties);
    }

    public void dropMagazine(World world, ItemStack stack,
        EntityLivingBase entity, WeaponProperties properties)
    {
        if (!world.isRemote)
            entity.entityDropItem(new ItemStack(magazine, 1, stack.getItemDamage()), 0.5F);
        stack.setItemDamage(stack.getMaxDamage()); // Empty weapon
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
}
