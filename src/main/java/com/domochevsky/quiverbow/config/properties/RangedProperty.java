package com.domochevsky.quiverbow.config.properties;

public abstract class RangedProperty extends WeaponProperty
{
    public RangedProperty(String propertyName, String comment)
    {
        super(propertyName, comment);
    }

    public abstract Number getMinimum();
    
    public abstract Number getMaximum();
}
