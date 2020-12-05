package com.domochevsky.quiverbow.integration.jei;

import java.util.Collection;
import java.util.stream.Collectors;

import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.domochevsky.quiverbow.recipes.RecipeArmsAssistantUpgrade;
import com.google.common.collect.Streams;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.crafting.IShapedRecipe;

@JEIPlugin
public class RestrungJeiPlugin implements IModPlugin
{
    private IJeiHelpers jeiHelpers;

    @Override
    public void register(IModRegistry registry)
    {
        this.jeiHelpers = registry.getJeiHelpers();
        IStackHelper stackHelper = jeiHelpers.getStackHelper();
        Collection<?> reloadRecipes = Streams.stream(ReloadSpecificationRegistry.INSTANCE.getSpecifications())
            .map(entry -> new RecipeLoadAmmoCategory.Wrapper(entry.getKey(), entry.getValue(), stackHelper))
            .collect(Collectors.toList());
        registry.addRecipes(reloadRecipes, RecipeLoadAmmoCategory.ID);
        registry.handleRecipes(RecipeArmsAssistantUpgrade.class,
            recipe -> wrapAAUpgradeRecipe(recipe, stackHelper), VanillaRecipeCategoryUid.CRAFTING);
        registry.addIngredientInfo(new ItemStack(ItemRegistry.ENDER_RAIL_ACCELERATOR), VanillaTypes.ITEM,
            ItemRegistry.ENDER_RAIL_ACCELERATOR.getUnlocalizedName() + ".jei_description");
    }

    private IRecipeWrapper wrapAAUpgradeRecipe(RecipeArmsAssistantUpgrade recipe, IStackHelper stackHelper)
    {
        if (recipe.isShaped())
        {
            IShapedRecipe baseRecipe = (IShapedRecipe) recipe.getBaseRecipe();
            return new ShapedRecipeWrapper(recipe, stackHelper,
                baseRecipe.getRecipeWidth(), baseRecipe.getRecipeHeight());
        }
        return new ShapelessRecipeWrapper(recipe, stackHelper);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new RecipeLoadAmmoCategory(guiHelper));
    }
}
