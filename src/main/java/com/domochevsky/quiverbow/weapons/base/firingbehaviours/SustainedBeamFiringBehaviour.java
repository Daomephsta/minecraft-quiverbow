//package com.domochevsky.quiverbow.weapons.base.firingbehaviours;
//
//import com.domochevsky.quiverbow.weapons.base.WeaponBase;
//
//public class SustainedBeamFiringBehaviour<W extends WeaponBase> extends BeamFiringBehaviour<W>
//{
//	// The maximum number of ticks the beam should fire for. -1 results in
//	// infinite firing time.
//	private int sustainTime;
//
//	public SustainedBeamFiringBehaviour(W weapon, IBeamEffect effect, int beamColour, float maxRange)
//	{
//		super(weapon, effect, beamColour, maxRange);
//	}
//
//	public SustainedBeamFiringBehaviour<W> setSustainTime(int sustainTime)
//	{
//		this.sustainTime = sustainTime;
//		return this;
//	}
//}
