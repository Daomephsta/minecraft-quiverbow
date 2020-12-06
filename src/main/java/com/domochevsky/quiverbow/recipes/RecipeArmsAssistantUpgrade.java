package com.domochevsky.quiverbow.recipes;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.armsassistant.IArmsAssistantUpgrade;
import com.domochevsky.quiverbow.armsassistant.UpgradeRegistry;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.domochevsky.quiverbow.miscitems.PackedUpAA;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.*;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeArmsAssistantUpgrade extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
	private final IRecipe baseRecipe;
	private final IArmsAssistantUpgrade upgrade;

	protected RecipeArmsAssistantUpgrade(IRecipe baseRecipe, IArmsAssistantUpgrade upgrade)
	{
		this.baseRecipe = baseRecipe;
		this.upgrade = upgrade;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		//Check base recipe
		if (!baseRecipe.matches(inv, world)) return false;
		//Check if the upgrade has already been applied
		for(int r = 0; r < inv.getHeight(); r++)
		{
			for (int c = 0; c < inv.getWidth(); c++)
			{
				ItemStack stack = inv.getStackInRowAndColumn(r, c);
				if (stack.getItem() == ItemRegistry.ARMS_ASSISTANT && PackedUpAA.hasUpgrade(stack, upgrade))
					return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		ItemStack armsAssistant = ItemStack.EMPTY;
		for(int r = 0; r < inv.getHeight(); r++)
		{
			for(int c = 0; c < inv.getWidth(); c++)
			{
				ItemStack stack = inv.getStackInRowAndColumn(r, c);
				if (stack.getItem() == ItemRegistry.ARMS_ASSISTANT) armsAssistant = stack.copy();
			}
		}
		return PackedUpAA.withUpgrade(armsAssistant, upgrade);
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return baseRecipe.canFit(width, height);
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return PackedUpAA.withUpgrade(new ItemStack(ItemRegistry.ARMS_ASSISTANT), upgrade);
	}

	@Override
	public NonNullList<Ingredient> getIngredients()
	{
		return baseRecipe.getIngredients();
	}

	@Override
	public String getGroup()
	{
		return QuiverbowMain.MODID + ":arms_assistant_upgrades";
	}

	public boolean isShaped()
	{
	    return baseRecipe instanceof IShapedRecipe;
	}

	public IRecipe getBaseRecipe()
    {
        return baseRecipe;
    }

	private static class Shaped extends RecipeArmsAssistantUpgrade implements IShapedRecipe
	{
        protected Shaped(IRecipe baseRecipe, IArmsAssistantUpgrade upgrade)
        {
            super(baseRecipe, upgrade);
        }

        @Override
        public int getRecipeWidth()
        {
            return ((IShapedRecipe) getBaseRecipe()).getRecipeWidth();
        }

        @Override
        public int getRecipeHeight()
        {
            return ((IShapedRecipe) getBaseRecipe()).getRecipeHeight();
        }
	}

	public static class Factory implements IRecipeFactory
	{
		@Override
		public IRecipe parse(JsonContext context, JsonObject json)
		{
			JsonObject baseRecipeJSON = JsonUtils.getJsonObject(json, "baseRecipe");
			JsonObject dummyResult = new JsonObject();
			dummyResult.addProperty("item", QuiverbowMain.MODID + ":arms_assistant");
			dummyResult.addProperty("data", 0);
			baseRecipeJSON.add("result", dummyResult);

			ResourceLocation upgradeID = new ResourceLocation(JsonUtils.getString(json, "upgrade"));
			IArmsAssistantUpgrade upgrade = UpgradeRegistry.getUpgradeInstance(upgradeID);
			if(upgrade == null) throw new JsonSyntaxException("Unknown upgrade ID: " + upgrade);

			IRecipe recipe = CraftingHelper.getRecipe(baseRecipeJSON, context);
			if (recipe instanceof IShapedRecipe)
			    return new Shaped(recipe, upgrade);
            return new RecipeArmsAssistantUpgrade(recipe, upgrade);
		}
	}
}
