package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.SoulShot;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class SoulCairn extends WeaponBase
{
	public SoulCairn()
	{
		super("soul_cairn", 1);
		this.setCreativeTab(CreativeTabs.TOOLS); // Tool, so on the tool tab
		setFiringBehaviour(new SingleShotFiringBehaviour<SoulCairn>(this,
				(world, weaponStack, entity, data, properties) -> new SoulShot(world, entity, properties.getProjectileSpeed()))
		{
			@Override
			public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
			{
				entity.attackEntityFrom(DamageSource.causeThrownDamage(entity, entity), 2); // A
																							// sacrifice
																							// in
																							// blood
				super.fire(stack, world, entity, hand);
			}
		});
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
		entity.playSound(SoundEvents.BLOCK_NOTE_BASS, 1.0F, 0.4F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().projectileSpeed(3.0F).kickback(4).cooldown(20).build();
	}
}
