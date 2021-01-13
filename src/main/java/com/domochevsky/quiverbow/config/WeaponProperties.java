package com.domochevsky.quiverbow.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.properties.BooleanProperty;
import com.domochevsky.quiverbow.config.properties.FloatProperty;
import com.domochevsky.quiverbow.config.properties.IntProperty;
import com.domochevsky.quiverbow.config.properties.WeaponProperty;

import net.minecraftforge.common.config.ConfigCategory;

public class WeaponProperties
{
    public static final Pair<String, String>
        ENABLED = Pair.of("enabled", "Enables crafting this weapon if true"),
        DAMAGE_MIN = Pair.of("minDamage", "The minimum damage this weapon does"),
        DAMAGE_MAX = Pair.of("maxDamage", "The maximum damage this weapon does"),
        PROJECTILE_SPEED = Pair.of("projectileSpeed", "The speed of the projectile, in blocks per tick"),
        KNOCKBACK = Pair.of("knockback", "The amount of knockback the projectile applies to entities it hits"),
        KICKBACK = Pair.of("kickback", "The amount of knockback the projectile applies to the user"),
        COOLDOWN = Pair.of("cooldown", "How many ticks it takes for this weapon to be able to fire again"),
        MOB_USABLE = Pair.of("isMobUsable", "QuiverMobs can spawn with this weapon if true");
    private Map<String, WeaponProperty> properties;
    private WeaponProperties subProjectileProperties;

    public WeaponProperties(Builder builder)
    {
        this.properties = builder.properties;
        this.subProjectileProperties = builder.subProjectileProperties;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public void loadFromConfig(ConfigCategory configCategory, Function<String, ConfigCategory> subCategoryGetter)
    {
        for (WeaponProperty property : properties.values())
        {
            if (configCategory.containsKey(property.getPropertyName()))
                property.loadFromConfig(configCategory.get(property.getPropertyName()));
            else
                configCategory.put(property.getPropertyName(), property.createDefaultForgeConfigProperty());
        }
        if (subProjectileProperties != null)
            subProjectileProperties.loadFromConfig(subCategoryGetter.apply("sub_projectile"), subCategoryGetter);
    }

    public boolean isEnabled()
    {
        return getBoolean(ENABLED);
    }

    public int getDamageMin()
    {
        return getInt(DAMAGE_MIN);
    }

    public int getDamageMax()
    {
        return getInt(DAMAGE_MAX);
    }

    public float getProjectileSpeed()
    {
        return has(PROJECTILE_SPEED) ? getFloat(PROJECTILE_SPEED) : Float.POSITIVE_INFINITY;
    }

    public int getKnockback()
    {
        return has(KNOCKBACK) ? getInt(KNOCKBACK) : 0;
    }

    public int getKickback()
    {
        return has(KICKBACK) ? getInt(KICKBACK) : 0;
    }

    public int getMaxCooldown()
    {
        return has(COOLDOWN) ? getInt(COOLDOWN) : 0;
    }

    public boolean isMobUsable()
    {
        return getBoolean(MOB_USABLE);
    }

    public WeaponProperties getSubProjectileProperties()
    {
        if (subProjectileProperties == null)
            throw new IllegalArgumentException("No subprojectile properties exist");
        return subProjectileProperties;
    }

    public boolean has(Pair<String, String> property)
    {
        return properties.containsKey(property.getLeft());
    }

    public boolean has(String name)
    {
        return properties.containsKey(name);
    }

    public boolean getBoolean(Pair<String, String> property)
    {
        return getPropertyAndCheck(property.getLeft(), BooleanProperty.class).getValue();
    }

    public int getInt(Pair<String, String> property)
    {
        return getPropertyAndCheck(property.getLeft(), IntProperty.class).getValue();
    }

    public float getFloat(Pair<String, String> property)
    {
        return getPropertyAndCheck(property.getLeft(), FloatProperty.class).getValue();
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
        WeaponProperty property = properties.get(name);
        if (property == null) throw new IllegalArgumentException("No property named " + name + " exists");
        if (!expectedClass.isInstance(property))
            throw new IllegalArgumentException(String.format("Expected %s named %s, not %s",
                    expectedClass.getSimpleName(), name, property.getClass().getSimpleName()));
        return (T) property;
    }

    public static class Builder
    {
        private Map<String, WeaponProperty> properties = new HashMap<>();
        private WeaponProperties subProjectileProperties;

        public Builder damage(int defaultValue)
        {
            minimumDamage(defaultValue);
            maximumDamage(defaultValue);
            return this;
        }

        public Builder minimumDamage(int defaultValue)
        {
            return intProperty(DAMAGE_MIN, defaultValue);
        }

        public Builder maximumDamage(int defaultValue)
        {
            return intProperty(DAMAGE_MAX, defaultValue);
        }

        public Builder projectileSpeed(float defaultValue)
        {
            return floatProperty(PROJECTILE_SPEED, defaultValue);
        }

        public Builder knockback(int defaultValue)
        {
            return intProperty(KNOCKBACK, defaultValue);
        }

        public Builder cooldown(int defaultValue)
        {
            return intProperty(COOLDOWN, defaultValue);
        }

        public Builder kickback(int defaultValue)
        {
            return intProperty(KICKBACK, defaultValue);
        }

        public Builder mobUsable()
        {
            return booleanProperty(MOB_USABLE, true);
        }

        public Builder booleanProperty(Pair<String, String> property, boolean defaultValue)
        {
            return booleanProperty(property.getLeft(), property.getRight(), defaultValue);
        }

        public Builder booleanProperty(String name, String comment, boolean defaultValue)
        {
            properties.put(name, new BooleanProperty(name, comment, defaultValue));
            return this;
        }

        public Builder intProperty(Pair<String, String> property, int defaultValue)
        {
            return intProperty(property.getLeft(), property.getRight(), defaultValue);
        }

        public Builder intProperty(String name, String comment, int defaultValue)
        {
            properties.put(name,
                    new IntProperty(name, comment, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE));
            return this;
        }

        public Builder floatProperty(Pair<String, String> property, float defaultValue)
        {
            return floatProperty(property.getLeft(), property.getRight(), defaultValue);
        }

        public Builder floatProperty(String name, String comment, float defaultValue)
        {
            properties.put(name, new FloatProperty(name, comment, defaultValue, Float.MIN_VALUE, Float.MAX_VALUE));
            return this;
        }

        public Builder withSubProjectileProperties(Consumer<Builder> subProjectileProperties)
        {
            Builder builder = WeaponProperties.builder();
            subProjectileProperties.accept(builder);
            this.subProjectileProperties = builder.build();
            return this;
        }

        public WeaponProperties build()
        {
            return new WeaponProperties(this);
        }
    }

    public int generateDamage(Random rand)
    {
        return Helper.randomIntInRange(rand, getDamageMin(), getDamageMax());
    }
}
