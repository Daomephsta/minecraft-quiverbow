package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.QuiverbowMain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class LapisMagazine extends AmmoMagazine
{
    public LapisMagazine()
    {
        super(1, 1);
        this.setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (getAmmo(stack) >= getAmmoCapacity())
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        if (getAmmo(stack) > getAmmoCapacity() - 25)
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        if (player.capabilities.isCreativeMode)
        {
            if (world.isRemote)
            {
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage(
                    I18n.format(QuiverbowMain.MODID + ".ammo.nocreative"), false);
            }
            return ActionResult.newResult(EnumActionResult.FAIL, stack);
        }
        if (hasComponentItems(player, 1))
        {
            addAmmo(stack, 25);
            consumeComponentItems(player, 1);
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }
}
