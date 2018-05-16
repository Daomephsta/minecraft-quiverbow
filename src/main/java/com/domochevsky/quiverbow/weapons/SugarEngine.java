package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.projectiles.SugarRod;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.BurstFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class SugarEngine extends MagazineFedWeapon
{
	private class GatlingFiringBehaviour extends BurstFiringBehaviour<SugarEngine>
	{
		public GatlingFiringBehaviour()
		{
			super(SugarEngine.this, (world, weaponStack, entity, data) ->
			{
				SugarEngine weapon = (SugarEngine) weaponStack.getItem();
				float spreadHor = world.rand.nextFloat() * weapon.spread - (weapon.spread / 2); // Spread
																								// between
																								// -4
																								// and
																								// 4
																								// at
																								// ((0.0
																								// to
																								// 1.0)
																								// *
																								// 16
																								// -
																								// 8)
				float spreadVert = world.rand.nextFloat() * weapon.spread - (weapon.spread / 2);

				int dmg_range = weapon.damageMax - weapon.damageMin; // If max dmg is
																// 20 and min is
																// 10, then the
																// range will be
																// 10
				int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
																// between 0 and
																// 10
				dmg += weapon.damageMin; // Adding the min dmg of 10 back on top,
										// giving us the proper damage range
										// (10-20)

				SugarRod projectile = new SugarRod(world, entity, (float) weapon.speed, spreadHor, spreadVert);
				projectile.damage = dmg;

				return projectile;
			});
		}

		@Override
		public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
		{
			if (!stack.hasTagCompound())
			{
				stack.setTagCompound(new NBTTagCompound());
			}
			// Weapon is ready, so we can spin up now. set spin-down immunity to
			// x
			// ticks and spin up
			stack.getTagCompound().setInteger("spinDownImmunity", 20); // Can't
																		// spin
																		// down
																		// for
																		// 20
																		// ticks.
																		// Also
																		// indicates
																		// our
																		// desire
																		// to
																		// spin
																		// up

			if (stack.getTagCompound().getInteger("spinning") < weapon.getSpinupTime())
			{
				return;
			} // Not ready yet, so keep spinning up
				// else, we're ready

			weapon.setBurstFire(stack, 4); // Setting the rods left to fire to
											// 4, then
			// going through that via onUpdate (Will be
			// constantly refreshed if we're still
			// spinning)
		}

		protected void doBurstFire(ItemStack weaponStack, World world, EntityLivingBase entity)
		{
			Helper.knockUserBack(entity, weapon.kickback); // Kickback
			if (!world.isRemote)
				world.spawnEntity(projectileFactory.createProjectile(world, weaponStack, entity, null));
			doFireFX(world, entity);
		}
	}

	public SugarEngine(AmmoBase ammo)
	{
		super("sugar_engine", ammo, 200);
		setFiringBehaviour(new GatlingFiringBehaviour());
	}

	public float spread;

	int getSpinupTime()
	{
		return 30;
	} // Time in ticks until we can start firing

	@Override
	public void doFireFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.2F);
		entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.6F, 3.0F);
	}

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) // Overhauled
	// default
	{
		super.onUpdate(stack, world, entity, animTick, holdingItem);
		if (world.isRemote)
		{
			return;
		} // Not doing this on client side

		if (this.getCooldown(stack) > 0)
		{
			this.setCooldown(stack, this.getCooldown(stack) - 1);
		} // Cooling down
		if (this.getCooldown(stack) == 1)
		{
			this.doCooldownSFX(world, entity);
		} // One tick before cooldown is done with, so SFX now

		if (stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		} // Init

		if (stack.getTagCompound().getInteger("spinDownImmunity") == 0) // Not
		// firing
		// and
		// no
		// immunity
		// left,
		// so
		// spinning
		// down
		{
			if (stack.getTagCompound().getInteger("spinning") > 0)
			{
				stack.getTagCompound().setInteger("spinning", stack.getTagCompound().getInteger("spinning") - 1);

				this.doSpinSFX(stack, world, entity);
			}
			// else, not spinning
		}
		else // We're currently immune to spinning down, so decreasing that
				// immunity time until we actually can
		{
			stack.getTagCompound().setInteger("spinDownImmunity",
					stack.getTagCompound().getInteger("spinDownImmunity") - 1);

			// Also assuming that we're trying to fire, so spinning up (This is
			// a workaround for the fact that onRightClick isn't called every
			// tick)
			if (stack.getTagCompound().getInteger("spinning") < this.getSpinupTime())
			{
				stack.getTagCompound().setInteger("spinning", stack.getTagCompound().getInteger("spinning") + 1);
			}
			// else, we've reached full spin

			this.doSpinSFX(stack, world, entity); // Spin down SFX
		}
	}

	private void doSpinSFX(ItemStack stack, World world, Entity player)
	{
		// SFX
		int spin = stack.getTagCompound().getInteger("spinning");
		// Increasing in frequency as we spin up TODO: Clean up with formula
		switch (spin)
		{
		case 1 :
		case 5 :
		case 9 :
		case 13 :
		case 16 :
		case 19 :
		case 21 :
		case 23 :
		case 25 :
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON,
					SoundCategory.PLAYERS, 0.8F, 1.8F);
			break;
		default :
			if (spin >= 27)
				world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON,
						SoundCategory.PLAYERS, 0.8F, 0.4F);
			break;
		}
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 1)", 1).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 3)", 3).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 2.0 BPT (Blocks Per Tick))", 2.0)
				.getDouble();

		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 1)", 1)
				.getInt();
		this.spread = (float) config.get(this.name, "How accurate am I? (default 10 spread)", 10).getDouble();

		this.isMobUsable = config
				.get(this.name, "Can I be used by QuiverMobs? (default true. They'll probably figure it out.)", true)
				.getBoolean(true);
	}
}
