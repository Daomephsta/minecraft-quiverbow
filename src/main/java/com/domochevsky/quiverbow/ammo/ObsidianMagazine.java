package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.util.InventoryHelper;
import com.domochevsky.quiverbow.util.Utils;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;

public class ObsidianMagazine extends AmmoMagazine
{
    public ObsidianMagazine()
    {
	super();
	this.setMaxDamage(16);
	this.setCreativeTab(CreativeTabs.COMBAT); // On the combat tab by
						  // default, since this is
						  // amunition
    }

    @Override
    public String getIconPath()
    {
	return "ObsidianAmmo";
    }

    @Override
    public void addRecipes()
    {
	GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xox", 'x', Items.IRON_INGOT,
		'o', Blocks.OBSIDIAN);
    }

    @Override
    protected boolean hasComponentItems(EntityPlayer player, int amount)
    {
	return InventoryHelper.hasItem(player, Items.GUNPOWDER, amount)
		&& InventoryHelper.hasBlock(player, Blocks.OBSIDIAN, amount);
    }

    @Override
    protected boolean consumeComponentItems(EntityPlayer player, int amount)
    {
	Utils.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.30F);
	return InventoryHelper.consumeItem(player, Items.GUNPOWDER, amount)
		&& InventoryHelper.consumeBlock(player, Blocks.OBSIDIAN, amount);
    }
}
