package com.domochevsky.quiverbow.recipes;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class RecipeLoadAmmo implements IRecipe
{
	private final Item targetWeapon;
	private final HashMap<Item, AmmoData> ammoComponents = Maps.newHashMap();

	public RecipeLoadAmmo(Item targetWeapon)
	{
		this.targetWeapon = targetWeapon;
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
			else if (!ammoComponents.containsKey(stack.getItem())) return false;
		}
		for (Map.Entry<Item, AmmoData> componentEntry : ammoComponents.entrySet())
		{
			int componentCount = 0;
			for (int s = 0; s < invCrafting.getSizeInventory(); s++)
			{
				ItemStack stack = invCrafting.getStackInSlot(s);
				if (stack.isEmpty()) continue;
				if (stack.getItem() == componentEntry.getKey())
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
			if (ammoComponents.containsKey(stack.getItem()))
			{
				int ammoValue = ammoComponents.get(stack.getItem()).ammoValue;
				weapon.setItemDamage(weapon.getItemDamage() - ammoValue);
			}
		}
		return weapon;
	}

	public RecipeLoadAmmo addComponent(Block block, int ammoValue)
	{
		ammoComponents.put(Item.getItemFromBlock(block), new AmmoData(ammoValue));
		return this;
	}

	public RecipeLoadAmmo addComponent(Block block, int ammoValue, int min, int max)
	{
		ammoComponents.put(Item.getItemFromBlock(block), new AmmoData(ammoValue, min, max));
		return this;
	}

	public RecipeLoadAmmo addComponent(Item item, int ammoValue)
	{
		ammoComponents.put(item, new AmmoData(ammoValue));
		return this;
	}

	public RecipeLoadAmmo addComponent(Item item, int ammoValue, int min, int max)
	{
		ammoComponents.put(item, new AmmoData(ammoValue, min, max));
		return this;
	}

	@Override
	public int getRecipeSize()
	{
		return 10;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return ItemStack.EMPTY;
	}

	private static class AmmoData
	{
		private int ammoValue;
		private int min;
		private int max;

		public AmmoData(int ammoValue)
		{
			this.ammoValue = ammoValue;
			this.min = 1;
			this.max = 8;
		}

		public AmmoData(int ammoValue, int min, int max)
		{
			this.ammoValue = ammoValue;
			this.min = min;
			this.max = max;
		}
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
	{
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}
