package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.RocketBundle;
import com.domochevsky.quiverbow.projectiles.SmallRocket;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DragonBox extends WeaponBase
{
	private int fireDur;
	private double explosionSize;
	private boolean dmgTerrain;

	public DragonBox()
	{
		super("dragonbox", 64);
		setFiringBehaviour(new SingleShotFiringBehaviour<DragonBox>(this, (world, weaponStack, entity, data) ->
		{ // Random Damage
			int dmg_range = this.damageMax - this.damageMin; // If max dmg is 20 and
														// min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 0
			// and 10
			dmg += this.damageMin; // Adding the min dmg of 10 back on top, giving
								// us
			// the proper damage range (10-20)

			// Firing
			SmallRocket shot = new SmallRocket(world, entity, (float) this.speed, 0, 0);

			shot.damage = dmg;
			shot.fireDuration = this.fireDur;
			shot.explosionSize = this.explosionSize;
			shot.dmgTerrain = this.dmgTerrain;

			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 1.0F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 4)", 4).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 6)", 6).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 1.3 BPT (Blocks Per Tick))", 1.3)
				.getDouble();

		this.knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 2)", 2)
				.getInt();
		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 1)", 1)
				.getInt();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 10 ticks)", 10).getInt();

		this.fireDur = config.get(this.name, "How long is what I hit on fire? (default 6s)", 6).getInt();

		this.explosionSize = config.get(this.name,
				"How big are my explosions? (default 1.0 blocks, for no terrain damage. TNT is 4.0 blocks)", 1.0)
				.getDouble();
		this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true)
				.getBoolean(true);

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.enabled)
		{
			// One dragonbox (empty)
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "zxy", "azy", "zxy", 'x', Items.STICK,
					'y', Items.STRING, 'z', Items.IRON_INGOT, 'a', Items.FLINT_AND_STEEL);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		ItemStack stack = Helper.getAmmoStack(RocketBundle.class, 0);

		GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(stack.getItem(), 8));
	}
}
