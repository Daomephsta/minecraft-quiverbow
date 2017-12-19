package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.RocketBundle;
import com.domochevsky.quiverbow.projectiles.SmallRocket;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SalvoFiringBehaviour.SalvoData;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DragonBox_Quad extends _WeaponBase
{
	private int FireDur;
	private double ExplosionSize;
	private boolean dmgTerrain;

	public DragonBox_Quad()
	{
		super("quad_dragonbox", 64);
		setFiringBehaviour(new SalvoFiringBehaviour<DragonBox_Quad>(this, 4, (world, weaponStack, entity, data) ->
		{
			float distanceMod = 5.0f;

			int randNum = world.rand.nextInt(100) + 1; // 1-100

			if (randNum >= 95)
			{
				distanceMod = world.rand.nextInt(40);

				distanceMod -= 20; // Range of -20 to 20
			}

			switch (((SalvoData) data).shotCount)
			{
			case 0 :
				return this.fireRocket(world, entity, 0, 0); // Center 1
			case 1 :
				return this.fireRocket(world, entity, distanceMod, 0); // Right
																		// 2
			case 2 :
				return this.fireRocket(world, entity, -distanceMod, 0);// Left 3
			case 3 :
				return this.fireRocket(world, entity, 0, -distanceMod);// Top 4
			default :
				return null;
			}
		}));
	}

	private SmallRocket fireRocket(World world, Entity entity, float spreadHor, float spreadVert)
	{
		SmallRocket rocket = new SmallRocket(world, entity, (float) this.Speed, spreadHor, spreadVert);

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
		// is 10, then the range will
		// be 10
		int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
		// and 10
		dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
		// the proper damage range (10-20)

		// Properties
		rocket.damage = dmg;
		rocket.fireDuration = this.FireDur;
		rocket.explosionSize = this.ExplosionSize;
		rocket.dmgTerrain = this.dmgTerrain;

		return rocket;
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 1.0F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 4)", 4).getInt();
		this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 6)", 6).getInt();

		this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.3 BPT (Blocks Per Tick))", 1.3)
				.getDouble();

		this.Knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 2)", 2)
				.getInt();
		this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 1)", 1)
				.getInt();

		this.Cooldown = config.get(this.name, "How long until I can fire again? (default 10 ticks)", 10).getInt();

		this.FireDur = config.get(this.name, "How long is what I hit on fire? (default 6s)", 6).getInt();

		this.ExplosionSize = config.get(this.name,
				"How big are my explosions? (default 1.0 blocks, for no terrain damage. TNT is 4.0 blocks)", 1.0)
				.getDouble();
		this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true)
				.getBoolean(true);

		this.isMobUsable = config
				.get(this.name, "Can I be used by QuiverMobs? (default false. A bit too high-power for them.)", false)
				.getBoolean();
	}

	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One QuadBox (empty) An upgrade from the regular Dragonbox (so 3
			// more flint&steel + Pistons for reloading mechanism + more
			// barrels)
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "ddd", "pdp", "sts", 'p',
					Blocks.PISTON, 's', Blocks.STICKY_PISTON, 't', Blocks.TRIPWIRE_HOOK, 'd',
					Helper.getWeaponStackByClass(DragonBox.class, true));
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		ItemStack stack = Helper.getAmmoStack(RocketBundle.class, 0);

		GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(stack.getItem(), 8));
	}
}
