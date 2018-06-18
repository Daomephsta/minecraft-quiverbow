package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.WebShot;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class SilkenSpinner extends WeaponBase
{
	public SilkenSpinner()
	{
		super("silken_spinner", 8);
		this.setCreativeTab(CreativeTabs.TOOLS); // This is a tool
		setFiringBehaviour(new SingleShotFiringBehaviour<SilkenSpinner>(this,
				(world, weaponStack, entity, data, properties) -> new WebShot(world, entity,
						properties.getProjectileSpeed())));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().projectileSpeed(1.5F).cooldown(20).build();
	}
}
