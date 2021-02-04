package com.domochevsky.quiverbow.integration.jei;

import java.util.Collection;
import java.util.stream.Collectors;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.domochevsky.quiverbow.recipes.RecipeArmsAssistantUpgrade;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.google.common.collect.Streams;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.oredict.OreDictionary;

@JEIPlugin
public class RestrungJeiPlugin implements IModPlugin
{
    @Override
    public void register(IModRegistry registry)
    {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IStackHelper stackHelper = jeiHelpers.getStackHelper();
        Collection<?> reloadRecipes = Streams.stream(ReloadSpecificationRegistry.INSTANCE.getSpecifications())
            .map(entry -> new RecipeLoadAmmoCategory.Wrapper(entry.getKey(), entry.getValue(), jeiHelpers))
            .collect(Collectors.toList());
        registry.addRecipes(reloadRecipes, RecipeLoadAmmoCategory.ID);
        registry.handleRecipes(RecipeArmsAssistantUpgrade.class,
            recipe -> wrapAAUpgradeRecipe(recipe, stackHelper), VanillaRecipeCategoryUid.CRAFTING);
        registry.addIngredientInfo(new ItemStack(ItemRegistry.ENDER_RAIL_ACCELERATOR), VanillaTypes.ITEM,
            ItemRegistry.ENDER_RAIL_ACCELERATOR.getUnlocalizedName() + ".jei_description");

        IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
        for (Weapon weapon : QuiverbowMain.weapons)
        {
            if (!weapon.getProperties().isEnabled())
                blacklist.addIngredientToBlacklist(new ItemStack(weapon, 1, OreDictionary.WILDCARD_VALUE));
        }
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
