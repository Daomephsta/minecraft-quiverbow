package com.domochevsky.quiverbow.weapons.base.effects;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon.Effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class PlaySound implements Effect
{
    private final SoundEvent sound;
    private final float volume;
    private final float pitch;

    public PlaySound(SoundEvent sound, float volume, float pitch)
    {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }
    @Override
    public void apply(World world, EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        Helper.playSoundAtEntityPos(shooter, sound, volume, pitch);
    }
}
