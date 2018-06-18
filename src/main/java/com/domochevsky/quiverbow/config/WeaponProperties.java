package com.domochevsky.quiverbow.config;

import java.util.HashMap;
import java.util.Map;

import com.domochevsky.quiverbow.config.properties.*;

import net.minecraftforge.common.config.ConfigCategory;

public class WeaponProperties
{
	private BooleanProperty enabled = new BooleanProperty("enabled", "Enables crafting this weapon if true", true);
	private IntProperty damageMin, damageMax;
	private FloatProperty projectileSpeed;
	private IntProperty knockback;
	private IntProperty kickback;
	private IntProperty cooldown;
	private BooleanProperty isMobUsable;
	private Map<String, WeaponProperty> usedProperties;

	public WeaponProperties(Builder builder)
	{
		this.damageMin = builder.damageMin;
		this.damageMax = builder.damageMax;
		this.projectileSpeed = builder.projectileSpeed;
		this.knockback = builder.knockback;
		this.kickback = builder.kickback;
		this.cooldown = builder.cooldown;
		this.isMobUsable = builder.isMobUsable;
		this.usedProperties = builder.usedProperties;
		usedProperties.put(enabled.getPropertyName(), enabled);
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public void loadFromConfig(ConfigCategory configCategory)
	{
		for (WeaponProperty property : usedProperties.values())
		{
			if (configCategory.containsKey(property.getPropertyName()))
				property.loadFromConfig(configCategory.get(property.getPropertyName()));
			else
				configCategory.put(property.getPropertyName(), property.createDefaultForgeConfigProperty());
		}
	}

	public boolean isEnabled()
	{
		return enabled.getValue();
	}

	public int getDamageMin()
	{
		return damageMin.getValue();
	}

	public int getDamageMax()
	{
		return damageMax.getValue();
	}

	public float getProjectileSpeed()
	{
		return projectileSpeed.getValue();
	}

	public int getKnockback()
	{
		return knockback.getValue();
	}

	public int getKickback()
	{
		return kickback.getValue();
	}

	public int getMaxCooldown()
	{
		return cooldown.getValue();
	}

	public boolean isMobUsable()
	{
		return isMobUsable.getValue();
	}
	
	public boolean has(String name)
	{
		return usedProperties.containsKey(name);
	}

	public boolean getBoolean(String name)
	{
		return getPropertyAndCheck(name, BooleanProperty.class).getValue();
	}

	public int getInt(String name)
	{
		return getPropertyAndCheck(name, IntProperty.class).getValue();
	}

	public float getFloat(String name)
	{
		return getPropertyAndCheck(name, FloatProperty.class).getValue();
	}

	@SuppressWarnings("unchecked")
	private <T extends WeaponProperty> T getPropertyAndCheck(String name, Class<T> expectedClass)
	{
		WeaponProperty property = usedProperties.get(name);
		if (property == null) throw new IllegalArgumentException("No property named " + name + " exists");
		if (!expectedClass.isInstance(property))
			throw new IllegalArgumentException(String.format("Expected %s named %s, not %s",
					expectedClass.getSimpleName(), name, property.getClass().getSimpleName()));
		return (T) property;
	}

	public static class Builder
	{
		private IntProperty damageMin, damageMax;
		private FloatProperty projectileSpeed;
		private IntProperty knockback;
		private IntProperty kickback;
		private IntProperty cooldown;
		private BooleanProperty isMobUsable = new BooleanProperty("isMobUsable",
				"QuiverMobs can spawn with this weapon if true", false);
		private Map<String, WeaponProperty> usedProperties = new HashMap<>();

		private Builder()
		{
			//This property is always used
			usedProperties.put(isMobUsable.getPropertyName(), isMobUsable);
		}

		public Builder damage(int defaultValue)
		{
			minimumDamage(defaultValue);
			maximumDamage(defaultValue);
			return this;
		}

		public Builder minimumDamage(int defaultValue)
		{
			damageMin = new IntProperty("minDamage", "The minimum damage this weapon does", defaultValue,
					Integer.MIN_VALUE, Integer.MAX_VALUE);
			usedProperties.put(damageMin.getPropertyName(), damageMin);
			return this;
		}

		public Builder maximumDamage(int defaultValue)
		{
			damageMax = new IntProperty("maxDamage", "The maximum damage this weapon does", defaultValue,
					Integer.MIN_VALUE, Integer.MAX_VALUE);
			usedProperties.put(damageMax.getPropertyName(), damageMax);
			return this;
		}

		public Builder projectileSpeed(float defaultValue)
		{
			projectileSpeed = new FloatProperty("projectileSpeed", "The speed of the projectile, in blocks per tick",
					defaultValue, Float.MIN_VALUE, Float.MAX_VALUE);
			usedProperties.put(projectileSpeed.getPropertyName(), projectileSpeed);
			return this;
		}

		public Builder knockback(int defaultValue)
		{
			knockback = new IntProperty("knockback",
					"The amount of knockback the projectile applies to entities it hits", defaultValue,
					Integer.MIN_VALUE, Integer.MAX_VALUE);
			usedProperties.put(knockback.getPropertyName(), knockback);
			return this;
		}

		public Builder cooldown(int defaultValue)
		{
			cooldown = new IntProperty("cooldown", "How many ticks it takes for this weapon to be able to fire again",
					defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
			usedProperties.put(cooldown.getPropertyName(), cooldown);
			return this;
		}

		public Builder kickback(int defaultValue)
		{
			kickback = new IntProperty("kickback", "The amount of knockback the projectile applies to the user",
					defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
			usedProperties.put(kickback.getPropertyName(), kickback);
			return this;
		}

		public Builder mobUsable()
		{
			isMobUsable = new BooleanProperty("isMobUsable", "QuiverMobs can spawn with this weapon if true", true);
			return this;
		}

		public Builder booleanProperty(String name, String comment, boolean defaultValue)
		{
			usedProperties.put(name, new BooleanProperty(name, comment, defaultValue));
			return this;
		}

		public Builder intProperty(String name, String comment, int defaultValue)
		{
			usedProperties.put(name,
					new IntProperty(name, comment, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE));
			return this;
		}

		public Builder floatProperty(String name, String comment, float defaultValue)
		{
			usedProperties.put(name, new FloatProperty(name, comment, defaultValue, Float.MIN_VALUE, Float.MAX_VALUE));
			return this;
		}

		public WeaponProperties build()
		{
			if (damageMin == null) damageMin = IntProperty.createIgnored("damageMin");
			if (damageMax == null) damageMax = IntProperty.createIgnored("damageMax");
			if (projectileSpeed == null) projectileSpeed = FloatProperty.createIgnored("projectileSpeed");
			if (knockback == null) knockback = IntProperty.createIgnored("knockback");
			if (kickback == null) kickback = IntProperty.createIgnored("kickback");
			if (cooldown == null) cooldown = IntProperty.createIgnored("cooldown");
			return new WeaponProperties(this);
		}
	}
}
