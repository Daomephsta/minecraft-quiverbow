package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.SabotArrow;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MortarArrow extends WeaponBase
{
	public MortarArrow()
	{
		super("arrow_mortar", 8);
		setFiringBehaviour(new SingleShotFiringBehaviour<MortarArrow>(this, (world, weaponStack, entity, data) ->
		{
			// Random Damage
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
			SabotArrow projectile = new SabotArrow(world, entity, (float) this.speed);
			projectile.damage = dmg;

			return projectile;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.SMOKE_LARGE, (byte) 1);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity) // Server side
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 2.0F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage are my arrows dealing, at least? (default 2)", 2).getInt();
		this.damageMax = config.get(this.name, "What damage are my arrows dealing, tops? (default 10)", 10).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5)
				.getDouble();
		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
				.getInt();

		this.cooldown = config.get(this.name, "How long until I can fire again? (default 20 ticks)", 20).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.enabled)
		{
			// One Arrow Mortar (Empty)
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "ipi", "isr", "tsr", 't',
					Blocks.TRIPWIRE_HOOK, 'i', Items.IRON_INGOT, 's', Blocks.STICKY_PISTON, 'p', Blocks.PISTON, 'r',
					Items.REPEATER);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		ItemStack ammo = Helper.getAmmoStack(ArrowBundle.class, 0);
		GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(ammo.getItem(), 1));
	}
}
