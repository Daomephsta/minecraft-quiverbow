package com.domochevsky.quiverbow.weapons;

import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.OWRShot;
import com.domochevsky.quiverbow.projectiles.ProjectileBase;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OWR extends MagazineFedWeapon
{
	private static final String PROP_MAX_MAGIC_DAMAGE = "maxDamageMagic", PROP_MIN_MAGIC_DAMAGE = "minDamageMagic";

	public OWR(AmmoBase ammo)
	{
		super("wither_rifle", ammo, 16);
		setFiringBehaviour(new SingleShotFiringBehaviour<OWR>(this, (world, weaponStack, entity, data, properties) ->
		{
			ProjectileBase projectile = new OWRShot(world, entity, properties.getProjectileSpeed(),
					new PotionEffect(MobEffects.WITHER, properties.getInt(CommonProperties.PROP_WITHER_DUR), properties.getInt(CommonProperties.PROP_WITHER_STRENGTH)));

			// Random Damage
			int dmg_range = properties.getDamageMax() - properties.getDamageMin();
			int dmg = properties.getDamageMin() + world.rand.nextInt(dmg_range + 1);

			projectile.damage = dmg;

			// Random Magic Damage
			dmg_range = properties.getInt(PROP_MAX_MAGIC_DAMAGE) - properties.getInt(PROP_MIN_MAGIC_DAMAGE);
			dmg = properties.getDamageMin() + world.rand.nextInt(dmg_range + 1);

			((OWRShot) projectile).damageMagic = dmg;
			return projectile;
		}));
	}

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity)
	{
		NetHelper.sendParticleMessageToAllPlayers(world, entity, EnumParticleTypes.SMOKE_LARGE, (byte) 4);
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.2F);
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXPLODE, 0.5F, 1.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity, EnumParticleTypes.SPELL_INSTANT,
				(byte) 4);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
		super.addInformation(stack, world, list, flags);
		if (this.getCooldown(stack) > 0)
			list.add(I18n.format(getUnlocalizedName() + ".cooldown", this.displayInSec(this.getCooldown(stack))));
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(7).maximumDamage(13).projectileSpeed(3.0F).knockback(2)
				.kickback(6).cooldown(60)
				.intProperty(PROP_MIN_MAGIC_DAMAGE, "The minimum magic damage this weapon does", 6)
				.intProperty(PROP_MAX_MAGIC_DAMAGE, "The maximum magic damage this weapon does", 14)
				.intProperty(CommonProperties.PROP_WITHER_STRENGTH, CommonProperties.COMMENT_WITHER_STRENGTH, 3)
				.intProperty(CommonProperties.PROP_WITHER_DUR, CommonProperties.COMMENT_WITHER_DUR, 61).build();
	}
}
