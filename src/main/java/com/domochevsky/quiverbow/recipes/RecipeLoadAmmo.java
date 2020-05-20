package com.domochevsky.quiverbow.recipes;

import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ComponentData;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ReloadSpecification;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeLoadAmmo extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
	private final WeaponBase targetWeapon;
	private final ReloadSpecification specification;

    public RecipeLoadAmmo(WeaponBase targetWeapon)
	{
		this.targetWeapon = targetWeapon;
		this.specification = ReloadSpecificationRegistry.INSTANCE.getSpecification(targetWeapon);
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
		}
		for (ComponentData component : specification.getComponents())
		{
			int componentCount = 0;
			for (int s = 0; s < invCrafting.getSizeInventory(); s++)
			{
				ItemStack stack = invCrafting.getStackInSlot(s);
				if (stack.isEmpty()) continue;
				if (component.getIngredient().apply(stack))
				{
					componentCount++;
					if (componentCount > component.getMax()) return false;
				}
			}
			if (componentCount < component.getMin()) return false;
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
			for (ComponentData component : specification.getComponents())
			{
				if (component.getIngredient().apply(stack))
				{
					int ammoValue = component.getAmmoValue(stack);
					weapon.setItemDamage(weapon.getItemDamage() - ammoValue);
					break;
				}
			}
		}
		return weapon;
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
}
