package com.domochevsky.quiverbow.ammo;

import java.util.Collections;
import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.util.Newliner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AmmoMagazine extends _AmmoBase
{ 
    // How much should this magazine attempt to fill when sneak-clicked?
    private int sneakFillQuantity;
    // How much should this magazine attempt to fill when not sneak-clicked?
    private int standardFillQuantity;

    public AmmoMagazine()
    {
	this(1, 1);
    }

    public AmmoMagazine(int standardFillQuantity, int sneakFillQuantity)
    {
	this.sneakFillQuantity = sneakFillQuantity;
	this.standardFillQuantity = standardFillQuantity;

	this.setMaxStackSize(1);
	this.setHasSubtypes(true);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	if (stack.getItemDamage() == 0)
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // Already fully loaded or player is in Creative mode
	if (player.capabilities.isCreativeMode)
	{
	    if (world.isRemote) Minecraft.getMinecraft().ingameGUI
		    .setOverlayMessage(I18n.format("quiverchevsky.ammo.nocreative"), false);
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	}

	if (player.isSneaking())
	    this.fill(stack, world, player, sneakFillQuantity);
	else this.fill(stack, world, player, standardFillQuantity);

	return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
    }

    protected void fill(ItemStack stack, World world, EntityPlayer player, int amount)
    {
	if (!hasComponentItems(player, amount))
	{
	    if (world.isRemote) Minecraft.getMinecraft().ingameGUI
		    .setOverlayMessage(I18n.format("quiverchevsky.ammo.missingitems"), false);
	    return;
	}
	if (consumeComponentItems(player, amount)) stack.setItemDamage(stack.getItemDamage() - amount);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advancedTooltips)
    {
	Collections.addAll(list, Newliner.translateAndParse(getUnlocalizedName() + ".clipstatus",
		stack.getMaxDamage() - stack.getItemDamage(), stack.getMaxDamage()));
	Collections.addAll(list, Newliner.translateAndParse(getUnlocalizedName() + ".filltext"));
	Collections.addAll(list, Newliner.translateAndParse(getUnlocalizedName() + ".description"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
	subItems.add(new ItemStack(item, 1, 0));
	subItems.add(Helper.createEmptyWeaponOrAmmoStack(item, 1));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
	return true;
    }

    // Does the player have all the items required to refill the magazine?
    protected abstract boolean hasComponentItems(EntityPlayer player, int amount);

    // Consume the items required to refill the magazine.
    protected abstract boolean consumeComponentItems(EntityPlayer player, int amount);
}
