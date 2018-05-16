package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.RedLight;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.BeamFiringBehaviour;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.BeamFiringBehaviour.IBeamEffect;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class LightningRed extends MagazineFedWeapon
{
	public LightningRed(AmmoBase ammo)
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
					target.entityHit.hurtResistantTime = 0; // No immunity
															// frames

					// Bonus
					/* EntityLightningBolt bolt = new EntityLightningBolt(world,
					 * target.entityHit.posX, target.entityHit.posY,
					 * target.entityHit.posZ, false); */
					// world.addWeatherEffect(bolt);
					// breakTerrain(world, shooter,
					// target.entityHit.getPosition().down());
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

				if (breakThis) // Sorted out all blocks we don't want to break.
								// Check defaults
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

		Helper.knockUserBack(entity, this.kickback); // Kickback

		// SFX
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_LIGHTNING_THUNDER, 1.0F, 0.5F);
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_BLAST, 2.0F, 0.1F);

		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), EnumParticleTypes.REDSTONE, (byte) 4);

		if (!world.isRemote)
		{
			// Firing
			RedLight shot = new RedLight(world, entity, (float) this.speed);

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

			// The moving end point
			shot.damage = dmg;
			shot.targetsHitMax = this.PassThroughMax; // The maximum number of
			// entities to punch through
			// before ending
			shot.ignoreFrustumCheck = true;
			shot.ticksInAirMax = this.MaxTicks;

			world.spawnEntity(shot); // Firing!
		}

		this.setCooldown(stack, this.cooldown);
		if (this.consumeAmmo(stack, entity, 4))
		{
			this.dropMagazine(world, stack, entity);
		}
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity) // Server side.
																// Only done
	// when held
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 0.2F);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 8)", 8).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 16)", 16).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 5.0 BPT (Blocks Per Tick))", 5.0)
				.getDouble();
		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
				.getInt();
		this.cooldown = config.get(this.name, "How long until I can fire again? (default 40 ticks. That's 2 sec)", 40)
				.getInt();

		this.PassThroughMax = config
				.get(this.name, "Through how many entities and blocks can I punch, tops? (default 5)", 5).getInt();
		this.MaxTicks = config.get(this.name, "How long does my beam exist, tops? (default 60 ticks)", 60).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}
}
