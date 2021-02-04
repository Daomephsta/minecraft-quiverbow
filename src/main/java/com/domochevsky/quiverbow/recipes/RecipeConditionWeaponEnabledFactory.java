package com.domochevsky.quiverbow.recipes;

import java.util.function.BooleanSupplier;

import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.google.gson.JsonObject;

import net.minecraft.item.Item;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class RecipeConditionWeaponEnabledFactory implements IConditionFactory
{
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject jsonObj)
    {
        String regName = JsonUtils.getString(jsonObj, "id");
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(regName));
        if(item == null) throw new IllegalArgumentException(regName + " is not a registered item");
        if(item instanceof Weapon)
            return () -> ((Weapon) item).getProperties().isEnabled();
        throw new IllegalArgumentException(regName + " is not an instance of Weapon/WeaponBase");
    }
}
