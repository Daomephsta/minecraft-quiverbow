package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderShot;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EnderRifle extends WeaponBase
{
	public int zoomMax;
	private double damageIncrease;

	public EnderRifle()
	{
		super("ender_rifle", 8);
		setFiringBehaviour(new SingleShotFiringBehaviour<EnderRifle>(this, (world, weaponStack, entity, data) ->
		{
			EnderShot shot = new EnderShot(world, entity, (float) this.speed);
			shot.damage = this.damageMin;
			shot.damageMax = this.damageMax;
			shot.damageIncrease = this.damageIncrease; // Increases damage each
														// tick until the max
														// has been reached
			shot.knockbackStrength = this.knockback;
			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
				(byte) 1);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.7F, 0.2F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
				(byte) 1); // smoke
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 4)", 4).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 16)", 16).getInt();

		this.damageIncrease = config.get(this.name,
				"By what amount does my damage rise? (default 1.0, for +1.0 DMG per tick of flight)", 1.0).getDouble();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 3.0 BPT (Blocks Per Tick))", 3.0)
				.getDouble();

		this.knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 1)", 1)
				.getInt();
		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
				.getInt();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 25 ticks)", 25).getInt();

		this.zoomMax = (config.get(this.name, "How far can I zoom in? (default 30. Less means more zoom)", 30)
				.getInt());

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.enabled)
		{
			// One ender rifle (empty)
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "aza", "bcy", "xzx", 'x',
					Blocks.OBSIDIAN, 'y', Blocks.TRIPWIRE_HOOK, 'z', Items.IRON_INGOT, 'a', Items.ENDER_EYE, 'b',
					Blocks.PISTON, 'c', Blocks.STICKY_PISTON);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(Items.IRON_INGOT, 1));
	}
}
