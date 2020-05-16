package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.Thorn;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.BurstFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ThornSpitter extends MagazineFedWeapon
{
	private class ThornSpitterFiringBehaviour extends BurstFiringBehaviour<ThornSpitter>
	{
		public ThornSpitterFiringBehaviour()
		{
			super(ThornSpitter.this, (world, weaponStack, entity, data, properties) ->
			{
				int dmg_range = properties.getDamageMax() - properties.getDamageMin();
				int dmg = properties.getDamageMin() + world.rand.nextInt(dmg_range + 1);

				Thorn projectile = new Thorn(world, entity, properties.getProjectileSpeed());
				projectile.damage = dmg;
				return projectile;
			});
		}

		@Override
		public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
		{
			weapon.setBurstFire(stack, 4);
		}

		@Override
        protected void doBurstFire(ItemStack weaponStack, World world, EntityLivingBase entity)
		{
			if (weapon.getCooldown(weaponStack) > 0) return;
			Helper.knockUserBack(entity, getKickback()); // Kickback
			if (!world.isRemote)
				world.spawnEntity(projectileFactory.createProjectile(world, weaponStack, entity, null, weapon.getProperties()));
			doFireFX(world, entity);
			weapon.setBurstFire(weaponStack, weapon.getBurstFire(weaponStack) - 1);
			if (weapon.getBurstFire(weaponStack) == 0) weapon.resetCooldown(weaponStack);
		}
	}

	public ThornSpitter(AmmoBase ammo)
	{
		super("thorn_spitter", ammo, 64);
		setFiringBehaviour(new ThornSpitterFiringBehaviour());
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
	{
		if (this.getCooldown(stack) > 0)
		{
			this.setCooldown(stack, this.getCooldown(stack) - 1);
		} // Cooling down
		if (this.getCooldown(stack) == 1)
		{
			this.doCooldownSFX(world, entity);
		} // One tick before cooldown is done with, so SFX now
		super.onUpdate(stack, world, entity, animTick, holdingItem);
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 0.6F);
	}

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 1.3F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(1).maximumDamage(2).projectileSpeed(1.75F).cooldown(10)
				.mobUsable().build();
	}
}