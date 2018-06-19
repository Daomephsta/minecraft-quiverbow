package com.domochevsky.quiverbow.weapons;

import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.ColdIron;
import com.domochevsky.quiverbow.weapons.base.*;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FrostLancer extends WeaponBase implements IScopedWeapon
{
	private static final String PROP_NAUSEA_STRENGTH = "nauseaStrength";

	public FrostLancer(AmmoBase ammo)
	{
		super("frost_lancer", 4);
		setFiringBehaviour(new SingleShotFiringBehaviour<FrostLancer>(this, (world, weaponStack, entity, data, properties) ->
		{
			ColdIron projectile = new ColdIron(world, entity, properties.getProjectileSpeed(),
					new PotionEffect(MobEffects.SLOWNESS, properties.getInt(CommonProperties.PROP_SLOWNESS_DUR), properties.getInt(CommonProperties.PROP_SLOWNESS_STRENGTH)),
					new PotionEffect(MobEffects.NAUSEA, properties.getInt(CommonProperties.PROP_NAUSEA_DUR), properties.getInt(PROP_NAUSEA_STRENGTH)));

			projectile.damage = Helper.randomIntInRange(world.rand, getProperties().getDamageMin(), getProperties().getDamageMax());;

			projectile.knockbackStrength = properties.getKnockback();
			return projectile;
		}));
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.7F, 0.2F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity, EnumParticleTypes.SMOKE_NORMAL,
				(byte) 1); // smoke
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXPLODE, 0.8F, 1.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity, EnumParticleTypes.SMOKE_NORMAL,
				(byte) 1); // smoke
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
		super.addInformation(stack, world, list, flags);
		if (this.getCooldown(stack) > 0)
			list.add(I18n.format(getUnlocalizedName() + ".cooldown",
					this.displayInSec(this.getCooldown(stack))));
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
		return WeaponProperties.builder().minimumDamage(9).minimumDamage(18).projectileSpeed(3.5F).knockback(3)
				.kickback(4).cooldown(40).mobUsable()
				.intProperty(CommonProperties.PROP_SLOWNESS_STRENGTH, CommonProperties.COMMENT_SLOWNESS_STRENGTH, 3)
				.intProperty(CommonProperties.PROP_SLOWNESS_DUR, CommonProperties.COMMENT_SLOWNESS_DUR, 120)
				.intProperty(CommonProperties.PROP_NAUSEA_DUR, CommonProperties.COMMENT_NAUSEA_DUR, 120)
				.intProperty(PROP_NAUSEA_STRENGTH, "The strength of the Nausea effect applied", 120)
				.intProperty(CommonProperties.PROP_MAX_ZOOM, CommonProperties.COMMENT_MAX_ZOOM, 20).build();
	}
}			