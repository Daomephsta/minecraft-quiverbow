package com.domochevsky.quiverbow.ammo;

import java.io.InputStreamReader;
import java.util.*;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.util.Resources;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.google.gson.*;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ReloadSpecificationRegistry
{
    public static final ReloadSpecificationRegistry INSTANCE = new ReloadSpecificationRegistry();
    private final Map<Item, ReloadSpecification> specsByWeapon = new HashMap<>();

    private ReloadSpecificationRegistry() {}

    public ReloadSpecification getSpecification(Item targetWeapon)
    {
        return specsByWeapon.get(targetWeapon);
    }

    public void loadData()
    {
        JsonContext jsonContext = new JsonContext(QuiverbowMain.MODID);
        String reloadSpecsDir = "data/" + QuiverbowMain.MODID + "/reload_specifications";
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Resources.findFileResources(classLoader, reloadSpecsDir, path ->
        {
            ReloadSpecification reloadSpecification = new ReloadSpecification();
            JsonElement root = new JsonParser().parse(new InputStreamReader(classLoader.getResourceAsStream(path)));
            JsonArray components = JsonUtils.getJsonArray(root, "components");
            for (JsonElement component : components)
            {
                if (JsonUtils.isString(component))
                {
                    Item magazine = JsonUtils.getItem(component, "magazine");
                    if (magazine instanceof AmmoMagazine)
                        reloadSpecification.add((AmmoMagazine) magazine);
                    else
                        throw new JsonSyntaxException(magazine + " is not an ammo magazine");
                }
                else if (component.isJsonObject())
                {
                    JsonObject object = component.getAsJsonObject();
                    Ingredient ingredient = CraftingHelper.getIngredient(JsonUtils.getJsonObject(object,
                        "ingredient"), jsonContext);
                    reloadSpecification.add(ingredient,
                        JsonUtils.getInt(object, "ammoValue"),
                        JsonUtils.getInt(object, "min"),
                        JsonUtils.getInt(object, "max"));
                }
            }
            Item weapon = getWeapon(path);
            if (weapon instanceof Weapon)
                specsByWeapon.put(weapon, reloadSpecification);
            else
                throw new JsonSyntaxException(weapon + " is not a weapon");
        });
    }

    private Item getWeapon(String path)
    {
        int fileNameEnd = path.lastIndexOf('/') + 1;
        int extensionStart = path.lastIndexOf('.');
        ResourceLocation weaponId = new ResourceLocation(QuiverbowMain.MODID, path.substring(fileNameEnd, extensionStart));
        return ForgeRegistries.ITEMS.getValue(weaponId);
    }

    public Set<Item> getRegisteredWeapons()
    {
        return specsByWeapon.keySet();
    }

    public static class ReloadSpecification
    {
        private final Collection<ComponentData> components = new HashSet<>();

        private ReloadSpecification add(Ingredient ingredient, int ammoValue, int min, int max)
        {
            components.add(new AmmoData(ingredient, ammoValue, min, max));
            return this;
        }

        private ReloadSpecification add(AmmoMagazine magazine)
        {
            components.add(new MagazineData(Ingredient.fromItem(magazine)));
            return this;
        }

        public Collection<ComponentData> getComponents()
        {
            return components;
        }

        @Override
        public String toString()
        {
            return String.format("ReloadSpecification(components=%s)", components);
        }
    }

    public static interface ComponentData
    {
        Ingredient getIngredient();
        int getAmmoValue(ItemStack stack);
        int getMin();
        int getMax();
    }

    private static class AmmoData implements ComponentData
    {
        private final Ingredient ingredient;
        private final int ammoValue;
        private final int min;
        private final int max;

        public AmmoData(Ingredient ingredient, int ammoValue, int min, int max)
        {
            this.ingredient = ingredient;
            this.ammoValue = ammoValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public Ingredient getIngredient()
        {
            return ingredient;
        }

        @Override
        public int getAmmoValue(ItemStack stack)
        {
            return ammoValue;
        }

        @Override
        public int getMax()
        {
            return max;
        }

        @Override
        public int getMin()
        {
            return min;
        }

        @Override
        public String toString()
        {
            return String.format("AmmoData(ammoValue=%s, min=%s, max=%s)", ammoValue, min, max);
        }
    }

    private static class MagazineData implements ComponentData
    {
        private final Ingredient ingredient;

        public MagazineData(Ingredient ingredient)
        {
            this.ingredient = ingredient;
        }

        @Override
        public Ingredient getIngredient()
        {
            return ingredient;
        }

        @Override
        public int getAmmoValue(ItemStack stack)
        {
            return stack.getMaxDamage() - stack.getItemDamage();
        }

        @Override
        public int getMin()
        {
            return 1;
        }

        @Override
        public int getMax()
        {
            return 1;
        }

        @Override
        public String toString()
        {
            return String.format("MagazineData(%s)", Arrays.toString(ingredient.getMatchingStacks()));
        }
    }
}
