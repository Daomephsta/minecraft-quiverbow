package com.domochevsky.quiverbow.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

class ShapedRecipeWrapper implements IShapedCraftingRecipeWrapper
{
    private final IRecipe wrapped;
    private final IStackHelper stackHelper;
    private final int width, height;

    public ShapedRecipeWrapper(IRecipe wrapped, IStackHelper stackHelper, int width, int height)
    {
        this.wrapped = wrapped;
        this.stackHelper = stackHelper;
        this.width = width;
        this.height = height;
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

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }
}