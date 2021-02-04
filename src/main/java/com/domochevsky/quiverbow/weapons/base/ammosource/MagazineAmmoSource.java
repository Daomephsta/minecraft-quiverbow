package com.domochevsky.quiverbow.weapons.base.ammosource;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.util.NBTags;
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
            entity.entityDropItem(new ItemStack(magazine, 1, stack.getItemDamage()), 0.5F);
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
}
