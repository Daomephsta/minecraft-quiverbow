package com.domochevsky.quiverbow.recipes;

import java.util.List;

import com.domochevsky.quiverbow.miscitems.PackedUpAA;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;

public class Recipe_AA_Armor extends ShapelessRecipes implements IRecipe
{
    public Recipe_AA_Armor(ItemStack result, List<ItemStack> components)
    {
	super(result, components);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting matrix)
    {
	ItemStack stack = this.getRecipeOutput().copy();
	ItemStack previousAA = this.getAAFromMatrix(matrix);

	if (!previousAA.isEmpty() && previousAA.hasTagCompound()) // Copying
							       // existing
							       // properties
	{
	    stack.setTagCompound((NBTTagCompound) previousAA.getTagCompound().copy());
	}
	else // ...or just applying new ones
	{
	    stack.setTagCompound(new NBTTagCompound());
	}

	// Apply the new upgrade now
	stack.getTagCompound().setBoolean("hasArmorUpgrade", true);

	stack.getTagCompound().setInteger("currentHealth", 40); // Free healing
								// to go along
								// with that
								// upgrade

	return stack;
    }

    private ItemStack getAAFromMatrix(InventoryCrafting matrix)
    {
	int counter = 0;

	while (counter < matrix.getSizeInventory())
	{
	    if (!matrix.getStackInSlot(counter).isEmpty()
		    && matrix.getStackInSlot(counter).getItem() instanceof PackedUpAA)
	    {
		return matrix.getStackInSlot(counter); // Found it
	    }

	    counter += 1;
	}

	return ItemStack.EMPTY;
    }
}
