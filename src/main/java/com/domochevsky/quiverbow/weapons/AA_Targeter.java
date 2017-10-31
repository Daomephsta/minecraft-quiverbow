package com.domochevsky.quiverbow.weapons;

import java.util.Collections;
import java.util.List;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.util.Newliner;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public class AA_Targeter extends _WeaponBase
{
    public AA_Targeter()
    {
	super("aa_target_assist", 1);
	this.setCreativeTab(CreativeTabs.TOOLS); // This is a tool
    } // Not consuming ammo

    public double targetingDistance = 64;

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);

	this.setDamage(stack, 1); // Set to be firing
	this.setCooldown(stack, 4); // 4 ticks

	// SFX
	player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.3F, 2.0F);

	return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
    {
	if (world.isRemote)
	{
	    return;
	} // Not doing this on client side

	if (this.getCooldown(stack) > 0) // Active right now, so ticking down
	{
	    this.setCooldown(stack, this.getCooldown(stack) - 1);

	    if (this.getCooldown(stack) >= 0)
	    {
		this.setDamage(stack, 0);
	    } // Back to inactive
	}
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
	return false;
    } // No point in showing the bar for this

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4)
    {
	Collections.addAll(list, Newliner.translateAndParse(getUnlocalizedName() + ".description"));
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.isMobUsable = config
		.get(this.name, "Can I be used by QuiverMobs? (default false. They're not friends with AAs.)", false)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (Enabled)
	{
	    GameRegistry.addRecipe(new ItemStack(this), "bi ", "iri", " it", 'b', Blocks.NOTEBLOCK, 'r', Items.REPEATER,
		    't', Blocks.TRIPWIRE_HOOK, 'i', Items.IRON_INGOT);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
	subItems.add(new ItemStack(item, 1, 0));
    }
}
