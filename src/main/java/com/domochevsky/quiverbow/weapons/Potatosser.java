package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Quiverbow;
import com.domochevsky.quiverbow.projectiles.PotatoShot;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Potatosser extends WeaponBase
{
	private boolean shouldDrop;

	public Potatosser()
	{
		super("potatosser", 14);
		setFiringBehaviour(new SingleShotFiringBehaviour<Potatosser>(this, (world, weaponStack, entity, data) ->
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
			PotatoShot shot = new PotatoShot(world, entity, (float) this.speed);
			shot.damage = dmg;
			shot.setDrop(this.shouldDrop);
			return shot;
		}));
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ITEM_BREAK, 0.7F, 0.4F);
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity) // Server side
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.3F, 3.0F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 2)", 2).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 5)", 5).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5)
				.getDouble();
		this.cooldown = config.get(this.name, "How long until I can fire again? (default 15)", 15).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);

		this.shouldDrop = config.get(this.name, "Do I drop naked potatoes on misses? (default true)", true)
				.getBoolean(true);
	}
}
