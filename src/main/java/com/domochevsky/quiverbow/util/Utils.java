package com.domochevsky.quiverbow.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class Utils
{
    public static void playSoundAtEntityPos(Entity entity, SoundEvent sound, float volume, float pitch)
    {
	entity.world.playSound(entity.posX, entity.posY, entity.posZ, sound, entity.getSoundCategory(), volume, pitch,
		false);
    }

    private static final ItemStack ARROW_STACK = new ItemStack(Items.ARROW);

    public static EntityArrow createArrow(World world, EntityLivingBase shooter)
    {
	return ((ItemArrow) Items.ARROW).createArrow(world, ARROW_STACK, shooter);
    }
}
