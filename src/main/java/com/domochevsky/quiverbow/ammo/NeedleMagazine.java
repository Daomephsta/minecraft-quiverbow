package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.util.InventoryHelper;
import com.domochevsky.quiverbow.util.Utils;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;

public class NeedleMagazine extends AmmoMagazine
{
    public NeedleMagazine()
    {
	super(1, 8);
	this.setMaxDamage(64); // Filled with cactus thorns
	this.setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public String getIconPath()
    {
	return "NeedleAmmo";
    }

    @Override
    public void addRecipes()
    {
	GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xix", 'x', Items.LEATHER,
		'i', Items.IRON_INGOT);
    }

    @Override
    protected boolean hasComponentItems(EntityPlayer player, int amount)
    {
	return InventoryHelper.hasBlock(player, Blocks.CACTUS, amount);
    }

    @Override
    protected boolean consumeComponentItems(EntityPlayer player, int amount)
    {
	Utils.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 1.3F);
	return InventoryHelper.consumeBlock(player, Blocks.CACTUS, amount);
    }
}
