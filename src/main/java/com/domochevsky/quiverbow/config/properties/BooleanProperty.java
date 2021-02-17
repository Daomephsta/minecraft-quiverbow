package com.domochevsky.quiverbow.config.properties;

import net.minecraftforge.common.config.Property;

public class BooleanProperty extends WeaponProperty
{
    private final boolean defaultValue;
    private boolean value;

    public BooleanProperty(String propertyName, String comment, boolean defaultValue)
    {
        super(propertyName, comment);
        this.defaultValue = this.value = defaultValue;
    }

    public static BooleanProperty createIgnored(String name)
    {
        return (BooleanProperty) new BooleanProperty(name, "", false).ignore();
    }

    @Override
    public void loadFromConfig(Property configProp)
    {
        value = configProp.getBoolean();
    }

    @Override
    public Property createDefaultForgeConfigProperty()
    {
        Property property = new Property(getPropertyName(), Boolean.toString(defaultValue), Property.Type.BOOLEAN)
                .setDefaultValue(defaultValue);
        property.setComment(this.getComment());
        return property;
    }

    public boolean getValue()
    {
        return value;
    }

    @Override
    public String getValueAsString()
    {
        return Boolean.toString(value);
    }

    @Override
    public void reset()
    {
        this.value = defaultValue;
    }
}
