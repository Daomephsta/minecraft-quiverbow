package com.domochevsky.quiverbow.miscitems;

import net.minecraft.item.Item;

public abstract class QuiverBowItem extends Item
{   
    public String getIconPath()
    {
	return null;
    }
    
    public void addRecipes() { }	// Called once after all items have been registered and initialized
}
