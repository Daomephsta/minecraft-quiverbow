package com.domochevsky.quiverbow.weapons;

import java.util.Collections;
import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ColdIronClip;
import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.ColdIron;
import com.domochevsky.quiverbow.util.Newliner;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FrostLancer extends _WeaponBase
{
	public FrostLancer(_AmmoBase ammo)
	{
		super("frost_lancer", 4);
		setFiringBehaviour(new SingleShotFiringBehaviour<FrostLancer>(this, (world, weaponStack, entity, data) ->
		{
			FrostLancer weapon = (FrostLancer) weaponStack.getItem();
			ColdIron projectile = new ColdIron(world, entity, (float) weapon.Speed,
					new PotionEffect(MobEffects.SLOWNESS, weapon.Slowness_Dur, weapon.Slowness_Str),
					new PotionEffect(MobEffects.NAUSEA, weapon.Nausea_Dur, weapon.Nausea_Str));

			// Random Damage
			int dmg_range = weapon.DmgMax - weapon.DmgMin; // If max dmg is 20
															// and min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 0
			// and 10
			dmg += weapon.DmgMin; // Adding the min dmg of 10 back on top,
									// giving us
			// the proper damage range (10-20)

			projectile.damage = dmg;

			projectile.knockbackStrength = weapon.Knockback;
			return projectile;
		}));
	}

	public int ZoomMax;

	public int Slowness_Str;
	public int Slowness_Dur;

	public int Nausea_Str;
	public int Nausea_Dur;

	@Override
	protected void doCooldownSFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.7F, 0.2F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
				(byte) 1); // smoke
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXPLODE, 0.8F, 1.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
				(byte) 1); // smoke
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);
		if (this.getCooldown(stack) > 0)
			Collections.addAll(list, Newliner.translateAndParse(getUnlocalizedName() + ".cooldown",
					this.displayInSec(this.getCooldown(stack))));
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 9)", 9).getInt();
		this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 18)", 18).getInt();

		this.Speed = config.get(this.name, "How fast are my projectiles? (default 3.5 BPT (Blocks Per Tick))", 3.5)
				.getDouble();

		this.Knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 3)", 3)
				.getInt();
		this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 4)", 4)
				.getInt();

		this.Cooldown = config.get(this.name, "How long until I can fire again? (default 40 ticks)", 40).getInt();

		this.ZoomMax = config.get(this.name, "How far can I zoom in? (default 20. Lower means more zoom)", 20).getInt();

		this.Slowness_Str = config.get(this.name, "How strong is my Slowness effect? (default 3)", 3).getInt();
		this.Slowness_Dur = config.get(this.name, "How long does my Slowness effect last? (default 120 ticks)", 120)
				.getInt();

		this.Nausea_Dur = config.get(this.name, "How long does my Nausea effect last? (default 120 ticks)", 120)
				.getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// Upgrade of the EnderRifle

			// One Frost Lancer (empty)
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "qiq", "prs", " o ", 'o',
					Blocks.OBSIDIAN, 'q', Items.QUARTZ, 'i', Items.IRON_INGOT, 'p', Blocks.PISTON, 's',
					Blocks.STICKY_PISTON, 'r', Helper.getWeaponStackByClass(EnderRifle.class, true) // One
			// empty
			// Ender
			// Rifle
			);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		// Reloading with one Frost Clip
		GameRegistry.addShapelessRecipe(new ItemStack(this), Helper.createEmptyWeaponOrAmmoStack(this, 1),
				Helper.getAmmoStack(ColdIronClip.class, 0));
	}
}
