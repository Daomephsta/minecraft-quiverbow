package com.domochevsky.quiverbow.recipes;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.*;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.*;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeLoadAmmo extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
	private final Item targetWeapon;
	private final List<Map.Entry<Ingredient, AmmoData>> ammoComponents;

	private RecipeLoadAmmo(Item targetWeapon, List<Map.Entry<Ingredient, AmmoData>> ammoComponents)
	{
		this.targetWeapon = targetWeapon;
		this.ammoComponents = ammoComponents;
	}

	@Override
	public boolean matches(InventoryCrafting invCrafting, World world)
	{
		boolean weaponFound = false;

		for (int s = 0; s < invCrafting.getSizeInventory(); s++)
		{
			ItemStack stack = invCrafting.getStackInSlot(s);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == this.targetWeapon)
			{
				if (stack.getItemDamage() == 0) return false;// Already full
				if (!weaponFound) weaponFound = true;
				else return false; // Cannot reload two weapons at the same time
			}
			else
			{

			}
		}
		for (Map.Entry<Ingredient, AmmoData> componentEntry : ammoComponents)
		{
			int componentCount = 0;
			for (int s = 0; s < invCrafting.getSizeInventory(); s++)
			{
				ItemStack stack = invCrafting.getStackInSlot(s);
				if (stack.isEmpty()) continue;
				if (componentEntry.getKey().apply(stack))
				{
					componentCount++;
					if (componentCount > componentEntry.getValue().max) return false;
				}
			}
			if (componentCount < componentEntry.getValue().min) return false;
		}
		return weaponFound;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting invCrafting)
	{
		ItemStack weapon = ItemStack.EMPTY;
		int s;
		for (s = 0; s < invCrafting.getSizeInventory(); s++)
		{
			ItemStack stack = invCrafting.getStackInSlot(s);
			if (stack.isEmpty()) continue;
			if (stack.getItem() == this.targetWeapon)
			{
				weapon = stack.copy();
			}
		}
		for (s = 0; s < invCrafting.getSizeInventory(); s++)
		{
			ItemStack stack = invCrafting.getStackInSlot(s);
			if (stack.isEmpty()) continue;
			for (Map.Entry<Ingredient, AmmoData> ammoComponent : ammoComponents)
			{
				if (ammoComponent.getKey().apply(stack))
				{
					int ammoValue = ammoComponent.getValue().ammoValue;
					weapon.setItemDamage(weapon.getItemDamage() - ammoValue);
					break;
				}
			}
		}
		return weapon;
	}

	public RecipeLoadAmmo addComponent(Ingredient ingredient, int ammoValue)
	{
		return addComponent(ingredient, ammoValue, 1, 8);
	}

	public RecipeLoadAmmo addComponent(Ingredient ingredient, int ammoValue, int min, int max)
	{
		ammoComponents.add(Pair.of(ingredient, new AmmoData(ammoValue, min, max)));
		return this;
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return width * height >= 2;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
	{
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}

	private static class AmmoData
	{
		private int ammoValue;
		private int min;
		private int max;

		public AmmoData(int ammoValue, int min, int max)
		{
			this.ammoValue = ammoValue;
			this.min = min;
			this.max = max;
		}
	}

	public static class Factory implements IRecipeFactory
	{
		@Override
		public IRecipe parse(JsonContext context, JsonObject json)
		{
			Item weapon = JsonUtils.getItem(json, "weapon");
			JsonArray componentsJSON = JsonUtils.getJsonArray(json, "components");
			List<Map.Entry<Ingredient, AmmoData>> components = new ArrayList<>();
			for (JsonElement element : componentsJSON)
			{
				if (!element.isJsonObject())
					throw new JsonSyntaxException("Expected " + element + " to be a JSON object");
				components.add(deserialiseIngredientAmmoDataEntry((JsonObject) element, context));
			}
			return new RecipeLoadAmmo(weapon, components);
		}

		private static Map.Entry<Ingredient, AmmoData> deserialiseIngredientAmmoDataEntry(JsonObject entry, JsonContext context)
		{
			Ingredient ingredient = CraftingHelper.getIngredient(entry.get("ing"), context);
			AmmoData ammoData = deserialiseAmmoData(JsonUtils.getJsonObject(entry, "data"));
			return new AbstractMap.SimpleImmutableEntry<Ingredient, AmmoData>(ingredient, ammoData);
		}

		private static AmmoData deserialiseAmmoData(JsonObject jsonObj)
		{
			return new AmmoData(JsonUtils.getInt(jsonObj, "ammoValue"), JsonUtils.getInt(jsonObj, "min"),
					JsonUtils.getInt(jsonObj, "max"));
		}
	}
}
