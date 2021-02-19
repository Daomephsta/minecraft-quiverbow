package com.domochevsky.quiverbow.ammo;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.List;

import com.domochevsky.quiverbow.AmmoContainer;
import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ComponentData;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ReloadSpecification;
import com.domochevsky.quiverbow.miscitems.QuiverBowItem;
import com.domochevsky.quiverbow.util.InventoryHelper;
import com.domochevsky.quiverbow.util.NBTags;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class AmmoMagazine extends QuiverBowItem implements AmmoContainer
{
    // How much should this magazine attempt to fill when sneak-clicked?
    private int sneakFillQuantity;
    // How much should this magazine attempt to fill when not sneak-clicked?
    private int standardFillQuantity;
    private int ammoCapacity;
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
        this.addPropertyOverride(new ResourceLocation(QuiverbowMain.MODID, "ammo"),
            (stack, world, entity) -> 1.0F - (float) getAmmo(stack) / (float) getAmmoCapacity());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (getAmmo(stack) >= getAmmoCapacity())
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
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
        if (consumeComponentItems(player, amount))
            addAmmo(stack, amount);
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
        list.add(I18n.format(getUnlocalizedName() + ".clipstatus", getAmmo(stack), getAmmoCapacity()));
        list.add(I18n.format(getUnlocalizedName() + ".filltext"));
        list.add(I18n.format(getUnlocalizedName() + ".description"));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (!isInCreativeTab(tab)) return;
        subItems.add(createFull());
        subItems.add(withAmmo(new ItemStack(this), 0));
    }

    public AmmoMagazine fillSound(SoundEvent fillSound, float fillSoundVolume, float fillSoundPitch)
    {
        this.fillSound = fillSound;
        this.fillSoundVolume = fillSoundVolume;
        this.fillSoundPitch = fillSoundPitch;
        return this;
    }

    public int getAmmo(ItemStack stack)
    {
        return NBTags.getOrCreate(stack).getInteger("ammo");
    }

    public ItemStack withAmmo(ItemStack stack, int ammo)
    {
        NBTags.getOrCreate(stack).setInteger("ammo", max(0, min(ammo, getAmmoCapacity())));
        return stack;
    }

    protected void addAmmo(ItemStack stack, int increment)
    {
        NBTags.getOrCreate(stack).setInteger("ammo",
            min(getAmmo(stack) + increment, getAmmoCapacity()));
    }

    public int getAmmoCapacity()
    {
        return ammoCapacity;
    }

    public AmmoMagazine setAmmoCapacity(int ammoCapacity)
    {
        this.ammoCapacity = ammoCapacity;
        return this;
    }

    @Override
    public ItemStack createFull()
    {
        return withAmmo(new ItemStack(this), getAmmoCapacity());
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return (double) (getAmmoCapacity() - getAmmo(stack)) / (double) getAmmoCapacity();
    }
}
