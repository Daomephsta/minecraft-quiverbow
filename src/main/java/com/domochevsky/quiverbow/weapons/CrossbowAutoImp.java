package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.weapons.base.WeaponCrossbow;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CrossbowAutoImp extends WeaponCrossbow
{
	public CrossbowAutoImp()
	{
		super("auto_crossbow_imp", 16, (world, weaponStack, entity, data) ->
		{
			CrossbowAutoImp weapon = (CrossbowAutoImp) weaponStack.getItem();
			EntityArrow entityarrow = Helper.createArrow(world, entity);

			// Random Damage
			int dmg_range = weapon.damageMax - weapon.damageMin; // If max dmg is 20
															// and min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 0
			// and 10
			dmg += weapon.damageMin; // Adding the min dmg of 10 back on top,
									// giving us
			// the proper damage range (10-20)

			entityarrow.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0F, (float)weapon.speed, 0.5F);
			entityarrow.setDamage(dmg);
			entityarrow.setKnockbackStrength(weapon.knockback);

			return entityarrow;
		});
	} // 2 bundles

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 10)", 10).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 16)", 16).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5)
				.getDouble();
		this.knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 1)", 1)
				.getInt();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 8 ticks)", 8).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false.)", false)
				.getBoolean(true);
	}
}
