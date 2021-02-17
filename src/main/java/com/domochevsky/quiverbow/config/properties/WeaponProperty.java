package com.domochevsky.quiverbow.config.properties;

import net.minecraftforge.common.config.Property;

public abstract class WeaponProperty
{
    private final String propertyName;
    private final String comment;
    private boolean ignore;

    public WeaponProperty(String propertyName, String comment)
    {
        this.propertyName = propertyName;
        this.comment = comment;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public String getComment()
    {
        return comment;
    }

    protected WeaponProperty ignore()
    {
        this.ignore = true;
        return this;
    }

    public boolean shouldIgnore()
    {
        return ignore;
    }

    public abstract void reset();

    public abstract void loadFromConfig(Property configProp);

    public abstract Property createDefaultForgeConfigProperty();

    public abstract String getValueAsString();
}
