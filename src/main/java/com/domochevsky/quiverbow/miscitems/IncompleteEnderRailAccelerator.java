package com.domochevsky.quiverbow.miscitems;

import com.domochevsky.quiverbow.items.ItemRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class IncompleteEnderRailAccelerator extends QuiverBowItem
{
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        EnumHand otherHand = hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        ItemStack otherHandStack = player.getHeldItem(otherHand);
        if (otherHandStack.getItem() == Item.getItemFromBlock(Blocks.GOLDEN_RAIL) && otherHandStack.getCount() >= 27)
        {
            otherHandStack.shrink(27);
            return ActionResult.newResult(EnumActionResult.PASS, new ItemStack(ItemRegistry.ENDER_RAIL_ACCELERATOR));
        }
        else
            player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".completeFail"), true);
        return super.onItemRightClick(world, player, hand);
    }
}
