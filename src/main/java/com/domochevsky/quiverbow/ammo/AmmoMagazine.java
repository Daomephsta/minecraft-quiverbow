package com.domochevsky.quiverbow.ammo;

import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ComponentData;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ReloadSpecification;
import com.domochevsky.quiverbow.util.InventoryHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class AmmoMagazine extends AmmoBase
{
	// How much should this magazine attempt to fill when sneak-clicked?
	private int sneakFillQuantity;
	// How much should this magazine attempt to fill when not sneak-clicked?
	private int standardFillQuantity;
	private SoundEvent fillSound;
    private float fillSoundVolume;
    private float fillSoundPitch;

	public AmmoMagazine(int useFillQuantity)
	{
		this(useFillQuantity, useFillQuantity);
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
			if (world.isRemote)
				Minecraft.getMinecraft().ingameGUI.setOverlayMessage(I18n.format(QuiverbowMain.MODID + ".ammo.nocreative"),
						false);
			return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
		}

		if (player.isSneaking()) this.fill(stack, world, player, sneakFillQuantity);
		else this.fill(stack, world, player, standardFillQuantity);

		return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	private void fill(ItemStack stack, World world, EntityPlayer player, int amount)
	{
		if (!hasComponentItems(player, amount))
		{
			if (world.isRemote)
            {
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage(
				    I18n.format(QuiverbowMain.MODID + ".ammo.missingitems"), false);
            }
			return;
		}
		if (consumeComponentItems(player, amount)) stack.setItemDamage(stack.getItemDamage() - amount);
	}

    protected final boolean hasComponentItems(EntityPlayer player, int amount)
    {
        ReloadSpecification specification = ReloadSpecificationRegistry.INSTANCE.getSpecification(this);
        for (ComponentData component : specification.getComponents())
        {
            if (!InventoryHelper.hasIngredient(player, component.getIngredient(), amount))
                return false;
        }
        return true;
    }

    protected final boolean consumeComponentItems(EntityPlayer player, int amount)
    {
        if (fillSound != null)
            Helper.playSoundAtEntityPos(player, fillSound, fillSoundVolume, fillSoundPitch);
        ReloadSpecification specification = ReloadSpecificationRegistry.INSTANCE.getSpecification(this);
        for (ComponentData component : specification.getComponents())
        {
            if (!InventoryHelper.consumeIngredient(player, component.getIngredient(), amount))
                return false;
        }
        return true;
    }

	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
		list.add(I18n.format(getUnlocalizedName() + ".clipstatus",
				stack.getMaxDamage() - stack.getItemDamage(), stack.getMaxDamage()));
		list.add(I18n.format(getUnlocalizedName() + ".filltext"));
		list.add(I18n.format(getUnlocalizedName() + ".description"));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		if (!isInCreativeTab(tab)) return;
		subItems.add(new ItemStack(this, 1, 0));
		subItems.add(Helper.createEmptyWeaponOrAmmoStack(this, 1));
	}

	public AmmoMagazine fillSound(SoundEvent fillSound, float fillSoundVolume, float fillSoundPitch)
	{
	    this.fillSound = fillSound;
	    this.fillSoundVolume = fillSoundVolume;
	    this.fillSoundPitch = fillSoundPitch;
	    return this;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	}

	@Override
	public AmmoMagazine setMaxDamage(int maxDamageIn)
	{
	    return (AmmoMagazine) super.setMaxDamage(maxDamageIn);
	}
}
