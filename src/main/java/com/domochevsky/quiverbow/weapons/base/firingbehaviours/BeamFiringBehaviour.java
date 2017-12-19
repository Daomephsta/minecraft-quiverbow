package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import java.util.ArrayList;
import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.renderer.RenderBeam;
import com.domochevsky.quiverbow.renderer.RenderBeam.Beam;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BeamFiringBehaviour<W extends _WeaponBase> extends FiringBehaviourBase<W>
{
	public static interface IBeamEffect
	{
		public void apply(ItemStack stack, World world, EntityLivingBase shooter, RayTraceResult target);
	}

	// RGB color encoded as an integer
	private final int beamColour;
	// Applies the effect of the beam
	private final IBeamEffect effect;
	// Maximum range of the beam in blocks
	private final float maxRange;
	// How many entities the beam pierces through
	private int pierceCount = 1;

	public BeamFiringBehaviour(W weapon, IBeamEffect effect, int beamColour, float maxRange)
	{
		super(weapon);
		this.beamColour = beamColour;
		this.effect = effect;
		this.maxRange = maxRange;
	}

	@Override
	public void fire(ItemStack stack, World world, EntityLivingBase shooter, EnumHand hand)
	{
		shooter.setActiveHand(hand);
	}

	@Override
	public void onFiringTick(ItemStack stack, EntityLivingBase shooter, int count)
	{
		World world = shooter.getEntityWorld();
		Vec3d eyeVec = shooter.getPositionVector().addVector(0.0D, shooter.getEyeHeight(), 0.0D);
		Vec3d rayEndVec = eyeVec.add(shooter.getLookVec().scale(maxRange));

		if (count % 10 == 0)
		{
			if (pierceCount > 1) // Piercing
			{
				List<RayTraceResult> results = new ArrayList<>(pierceCount);
				Helper.raytraceAll(results, world, shooter, eyeVec, rayEndVec);
				if (results.isEmpty())
				{
					RenderBeam.updateBeam(eyeVec, rayEndVec);
					return;
				}
				// Sort the list in ascending order of distance from the shooter
				results.sort((resultA, resultB) ->
				{
					double distanceA = resultA.hitVec.distanceTo(shooter.getPositionVector());
					double distanceB = resultB.hitVec.distanceTo(shooter.getPositionVector());
					return Double.compare(distanceA, distanceB);
				});
				// Truncate the list to contain only the results that will be
				// pierced/hit
				results = results.subList(0, Math.min(pierceCount, results.size()));
				for (RayTraceResult result : results)
				{
					effect.apply(stack, world, shooter, result);
				}
				RenderBeam.updateBeam(eyeVec, results.get(results.size() - 1).hitVec);
			}
			else // Non-piercing
			{
				RayTraceResult result = Helper.raytraceClosestObject(world, shooter, eyeVec, rayEndVec);
				if (result != null)
				{
					effect.apply(stack, world, shooter, result);
					RenderBeam.updateBeam(eyeVec, result.hitVec);
				}
				else
				{
					RenderBeam.updateBeam(eyeVec, rayEndVec);
				}
			}
		}
		else RenderBeam.updateBeam(eyeVec, rayEndVec);
	}

	@Override
	public void onStopFiring(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft)
	{
		entityLiving.resetActiveHand();
	}

	public BeamFiringBehaviour<W> setPierceCount(int pierceCount)
	{
		this.pierceCount = pierceCount;
		return this;
	}

	public int getBeamColour()
	{
		return beamColour;
	}
}
