package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.RedstoneMagazine;
import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.RedLight;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.BeamFiringBehaviour;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.BeamFiringBehaviour.IBeamEffect;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class LightningRed extends MagazineFedWeapon
{
	public LightningRed(_AmmoBase ammo)
	{
		super("lightning_red", ammo, 16);
		setFiringBehaviour(new BeamFiringBehaviour<LightningRed>(this, new IBeamEffect()
		{	
			@Override
			public void apply(ItemStack stack, World world, EntityLivingBase shooter, RayTraceResult target)
			{
				if (target.entityHit != null)
				{
					// Damage
					target.entityHit.attackEntityFrom(DamageSource.MAGIC, 9.0F);
					target.entityHit.hurtResistantTime = 0; // No immunity frames

					// Bonus
					/*EntityLightningBolt bolt = new EntityLightningBolt(world, target.entityHit.posX, target.entityHit.posY,
							target.entityHit.posZ, false);*/
					//world.addWeatherEffect(bolt);
					//breakTerrain(world, shooter, target.entityHit.getPosition().down());
				}
				else breakTerrain(world, shooter, target.getBlockPos());	
			}

			private void breakTerrain(World world, EntityLivingBase shooter, BlockPos pos)
			{
				IBlockState toBeBroken = world.getBlockState(pos);
				boolean breakThis = true;
				int harvestLevel = toBeBroken.getBlock().getHarvestLevel(toBeBroken);

				if (harvestLevel > 1 || toBeBroken == Blocks.OBSIDIAN || toBeBroken == Blocks.IRON_BLOCK)
				{
					breakThis = false;
				}

				if (breakThis) // Sorted out all blocks we don't want to break. Check defaults
				{
					Helper.tryBlockBreak(world, shooter, pos, 3); // Very
					// Strong
				}
			}
		}, 0xFFFFFF, 8.0F).setPierceCount(5));
	}

	private int PassThroughMax;
	private int MaxTicks;

	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity) // Server
	// side
	{
		if (this.getCooldown(stack) > 0)
		{
			return;
		} // Hasn't cooled down yet

		Helper.knockUserBack(entity, this.Kickback); // Kickback

		// SFX
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_LIGHTNING_THUNDER, 1.0F, 0.5F);
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_BLAST, 2.0F, 0.1F);

		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.REDSTONE, (byte) 4);

		if(!world.isRemote)
		{
			// Firing
			RedLight shot = new RedLight(world, entity, (float) this.Speed);

			// Random Damage
			int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
			// and 10
			dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
			// the proper damage range (10-20)

			// The moving end point
			shot.damage = dmg;
			shot.targetsHitMax = this.PassThroughMax; // The maximum number of
			// entities to punch through
			// before ending
			shot.ignoreFrustumCheck = true;
			shot.ticksInAirMax = this.MaxTicks;

			world.spawnEntity(shot); // Firing!
		}

		this.setCooldown(stack, this.Cooldown);
		if (this.consumeAmmo(stack, entity, 4))
		{
			this.dropMagazine(world, stack, entity);
		}
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity) // Server side. Only done
	// when held
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 0.2F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 8)", 8).getInt();
		this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 16)", 16).getInt();

		this.Speed = config.get(this.name, "How fast are my projectiles? (default 5.0 BPT (Blocks Per Tick))", 5.0)
				.getDouble();
		this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
				.getInt();
		this.Cooldown = config.get(this.name, "How long until I can fire again? (default 40 ticks. That's 2 sec)", 40)
				.getInt();

		this.PassThroughMax = config
				.get(this.name, "Through how many entities and blocks can I punch, tops? (default 5)", 5).getInt();
		this.MaxTicks = config.get(this.name, "How long does my beam exist, tops? (default 60 ticks)", 60).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Lightning Red (empty)
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "q q", "qiq", "iti", 'q', Items.QUARTZ,
					'i', Items.IRON_INGOT, 't', Blocks.TRIPWIRE_HOOK);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		Helper.registerAmmoRecipe(RedstoneMagazine.class, this);
	}
}
