package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import com.domochevsky.quiverbow.weapons.base._WeaponBase;

public abstract class FiringBehaviourBase<W extends _WeaponBase> implements IFiringBehaviour
{   
    protected final W weapon;
    
    protected FiringBehaviourBase(W weapon)
    {
	this.weapon = weapon;
    }
}
