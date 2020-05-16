package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderAno;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class Endernymous extends MagazineFedWeapon
{
	public Endernymous(AmmoBase ammo)
	{
		super("hidden_ender_pistol", ammo, 8);
		setFiringBehaviour(new SingleShotFiringBehaviour<Endernymous>(this, (world, weaponStack, entity, data, properties) ->
		{
			int dmg_range = properties.getDamageMax() - properties.getDamageMin();
			int dmg = properties.getDamageMin() + world.rand.nextInt(dmg_range + 1);

			EnderAno shot = new EnderAno(world, entity, properties.getProjectileSpeed());
			shot.damage = dmg;
			shot.ticksInAirMax = properties.getInt(CommonProperties.PROP_DESPAWN_TIME);
			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, 1.4F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity, EnumParticleTypes.PORTAL, (byte) 4);
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
		return WeaponProperties.builder().minimumDamage(16).maximumDamage(24).projectileSpeed(5.0F).kickback(1)
				.cooldown(20).intProperty(CommonProperties.PROP_DESPAWN_TIME, CommonProperties.COMMENT_DESPAWN_TIME, 40)
				.build();
	}
}
