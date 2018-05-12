package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ObsidianMagazine;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.OSPShot;
import com.domochevsky.quiverbow.projectiles.ProjectileBase;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class OSP extends MagazineFedWeapon
{
	public OSP(AmmoBase ammo)
	{
		super("splinter_pistol", ammo, 16);
		setFiringBehaviour(new SingleShotFiringBehaviour<OSP>(this, (world, weaponStack, entity, data) ->
		{
			OSP weapon = (OSP) weaponStack.getItem();
			ProjectileBase projectile = new OSPShot(world, entity, (float) weapon.speed,
					new PotionEffect(MobEffects.WITHER, weapon.Wither_Duration, weapon.Wither_Strength));

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

			projectile.damage = dmg;
			return projectile;
		}));
	}

	public int Wither_Duration; // 20 ticks to a second, let's start with 3
	// seconds
	public int Wither_Strength; // 2 dmg per second for 3 seconds = 6 dmg total

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 0.3F, 0.4F);
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXPLODE, 0.4F, 1.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
				(byte) 1); // smoke
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 4)", 4).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 8)", 8).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 1.7 BPT (Blocks Per Tick))", 1.7)
				.getDouble();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 15 ticks)", 15).getInt();

		this.Wither_Strength = config.get(this.name, "How strong is my Wither effect? (default 1)", 1).getInt();
		this.Wither_Duration = config.get(this.name, "How long does my Wither effect last? (default 61 ticks)", 61)
				.getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.enabled)
		{
			// One Obsidian Splinter
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), " io", "ipi", "oft", 'o',
					Blocks.OBSIDIAN, 'i', Items.IRON_INGOT, 'p', Blocks.PISTON, 'f', Items.FLINT_AND_STEEL, 't',
					Blocks.TRIPWIRE_HOOK);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		// Reloading with obsidian magazine, setting its ammo metadata as ours
		// (Need to be empty for that)
		Helper.registerAmmoRecipe(ObsidianMagazine.class, this);
	}
}
