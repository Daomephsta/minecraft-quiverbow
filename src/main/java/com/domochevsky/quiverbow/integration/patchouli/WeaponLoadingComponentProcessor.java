package com.domochevsky.quiverbow.integration.patchouli;

import java.util.HashMap;
import java.util.Map;

import com.domochevsky.quiverbow.ammo.AmmoMagazine;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ComponentData;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ReloadSpecification;
import com.domochevsky.quiverbow.weapons.base.Weapon;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class WeaponLoadingComponentProcessor implements IComponentProcessor
{
    private ReloadSpecification reloadSpecification;
    private Map<String, String> precomputed;

    @Override
    public void setup(IVariableProvider<String> variables)
    {
        this.precomputed = new HashMap<>();

        ResourceLocation weaponId = new ResourceLocation(variables.get("specification"));
        Item item = ForgeRegistries.ITEMS.getValue(weaponId);
        if (item instanceof Weapon)
        {
            this.reloadSpecification = ReloadSpecificationRegistry.INSTANCE.getSpecification((Weapon) item);
            precomputed.put("craft", "true");
        }
        else if (item instanceof AmmoMagazine)
        {
            this.reloadSpecification = ReloadSpecificationRegistry.INSTANCE.getSpecification((AmmoMagazine) item);
            precomputed.put("useMagazine", "true");
        }
        else
            throw new IllegalArgumentException("Unknown weapon or magazine" + weaponId);

        // Check that the right template is being used
        String type = variables.get("type");
        int expectedInputs = Integer.parseInt(type.substring(type.lastIndexOf('/') + 1));
        if (expectedInputs != reloadSpecification.getComponents().size())
            throw new IllegalArgumentException(type + "'s input count does not match " + weaponId);

        int i = 0;
        for (ComponentData component : reloadSpecification.getComponents())
        {
            precomputed.put("input" + i + ".ingredient", PatchouliAPI.instance.serializeIngredient(component.getIngredient()));
            if (component.getMin() == component.getMax())
            {
                precomputed.put("input" + i + ".count",
                    I18n.format("quiverbow_restrung.jei.ammo_loading.quantity", component.getMin()));
            }
            else
            {
                precomputed.put("input" + i + ".countRange",
                    I18n.format("quiverbow_restrung.jei.ammo_loading.quantity_range",
                    component.getMin(), component.getMax()));
            }
            i += 1;
        }
        precomputed.put("output", variables.get("specification"));
    }

    @Override
    public String process(String key)
    {
        return precomputed.get(key);
    }
}
