package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.projectiles.WebShot;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class SilkenSpinner extends WeaponBase
{
	public SilkenSpinner()
	{
		super("silken_spinner", 8);
		this.setCreativeTab(CreativeTabs.TOOLS); // This is a tool
		setFiringBehaviour(new SingleShotFiringBehaviour<SilkenSpinner>(this,
				(world, weaponStack, entity, data) -> new WebShot(world, entity,
						(float) ((WeaponBase) weaponStack.getItem()).speed)));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5)
				.getDouble();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 20 ticks)", 20).getInt();

		this.isMobUsable = config.get(this.name,
				"Can I be used by QuiverMobs? (default false. Potentially abusable for free cobwebs.)", false)
				.getBoolean(true);
	}
}
