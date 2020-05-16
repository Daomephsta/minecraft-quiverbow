package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.RedSpray;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class RedSprayer extends MagazineFedWeapon
{
	private static final String PROP_BLINDNESS_DUR = "blindnessDur";

	public RedSprayer(AmmoBase ammo)
	{
		super("redstone_sprayer", ammo, 200);
		setFiringBehaviour(new SalvoFiringBehaviour<RedSprayer>(this, 5, (world, waeponStack, entity, data, properties) ->
		{
			// Spread
			float spreadHor = world.rand.nextFloat() * 20 - 10; // Spread between -10 and 10
			float spreadVert = world.rand.nextFloat() * 20 - 10;

			RedSpray shot = new RedSpray(entity.world, entity, properties.getProjectileSpeed(), spreadHor, spreadVert,
					new PotionEffect(MobEffects.WITHER, properties.getInt(CommonProperties.PROP_WITHER_DUR), properties.getInt(CommonProperties.PROP_WITHER_STRENGTH)),
					new PotionEffect(MobEffects.BLINDNESS, properties.getInt(PROP_BLINDNESS_DUR), 1));
			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.7F, 1.5F);
	}

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().projectileSpeed(0.5F).mobUsable()
				.intProperty(CommonProperties.PROP_WITHER_STRENGTH, CommonProperties.COMMENT_WITHER_STRENGTH, 2)
				.intProperty(CommonProperties.PROP_WITHER_DUR, CommonProperties.COMMENT_WITHER_DUR, 20)
				.intProperty(PROP_BLINDNESS_DUR, "The duration in ticks of the Blindness effect applied", 20).build();
	}
}
