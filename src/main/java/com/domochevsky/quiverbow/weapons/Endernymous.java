package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.EnderQuartzClip;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderAno;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
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

public class Endernymous extends MagazineFedWeapon
{
	private int maxTicks;

	public Endernymous(AmmoBase ammo)
	{
		super("hidden_ender_pistol", ammo, 8);
		setFiringBehaviour(new SingleShotFiringBehaviour<Endernymous>(this, (world, weaponStack, entity, data) ->
		{
			// Random Damage
			int dmg_range = this.damageMax - this.damageMin; // If max dmg is 20 and
														// min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 1
			// and 10 (inclusive both)
			dmg += this.damageMin; // Adding the min dmg of 10 back on top, giving
								// us
			// the proper damage range (10-20)

			EnderAno shot = new EnderAno(world, entity, (float) this.speed);
			shot.damage = dmg;
			shot.ticksInAirMax = this.maxTicks;
			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, 1.4F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.PORTAL, (byte) 4);
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

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 16)", 16).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 24)", 24).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 5.0 BPT (Blocks Per Tick))", 5.0)
				.getDouble();
		this.maxTicks = config.get(this.name, "How long does my projectile exist, tops? (default 40 ticks)", 40)
				.getInt();

		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 1)", 1)
				.getInt();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 20 ticks)", 20).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false)", false)
				.getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.enabled)
		{
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "e e", "ofo", "oto", 'o',
					Blocks.OBSIDIAN, 'e', Blocks.END_STONE, 't', Blocks.TRIPWIRE_HOOK, 'f', Items.FLINT_AND_STEEL);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		// Ammo
		Helper.registerAmmoRecipe(EnderQuartzClip.class, this);
	}
}
