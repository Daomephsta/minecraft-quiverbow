package com.domochevsky.quiverbow.integration.patchouli;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.config.properties.BooleanProperty;
import com.domochevsky.quiverbow.config.properties.WeaponProperty;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.google.common.collect.Iterables;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

public class WeaponPropertiesComponentProcessor implements IComponentProcessor
{
    private static final Object2IntMap<String> PROPERTY_ORDER;
    private static final Comparator<WeaponProperty> PROPERTY_COMPARATOR;
    static
    {
        String[] propertyOrder = {"minDamage", "maxDamage", "minDamageMagic", "maxDamageMagic"};
        PROPERTY_ORDER = new Object2IntOpenHashMap<>(propertyOrder.length);
        for (int i = 0; i < propertyOrder.length; i++)
            PROPERTY_ORDER.put(propertyOrder[i], i);
        PROPERTY_COMPARATOR = (a, b) ->
        {
            String aName = a.getPropertyName();
            String bName = b.getPropertyName();
            if (PROPERTY_ORDER.containsKey(aName) && PROPERTY_ORDER.containsKey(bName))
                return Integer.compare(PROPERTY_ORDER.getInt(aName), PROPERTY_ORDER.getInt(bName));
            else if (PROPERTY_ORDER.containsKey(aName))
                return -1;
            else if (PROPERTY_ORDER.containsKey(bName))
                return 1;
            else
                return aName.compareTo(bName);
        };
    }
    private Weapon weapon;

    @Override
    public void setup(IVariableProvider<String> variables)
    {
        ResourceLocation weaponId = new ResourceLocation(variables.get("weapon"));
        Item item = ForgeRegistries.ITEMS.getValue(weaponId);
        if (item instanceof Weapon)
            this.weapon = (Weapon) item;
        else
            throw new IllegalArgumentException("Unknown weapon " + weaponId);
    }

    @Override
    public String process(String key)
    {
        if (!key.equals("properties"))
            throw new IllegalArgumentException("Unknown key " + key);

        StringBuilder output = new StringBuilder();
        WeaponProperties properties = weapon.getProperties();
        writeProperties(output, properties);
        if (properties.hasSubProjectileProperties())
        {
            output.append("$(2br)$(bold)").append(I18n.format(QuiverbowMain.MODID + ".subprojectile.heading"))
                .append("$(reset)$(br)");
            writeProperties(output, properties.getSubProjectileProperties());
        }
        return output.toString();
    }

    private void writeProperties(StringBuilder output, WeaponProperties properties)
    {
        Collection<WeaponProperty> sortedProperties = new TreeSet<>(PROPERTY_COMPARATOR);
        Iterables.addAll(sortedProperties, properties);
        for (WeaponProperty weaponProperty : sortedProperties)
        {
            if (weaponProperty.getPropertyName().equals("enabled") ||
                weaponProperty.getPropertyName().equals("isMobUsable"))
            {
                continue;
            }

            String value;
            if (weaponProperty instanceof BooleanProperty)
            {
                value = I18n.format(QuiverbowMain.MODID + ".property_format." +
                    ((BooleanProperty) weaponProperty).getValue() + "Value");
            }
            else value = weaponProperty.getValueAsString();

            String translated = I18n.format(QuiverbowMain.MODID +
                ".property_format." + weaponProperty.getPropertyName(), value);
            output.append(translated).append("$()").append("$(br)");
        }
    }
}
