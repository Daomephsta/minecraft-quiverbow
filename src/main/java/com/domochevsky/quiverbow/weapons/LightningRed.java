package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.config.WeaponProperties;
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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class LightningRed extends MagazineFedWeapon
{
	private static final String PROP_PIERCING = "piercing";

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

	@Override
	public boolean doSingleFire(World world, EntityLivingBase entity, ItemStack stack, EnumHand hand) // Server
	// side
	{
		boolean superResult = super.doSingleFire(world, entity, stack, hand);
		if (this.getCooldown(stack) > 0)
			return false;

		Helper.knockUserBack(entity, getKickback()); // Kickback

		// SFX
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_LIGHTNING_THUNDER, 1.0F, 0.5F);
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_FIREWORK_BLAST, 2.0F, 0.1F);

		NetHelper.sendParticleMessageToAllPlayers(world, entity, EnumParticleTypes.REDSTONE, (byte) 4);

		if (!world.isRemote)
		{
			// Firing
			RedLight shot = new RedLight(world, entity, getProjectileSpeed());
			// The moving end point
			shot.damage = Helper.randomIntInRange(world.rand, getProperties().getDamageMin(), getProperties().getDamageMax());
			shot.targetsHitMax = getProperties().getInt(PROP_PIERCING); // The maximum number of
			// entities to punch through
			// before ending
			shot.ignoreFrustumCheck = true;

			world.spawnEntity(shot); // Firing!
		}

		this.resetCooldown(stack);
		if (this.consumeAmmo(stack, entity, 4))
		{
			this.dropMagazine(world, stack, entity);
		}
		return superResult;
	}

	@Override
	protected void doCooldownSFX(World world, Entity entity) // Server side.
																// Only done
	// when held
	{
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 0.2F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(8).maximumDamage(16).projectileSpeed(5.0F).kickback(3)
				.cooldown(40).mobUsable()
				.intProperty(PROP_PIERCING, "How many entities and blocks the beam can pierce through", 5).build();
	}
}
