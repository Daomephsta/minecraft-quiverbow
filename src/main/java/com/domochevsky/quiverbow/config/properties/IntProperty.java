package com.domochevsky.quiverbow.config.properties;

import net.minecraftforge.common.config.Property;

public class IntProperty extends RangedProperty
{
    private int value;
    private final int minValue, maxValue, defaultValue;

    public IntProperty(String propertyName, String comment, int defaultValue, int minValue, int maxValue)
    {
        super(propertyName, comment);
        this.defaultValue = this.value = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public static IntProperty createIgnored(String name)
    {
        return (IntProperty) new IntProperty(name, "", 0, 0, 0).ignore();
    }

    @Override
    public void loadFromConfig(Property configProp)
    {
        value = configProp.getInt();
    }

    @Override
    public Property createDefaultForgeConfigProperty()
    {
        Property property = new Property(getPropertyName(), Integer.toString(defaultValue), Property.Type.INTEGER)
                .setDefaultValue(defaultValue).setMinValue(minValue).setMaxValue(maxValue);
        property.setComment(this.getComment());
        return property;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public String getValueAsString()
    {
        return Integer.toString(value);
    }

    @Override
    public Number getMinimum()
    {
        return minValue;
    }

    @Override
    public Number getMaximum()
    {
        return maxValue;
    }

    @Override
    public void reset()
    {
        this.value = defaultValue;
    }
}
