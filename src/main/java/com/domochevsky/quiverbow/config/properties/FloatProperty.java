package com.domochevsky.quiverbow.config.properties;

import net.minecraftforge.common.config.Property;

public class FloatProperty extends RangedProperty
{
	private float value;
	private final float minValue, maxValue, defaultValue;

	public FloatProperty(String propertyName, String comment, float defaultValue, float minValue, float maxValue)
	{
		super(propertyName, comment);
		this.defaultValue = this.value = defaultValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public static FloatProperty createIgnored(String name)
	{
		return (FloatProperty) new FloatProperty(name, "", 0.0F, 0.0F, 0.0F).ignore();
	}

	@Override
	public void loadFromConfig(Property configProp)
	{
		value = (float) configProp.getDouble();
	}

	@Override
	public Property createDefaultForgeConfigProperty()
	{
		Property property = new Property(getPropertyName(), Float.toString(defaultValue), Property.Type.DOUBLE)
				.setDefaultValue(defaultValue).setMinValue(minValue).setMaxValue(maxValue);
		property.setComment(this.getComment());
		return property;
	}

	public float getValue()
	{
		return value;
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
