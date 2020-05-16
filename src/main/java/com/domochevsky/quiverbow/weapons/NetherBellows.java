package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.NetherFire;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class NetherBellows extends MagazineFedWeapon
{
	public NetherBellows(AmmoBase ammo)
	{
		super("nether_bellows", ammo, 200);
		setFiringBehaviour(new SalvoFiringBehaviour<NetherBellows>(this, 5, (world, weaponStack, entity, data, properties) ->
		{
			float spreadHor = world.rand.nextFloat() * 20 - 10; // Spread between -10 and 10
			float spreadVert = world.rand.nextFloat() * 20 - 10;

			NetherFire shot = new NetherFire(world, entity, properties.getProjectileSpeed(), spreadHor, spreadVert);
			shot.damage = Helper.randomIntInRange(world.rand, properties.getDamageMin(), properties.getDamageMax());
			shot.fireDuration = properties.getInt(CommonProperties.PROP_FIRE_DUR_ENTITY);

			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 0.3F);
	}

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(1).maximumDamage(1).projectileSpeed(0.75F).mobUsable()
				.intProperty(CommonProperties.PROP_FIRE_DUR_ENTITY, CommonProperties.COMMENT_FIRE_DUR_ENTITY, 3)
				.build();
	}
}
