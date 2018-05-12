package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.SeedJar;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.projectiles.Seed;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SeedSweeper extends MagazineFedWeapon
{
	private int damage;
	private float spread;

	public SeedSweeper(AmmoBase ammo)
	{
		super("seed_sweeper", ammo, 512);
		setFiringBehaviour(new SalvoFiringBehaviour<SeedSweeper>(this, 8, (world, weaponStack, entity, data) ->
		{
			float spreadHor = world.rand.nextFloat() * this.spread - (this.spread / 2);
			float spreadVert = world.rand.nextFloat() * this.spread - (this.spread / 2);

			Seed shot = new Seed(world, entity, (float) this.speed, spreadHor, spreadVert);
			shot.damage = this.damage;
			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.6F, 0.9F);
	}

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damage = config.get(this.name, "What damage am I dealing per projectile? (default 1)", 1).getInt();
		this.cooldown = config.get(this.name, "How long until I can fire again? (default 15 ticks)", 15).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 1.6 BPT (Blocks Per Tick))", 1.6)
				.getDouble();
		this.spread = (float) config.get(this.name, "How accurate am I? (default 26 spread)", 26).getDouble();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.enabled)
		{
			// One Seed Sweeper (empty)
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), " i ", "ipi", " it", 'p',
					Blocks.PISTON, 'i', Items.IRON_INGOT, 't', Blocks.TRIPWIRE_HOOK);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		Helper.registerAmmoRecipe(SeedJar.class, this);
	}
}
