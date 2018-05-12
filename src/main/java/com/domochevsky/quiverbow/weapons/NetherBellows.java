package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.LargeNetherrackMagazine;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.projectiles.NetherFire;
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

public class NetherBellows extends MagazineFedWeapon
{
	private int damage;
	private int fireDuration;

	public NetherBellows(AmmoBase ammo)
	{
		super("nether_bellows", ammo, 200);
		setFiringBehaviour(new SalvoFiringBehaviour<NetherBellows>(this, 5, (world, weaponStack, entity, data) ->
		{
			float spreadHor = world.rand.nextFloat() * 20 - 10; // Spread
																// between
			// -10 and 10
			float spreadVert = world.rand.nextFloat() * 20 - 10;

			NetherFire shot = new NetherFire(world, entity, (float) this.speed, spreadHor, spreadVert);
			shot.damage = this.damage;
			shot.fireDuration = this.fireDuration;

			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 0.3F);
	}

	@Override
	protected void doUnloadFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);
		this.speed = config.get(this.name, "How fast are my projectiles? (default 0.75 BPT (Blocks Per Tick))", 0.75)
				.getDouble();
		this.damage = config.get(this.name, "What damage am I dealing per projectile? (default 1)", 1).getInt();
		this.fireDuration = config.get(this.name, "For how long do I set things on fire? (default 3 sec)", 3).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.enabled)
		{
			// One redstone sprayer (empty)
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "zxz", "zbz", "cya", 'x',
					Blocks.PISTON, 'y', Blocks.TRIPWIRE_HOOK, 'z', Blocks.OBSIDIAN, 'a', Items.REPEATER, 'b',
					Blocks.STICKY_PISTON, 'c', Items.FLINT_AND_STEEL);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		Helper.registerAmmoRecipe(LargeNetherrackMagazine.class, this);
	}
}
