package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.Seed;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class SeedSweeper extends MagazineFedWeapon
{
	public SeedSweeper(AmmoBase ammo)
	{
		super("seed_sweeper", ammo, 512);
		setFiringBehaviour(new SalvoFiringBehaviour<SeedSweeper>(this, 8, (world, weaponStack, entity, data, properties) ->
		{ 
			float spread = getProperties().getFloat(CommonProperties.PROP_SPREAD);
			float spreadHor = world.rand.nextFloat() * spread - (spread / 2.0F);
			float spreadVert = world.rand.nextFloat() * spread - (spread / 2.0F);

			Seed shot = new Seed(world, entity, properties.getProjectileSpeed(), spreadHor, spreadVert);
			shot.damage = Helper.randomIntInRange(world.rand, properties.getDamageMin(), properties.getDamageMax());
			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.6F, 0.9F);
	}

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(1).maximumDamage(1).projectileSpeed(1.6F).mobUsable()
				.floatProperty(CommonProperties.PROP_SPREAD, CommonProperties.COMMENT_SPREAD, 26.0F).build();
	}
}
