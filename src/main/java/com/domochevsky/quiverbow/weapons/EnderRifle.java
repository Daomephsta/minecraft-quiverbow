package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderShot;
import com.domochevsky.quiverbow.weapons.base.*;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EnderRifle extends WeaponBase implements IScopedWeapon
{
	private static final String PROP_BONUS_DAMAGE = "bonusDamage";

	public EnderRifle()
	{
		super("ender_rifle", 8);
		setFiringBehaviour(new SingleShotFiringBehaviour<EnderRifle>(this, (world, weaponStack, entity, data, properties) ->
		{
			EnderShot shot = new EnderShot(world, entity, properties.getProjectileSpeed());
			shot.damage = properties.getDamageMin();
			shot.damageMax = properties.getDamageMin();
			shot.damageIncrease = properties.getFloat(PROP_BONUS_DAMAGE); // Increases damage each tick until the max has been reached
			shot.knockbackStrength = properties.getKnockback();
			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity, EnumParticleTypes.SMOKE_NORMAL,
				(byte) 1);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.7F, 0.2F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity, EnumParticleTypes.SMOKE_NORMAL,
				(byte) 1); // smoke
	}
	
	@Override
	public int getMaxZoom()
	{
		return getProperties().getInt(CommonProperties.PROP_MAX_ZOOM);
	}
	
	@Override
	public boolean shouldZoom(World world, EntityPlayer player, ItemStack stack)
	{
		return player.isSneaking();
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(4).maximumDamage(16).projectileSpeed(3.0F).knockback(1)
				.kickback(3).cooldown(25).mobUsable()
				.floatProperty(PROP_BONUS_DAMAGE,
						"How much extra damage the projectile does for every tick it's in flight", 1.0F)
				.intProperty(CommonProperties.PROP_MAX_ZOOM, CommonProperties.COMMENT_MAX_ZOOM, 30).build();
	}
}
