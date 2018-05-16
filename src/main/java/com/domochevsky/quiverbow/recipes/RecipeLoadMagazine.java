package com.domochevsky.quiverbow.recipes;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.weapons.OSP;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.google.gson.JsonObject;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeLoadMagazine extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
	private final AmmoBase ammo;
	private final WeaponBase weapon;
	private int metadata;

	private RecipeLoadMagazine(AmmoBase ammo, WeaponBase weapon)
	{
		this.ammo = ammo;
		this.weapon = weapon;
	}

	@Override
	public boolean matches(InventoryCrafting matrix, World world) // Returns
	// true if
	// these
	// components
	// are what
	// I'm looking
	// for to make
	// my item
	{
		if (!this.isInMatrix(matrix, this.weapon))
		{
			return false;
		} // Weapon ain't in the matrix
		if (!this.isInMatrix(matrix, this.ammo))
		{
			return false;
		} // Ammo ain't in the matrix

		return true; // Checks out
	}

	// Returns true if the requested item is anywhere in the matrix
	private boolean isInMatrix(InventoryCrafting matrix, Item item)
	{
		if (item == null)
		{
			return false;
		} // Can't find what doesn't exist

		int counter = 0;

		ItemStack stack = matrix.getStackInSlot(counter);

		while (counter < matrix.getSizeInventory()) // scouring through the
		// entire thing
		{
			if (!stack.isEmpty() && stack.getItem().getClass() == item.getClass()) // Found
			// one!
			{
				if (stack.getItem() instanceof WeaponBase) // Is a weapon, so
				// need to ensure
				// that it's empty
				{
					if (stack.getItemDamage() == stack.getMaxDamage())
					{
						return true;
					}
					// else, isn't empty
				}
				else if (stack.getItem() instanceof AmmoBase) // is ammo
				{
					this.metadata = stack.getItemDamage(); // Keeping track of
					// what this is gonna
					// make, so I don't
					// have to constantly
					// recheck
					return true;
				}
				// else, don't care what this is
			}
			// else, empty. That's fine

			// Next!
			counter += 1;
			stack = matrix.getStackInSlot(counter);
		}

		return false; // Fallback. Didn't find what I'm looking for
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return new ItemStack(this.weapon, 1, this.metadata);
	}
	
	@Override
	public String getGroup()
	{
		return QuiverbowMain.MODID + ":load_magazine";
	}
	
	@Override
	public boolean canFit(int width, int height)
	{
		return width * height >= 2;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting matrix)
	{
		if (this.weapon instanceof OSP)
		{
			this.metadata *= 2;
		} // Two shots per bullet

		ItemStack stack = new ItemStack(this.weapon, 1, this.metadata);

		Helper.copyProps(matrix, stack);

		return stack;
	}
	
	public static class Factory implements IRecipeFactory
	{
		@Override
		public IRecipe parse(JsonContext context, JsonObject json)
		{
			AmmoBase ammo = (AmmoBase) JsonUtils.getItem(json, "ammo");
			WeaponBase weapon = (WeaponBase) JsonUtils.getItem(json, "weapon");
			return new RecipeLoadMagazine(ammo, weapon);
		}
	}
}
