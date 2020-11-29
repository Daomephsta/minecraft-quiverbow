package com.domochevsky.quiverbow.integration.jei;

import java.util.Collection;
import java.util.stream.Collectors;

import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.google.common.collect.Streams;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class RestrungJeiPlugin implements IModPlugin
{
    @Override
    public void register(IModRegistry registry)
    {
        IStackHelper stackHelper = registry.getJeiHelpers().getStackHelper();
        Collection<?> recipes = Streams.stream(ReloadSpecificationRegistry.INSTANCE.getSpecifications())
            .map(entry -> new RecipeLoadAmmoCategory.Wrapper(entry.getKey(), entry.getValue(), stackHelper))
            .collect(Collectors.toList());
        registry.addRecipes(recipes, RecipeLoadAmmoCategory.ID);
        registry.addIngredientInfo(new ItemStack(ItemRegistry.ENDER_RAIL_ACCELERATOR), VanillaTypes.ITEM,
            ItemRegistry.ENDER_RAIL_ACCELERATOR.getUnlocalizedName() + ".jei_description");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new RecipeLoadAmmoCategory(guiHelper));
    }
}
