package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.NeedleMagazine;
import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.projectiles.Thorn;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.BurstFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ThornSpitter extends MagazineFedWeapon
{
	private class ThornSpitterFiringBehaviour extends BurstFiringBehaviour<ThornSpitter>
	{
		public ThornSpitterFiringBehaviour()
		{
			super(ThornSpitter.this, (world, weaponStack, entity, data) ->
			{
				ThornSpitter weapon = (ThornSpitter) weaponStack.getItem();
				int dmg_range = weapon.DmgMax - weapon.DmgMin; // If max dmg is
																// 20 and min is
																// 10, then the
																// range will be
																// 10
				int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
																// between 0 and
																// 10
				dmg += weapon.DmgMin; // Adding the min dmg of 10 back on top,
										// giving us the proper damage range
										// (10-20)

				Thorn projectile = new Thorn(world, (EntityLivingBase) entity, (float) weapon.Speed);
				projectile.damage = dmg;
				return projectile;
			});
		}

		@Override
		public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
		{
			weapon.setBurstFire(stack, 4);
		}

		protected void doBurstFire(ItemStack weaponStack, World world, Entity entity)
		{
			if (weapon.getCooldown(weaponStack) > 0) return;
			Helper.knockUserBack(entity, weapon.Kickback); // Kickback
			if (!world.isRemote)
				world.spawnEntity(projectileFactory.createProjectile(world, weaponStack, entity, null));
			doFireFX(world, entity);
			weapon.setBurstFire(weaponStack, weapon.getBurstFire(weaponStack) - 1);
			if (weapon.getBurstFire(weaponStack) == 0) weapon.setCooldown(weaponStack, weapon.Cooldown);
		}
	}

	public ThornSpitter(_AmmoBase ammo)
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
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 1)", 1).getInt();
		this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 2)", 2).getInt();

		this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.75 BPT (Blocks Per Tick))", 1.75)
				.getDouble();

		this.Cooldown = config.get(this.name, "How long until I can fire again? (default 10 ticks)", 10).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (Enabled)
		{
			// One Thorn Spitter (empty)
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "bib", "php", "sts", 't',
					Blocks.TRIPWIRE_HOOK, 'b', Blocks.IRON_BARS, 'i', Items.IRON_INGOT, 'h', Blocks.HOPPER, 's',
					Blocks.STICKY_PISTON, 'p', Blocks.PISTON);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		Helper.registerAmmoRecipe(NeedleMagazine.class, this);
	}
}