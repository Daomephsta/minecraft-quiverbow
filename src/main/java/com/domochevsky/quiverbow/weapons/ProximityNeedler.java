package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.NeedleMagazine;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.projectiles.ProxyThorn;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ProximityNeedler extends MagazineFedWeapon
{
	private int maxTicks;
	private int proxyCheck;
	private int thornAmount;
	private double triggerDist;

	public ProximityNeedler(AmmoBase ammo)
	{
		super("proximity_thorn_thrower", ammo, 64);
		setFiringBehaviour(
				new SingleShotFiringBehaviour<ProximityNeedler>(this, 8, (world, weaponStack, entity, data) ->
				{
					int dmg_range = this.damageMax - this.damageMin; // If max dmg is
																// 20 and min
					// is 10, then the range will
					// be 10
					int dmg = world.rand.nextInt(dmg_range + 1); // Range will
																	// be
																	// between 1
					// and 10 (inclusive both)
					dmg += this.damageMin; // Adding the min dmg of 10 back on top,
										// giving us
					// the proper damage range (10-20)

					ProxyThorn shot = new ProxyThorn(world, entity, (float) this.speed);
					shot.damage = dmg;
					shot.ticksInGroundMax = this.maxTicks;
					shot.triggerDistance = this.triggerDist; // Distance in
																// blocks

					shot.proxyDelay = this.proxyCheck;
					shot.thornAmount = this.thornAmount;

					return shot;
				}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 0.3F);
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
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing per thorn, at least? (default 1)", 1).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing per thorn, tops? (default 2)", 2).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 2.0 BPT (Blocks Per Tick))", 2.0)
				.getDouble();
		this.maxTicks = config.get(this.name,
				"How long do my projectiles stick around, tops? (default 6000 ticks. That's 5 min.)", 6000).getInt();

		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 2)", 2)
				.getInt();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 20 ticks)", 20).getInt();
		this.proxyCheck = config.get(this.name,
				"How long does my projectile wait inbetween each proximity check? (default 20 ticks)", 20).getInt();
		this.thornAmount = config.get(this.name, "How many thorns does my projectile burst into? (default 32)", 32)
				.getInt();
		this.triggerDist = config
				.get(this.name, "What is the trigger distance of my projectiles? (default 2.0 blocks)", 2.0)
				.getDouble();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false)", false).getBoolean();
	}

	@Override
	public void addRecipes()
	{
		if (this.enabled)
		{
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "ihi", "bpb", "tsi", 't',
					Blocks.TRIPWIRE_HOOK, 'b', Blocks.IRON_BARS, 'i', Items.IRON_INGOT, 'h', Blocks.HOPPER, 's',
					Blocks.STICKY_PISTON, 'p', Blocks.PISTON);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		// Ammo
		Helper.registerAmmoRecipe(NeedleMagazine.class, this);
	}
}
