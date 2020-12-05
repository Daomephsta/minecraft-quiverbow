package com.domochevsky.quiverbow.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

class ShapelessRecipeWrapper implements ICraftingRecipeWrapper
{
    private final IRecipe wrapped;
    private final IStackHelper stackHelper;

    public ShapelessRecipeWrapper(IRecipe wrapped, IStackHelper stackHelper)
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
    public ResourceLocation getRegistryName()
    {
        return wrapped.getRegistryName();
    }
}