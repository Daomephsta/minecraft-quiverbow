package com.domochevsky.quiverbow.integration.jei;

import java.util.Collection;
import java.util.stream.Collectors;

import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.domochevsky.quiverbow.recipes.RecipeArmsAssistantUpgrade;
import com.google.common.collect.Streams;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

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
        return recipe.isShaped()
            ? new ShapedRecipeWrapper(recipe, stackHelper)
            : new ShapelessRecipeWrapper<>(jeiHelpers, recipe);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new RecipeLoadAmmoCategory(guiHelper));
    }

    private static class ShapedRecipeWrapper implements IShapedCraftingRecipeWrapper
    {
        private final IRecipe wrapped;
        private final IStackHelper stackHelper;

        public ShapedRecipeWrapper(IRecipe wrapped, IStackHelper stackHelper)
        {
            this.wrapped = wrapped;
            this.stackHelper = stackHelper;
        }

        @Override
        public void getIngredients(IIngredients ingredients)
        {
            ingredients.setInputLists(VanillaTypes.ITEM, stackHelper.expandRecipeItemStackInputs(wrapped.getIngredients()));
            ingredients.setOutput(VanillaTypes.ITEM, wrapped.getRecipeOutput());
        }

        @Override
        public int getWidth()
        {
            return 0;
        }

        @Override
        public int getHeight()
        {
            return 0;
        }
    }
}
