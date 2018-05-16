package com.domochevsky.quiverbow.weapons;

import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.ColdIron;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FrostLancer extends WeaponBase
{
	public FrostLancer(AmmoBase ammo)
	{
		super("frost_lancer", 4);
		setFiringBehaviour(new SingleShotFiringBehaviour<FrostLancer>(this, (world, weaponStack, entity, data) ->
		{
			FrostLancer weapon = (FrostLancer) weaponStack.getItem();
			ColdIron projectile = new ColdIron(world, entity, (float) weapon.speed,
					new PotionEffect(MobEffects.SLOWNESS, weapon.slownessDur, weapon.slownessStr),
					new PotionEffect(MobEffects.NAUSEA, weapon.nauseaDur, weapon.nauseaStr));

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

			projectile.knockbackStrength = weapon.knockback;
			return projectile;
		}));
	}

	public int zoomMax;

	public int slownessStr;
	public int slownessDur;

	public int nauseaStr;
	public int nauseaDur;

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
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
		super.addInformation(stack, world, list, flags);
		if (this.getCooldown(stack) > 0)
			list.add(I18n.format(getUnlocalizedName() + ".cooldown",
					this.displayInSec(this.getCooldown(stack))));
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 9)", 9).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 18)", 18).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 3.5 BPT (Blocks Per Tick))", 3.5)
				.getDouble();

		this.knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 3)", 3)
				.getInt();
		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 4)", 4)
				.getInt();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 40 ticks)", 40).getInt();

		this.zoomMax = config.get(this.name, "How far can I zoom in? (default 20. Lower means more zoom)", 20).getInt();

		this.slownessStr = config.get(this.name, "How strong is my Slowness effect? (default 3)", 3).getInt();
		this.slownessDur = config.get(this.name, "How long does my Slowness effect last? (default 120 ticks)", 120)
				.getInt();

		this.nauseaDur = config.get(this.name, "How long does my Nausea effect last? (default 120 ticks)", 120)
				.getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}
}			