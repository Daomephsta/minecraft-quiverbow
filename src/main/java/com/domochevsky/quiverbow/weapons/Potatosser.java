package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.PotatoShot;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class Potatosser extends WeaponBase
{
	public Potatosser()
	{
		super("potatosser", 14);
		setFiringBehaviour(new SingleShotFiringBehaviour<Potatosser>(this, (world, weaponStack, entity, data, properties) ->
		{
			// Random Damage
			int dmg_range = properties.getDamageMax() - properties.getDamageMin(); // If max dmg is 20 and
														// min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 0
			// and 10
			dmg += properties.getDamageMin(); // Adding the min dmg of 10 back on top, giving
								// us
			// the proper damage range (10-20)

			// Firing
			PotatoShot shot = new PotatoShot(world, entity, properties.getProjectileSpeed());
			shot.damage = dmg;
			shot.setDrop(properties.getBoolean(CommonProperties.PROP_SHOULD_DROP));
			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 0.7F, 0.4F);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity) // Server side
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.3F, 3.0F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(2).maximumDamage(5).projectileSpeed(1.5F).cooldown(15)
				.mobUsable()
				.booleanProperty(CommonProperties.PROP_SHOULD_DROP, CommonProperties.COMMENT_SHOULD_DROP, true).build();
	}
}
