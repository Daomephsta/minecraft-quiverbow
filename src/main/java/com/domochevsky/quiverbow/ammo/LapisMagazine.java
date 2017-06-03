package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.util.InventoryHelper;
import com.domochevsky.quiverbow.util.Utils;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class LapisMagazine extends AmmoMagazine
{
    public LapisMagazine()
    {
	super();
	this.setMaxDamage(150); // Filled with lapis
	this.setCreativeTab(CreativeTabs.COMBAT); // On the combat tab by
						  // default, since this is
						  // amunition
    }

    /*
     * @SideOnly(Side.CLIENT) private IIcon[] icons = new IIcon[6];
     */

    @Override
    public String getIconPath()
    {
	return "LapisAmmo";
    }

    /*
     * @SideOnly(Side.CLIENT)
     * 
     * @Override public void registerIcons(IIconRegister par1IconRegister) {
     * this.iconEmpty =
     * par1IconRegister.registerIcon("quiverchevsky:ammo/LapisAmmo_0"); for(int
     * i = 0; i < 6; i++) { this.icons[i] =
     * par1IconRegister.registerIcon("quiverchevsky:ammo/LapisAmmo_" + (i + 1));
     * } }
     * 
     * 
     * @Override public IIcon getIconFromDamage(int meta) { if (meta ==
     * this.getMaxDamage()) return this.iconEmpty; else return icons[(int) (5 -
     * Math.floor(meta / 25.0F))]; }
     */

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	if (stack.getItemDamage() == 0)
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // Already fully loaded
	if (stack.getItemDamage() < 25)
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // No room for another lapis block

	if (player.capabilities.isCreativeMode)
	{
	    if (world.isRemote) Minecraft.getMinecraft().ingameGUI
		    .setOverlayMessage(I18n.format("quiverchevsky.ammo.nocreative"), false);
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	}
	if (hasComponentItems(player, 1))
	{
	    // this.consumeItemStack(player.inventory, this.lapisStack); //
	    // We're just grabbing what we need from the inventory

	    int dmg = stack.getItemDamage() - 25;
	    stack.setItemDamage(dmg);

	    consumeComponentItems(player, 1);
	}

	return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addRecipes()
    {
	GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xgx", 'x', Blocks.GLASS_PANE,
		'g', new ItemStack(Items.DYE, 1, 4));
    }

    @Override
    protected boolean hasComponentItems(EntityPlayer player, int amount)
    {
	return InventoryHelper.hasBlock(player, Blocks.LAPIS_BLOCK, amount);
    }

    @Override
    protected boolean consumeComponentItems(EntityPlayer player, int amount)
    {
	Utils.playSoundAtEntityPos(player, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.2F);
	return InventoryHelper.consumeBlock(player, Blocks.LAPIS_BLOCK, amount);
    }
}
