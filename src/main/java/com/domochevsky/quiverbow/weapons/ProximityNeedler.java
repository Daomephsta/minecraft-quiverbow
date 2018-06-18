package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.ProxyThorn;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class ProximityNeedler extends MagazineFedWeapon
{
	private static final String PROP_PROX_CHECK_INTERVAL = "proxyCheck", PROP_THORN_AMOUNT = "thornAmount",
			PROP_TRIGGER_DISTANCE = "triggerDistance";

	public ProximityNeedler(AmmoBase ammo)
	{
		super("proximity_thorn_thrower", ammo, 64);
		setFiringBehaviour(
				new SingleShotFiringBehaviour<ProximityNeedler>(this, 8, (world, weaponStack, entity, data, properties) ->
				{
					int dmg_range = properties.getDamageMax() - properties.getDamageMin(); // If max dmg is
					// 20 and min is
					// 10, then the
					// range will be
					// 10
					int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
					// between 0 and
					// 10
					dmg += properties.getDamageMin(); // Adding the min dmg of 10 back on top,
					// giving us the proper damage range
					// (10-20)

					ProxyThorn shot = new ProxyThorn(world, entity, properties.getProjectileSpeed());
					shot.damage = dmg;
					shot.ticksInGroundMax = properties.getInt(CommonProperties.PROP_DESPAWN_TIME);
					shot.triggerDistance = properties.getInt(PROP_TRIGGER_DISTANCE);
					shot.proxyDelay = properties.getInt(PROP_PROX_CHECK_INTERVAL);
					shot.thornAmount = properties.getInt(PROP_THORN_AMOUNT);

					return shot;
				}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 0.3F);
	}

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.3F);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_GLASS_BREAK, 0.3F, 0.3F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(1).maximumDamage(2).projectileSpeed(2.0F).kickback(2)
				.cooldown(20)
				.intProperty(CommonProperties.PROP_DESPAWN_TIME, CommonProperties.COMMENT_DESPAWN_TIME, 6000)
				.intProperty(PROP_PROX_CHECK_INTERVAL, "The number of ticks inbetween proximity checks", 20)
				.intProperty(PROP_THORN_AMOUNT, "The number of thorns created when a proximity thorn detonates", 32)
				.floatProperty(PROP_TRIGGER_DISTANCE, "The distance proximity thorns trigger at", 2.0F).build();
	}
}
