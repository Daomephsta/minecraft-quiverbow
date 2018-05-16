package com.domochevsky.quiverbow.miscitems;

import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ai.AIProperties;
import com.domochevsky.quiverbow.armsassistant.EntityAA;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PackedUpAA extends QuiverBowItem
{
	@Override // TODO Figure out how to localise this
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
		list.add(ChatFormatting.BLUE + "Use to deploy.");
		list.add(ChatFormatting.YELLOW + "Use Assistant to give it your current weapon.");
		list.add(ChatFormatting.YELLOW + "Use Assistant to give it your current item.");
		list.add(ChatFormatting.YELLOW + "Crouch-use with empty hand to remove gear.");
		list.add(ChatFormatting.YELLOW + "Crouch-use empty Assistant to pack it up.");
		list.add(ChatFormatting.GREEN + "Use Iron Block on Assistant to repair it.");
		list.add(ChatFormatting.GREEN + "Use written book with one name/command per");
		list.add(ChatFormatting.GREEN + "line on Assistant to give it a list");
		list.add(ChatFormatting.GREEN + "of things not to shoot and to do.");
		list.add(ChatFormatting.RED + "Refer to manual for further instructions.");
		list.add("It's sizing you up.");

		if (stack.hasTagCompound()) // Customizations?
		{
			int maxHealth = 20;

			if (stack.getTagCompound().getBoolean("hasArmorUpgrade"))
			{
				maxHealth = 40;
			} // Upgraded health, eh?

			list.add(ChatFormatting.BLUE + "Health: " + stack.getTagCompound().getInteger("currentHealth") + " / "
					+ maxHealth);

			if (stack.getTagCompound().getBoolean("hasArmorUpgrade"))
			{
				list.add(ChatFormatting.BLUE + "Has upgraded armor.");
			}
			if (stack.getTagCompound().getBoolean("hasHeavyPlatingUpgrade"))
			{
				list.add(ChatFormatting.BLUE + "Has heavy plating.");
			}
			if (stack.getTagCompound().getBoolean("hasMobilityUpgrade"))
			{
				list.add(ChatFormatting.BLUE + "Has mobile legs.");
			}
			if (stack.getTagCompound().getBoolean("hasStorageUpgrade"))
			{
				list.add(ChatFormatting.BLUE + "Has extra storage.");
			}
			if (stack.getTagCompound().getBoolean("hasWeaponUpgrade"))
			{
				list.add(ChatFormatting.BLUE + "Has a second rail.");
			}
			if (stack.getTagCompound().getBoolean("hasRidingUpgrade"))
			{
				list.add(ChatFormatting.BLUE + "Has a seat.");
			}
			if (stack.getTagCompound().getBoolean("hasCommunicationUpgrade"))
			{
				list.add(ChatFormatting.BLUE + "Has a radio built in.");
			}
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);

		EntityAA turret = new EntityAA(world, player);

		turret.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
		world.spawnEntity(turret);

		// Custom name
		if (stack.hasDisplayName())
		{
			AIProperties.applyNameTag(player, turret, stack, false);
		}

		// Applying upgrades
		if (stack.hasTagCompound())
		{
			if (stack.getTagCompound().getBoolean("hasArmorUpgrade"))
			{
				AIProperties.applyArmorUpgrade(turret);
			}
			if (stack.getTagCompound().getBoolean("hasHeavyPlatingUpgrade"))
			{
				AIProperties.applyPlatingUpgrade(turret);
			}
			if (stack.getTagCompound().getBoolean("hasMobilityUpgrade"))
			{
				AIProperties.applyMobilityUpgrade(turret);
			}
			if (stack.getTagCompound().getBoolean("hasStorageUpgrade"))
			{
				AIProperties.applyStorageUpgrade(turret);
			}
			if (stack.getTagCompound().getBoolean("hasWeaponUpgrade"))
			{
				AIProperties.applyWeaponUpgrade(turret);
			}
			if (stack.getTagCompound().getBoolean("hasRidingUpgrade"))
			{
				AIProperties.applyRidingUpgrade(turret);
			}
			if (stack.getTagCompound().getBoolean("hasCommunicationUpgrade"))
			{
				AIProperties.applyCommunicationUpgrade(turret);
			}

			if (stack.getTagCompound().getInteger("currentHealth") > 0) // Tracking
			// health
			// and
			// reapplying
			// it
			{
				turret.setHealth(stack.getTagCompound().getInteger("currentHealth"));
			}
		}

		if (player.capabilities.isCreativeMode)
		{
			return EnumActionResult.SUCCESS;
		} // Not deducting them in creative mode

		stack.shrink(1);
		if (stack.getCount() <= 0) // Used up
		{
			player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
		}

		return EnumActionResult.SUCCESS;
	}

	private void registerArmorRecipe()
	{
		ItemStack[] input = new ItemStack[2];

		input[0] = new ItemStack(this);
		input[1] = new ItemStack(Item.getItemFromBlock(Blocks.DIAMOND_BLOCK)); // 1
		// diamond
		// block
		// to
		// upgrade
		// this
		// with

		Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasArmorUpgrade");
	}

	private void registerPlatingRecipe()
	{
		ItemStack[] input = new ItemStack[3];

		input[0] = new ItemStack(this);
		input[1] = new ItemStack(Item.getItemFromBlock(Blocks.EMERALD_BLOCK)); // 1
		// diamond
		// block
		// to
		// upgrade
		// this
		// with
		input[2] = new ItemStack(Item.getItemFromBlock(Blocks.IRON_BLOCK));

		Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasHeavyPlatingUpgrade");
	}

	private void registerMobilityRecipe()
	{
		ItemStack[] input = new ItemStack[9];

		// Top
		input[0] = ItemStack.EMPTY;
		input[1] = new ItemStack(this);
		input[2] = ItemStack.EMPTY;

		// Mid
		input[3] = new ItemStack(Item.getItemFromBlock(Blocks.STICKY_PISTON));
		input[4] = new ItemStack(Items.IRON_INGOT);
		input[5] = new ItemStack(Item.getItemFromBlock(Blocks.STICKY_PISTON));

		// Bottom
		input[6] = new ItemStack(Item.getItemFromBlock(Blocks.IRON_BARS));
		input[7] = ItemStack.EMPTY;
		input[8] = new ItemStack(Item.getItemFromBlock(Blocks.IRON_BARS));

		Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasMobilityUpgrade");
	}

	private void registerStorageRecipe()
	{
		ItemStack[] input = new ItemStack[9];

		// Top
		input[0] = ItemStack.EMPTY;
		input[1] = new ItemStack(Item.getItemFromBlock(Blocks.CHEST));
		input[2] = ItemStack.EMPTY;

		// Mid
		input[3] = new ItemStack(Item.getItemFromBlock(Blocks.STICKY_PISTON));
		input[4] = new ItemStack(this);
		input[5] = new ItemStack(Items.SLIME_BALL);

		// Bottom
		input[6] = ItemStack.EMPTY;
		input[7] = new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));
		input[8] = ItemStack.EMPTY;

		Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasStorageUpgrade");
	}

	private void registerSecondRailRecipe()
	{
		ItemStack[] input = new ItemStack[9];

		// Top
		input[0] = ItemStack.EMPTY;
		input[1] = new ItemStack(this);
		input[2] = ItemStack.EMPTY;

		// Mid
		input[3] = new ItemStack(Item.getItemFromBlock(Blocks.IRON_BLOCK));
		input[4] = new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));
		input[5] = new ItemStack(Item.getItemFromBlock(Blocks.STICKY_PISTON));

		// Bottom
		input[6] = ItemStack.EMPTY;
		input[7] = ItemStack.EMPTY;
		input[8] = new ItemStack(Item.getItemFromBlock(Blocks.STICKY_PISTON));

		Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasWeaponUpgrade");
	}

	private void registerRidingRecipe()
	{
		ItemStack[] input = new ItemStack[3];

		input[0] = new ItemStack(this);
		input[1] = new ItemStack(Items.SADDLE);
		input[2] = new ItemStack(Items.IRON_INGOT);

		Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasRidingUpgrade");
	}

	private void registerCommunicationRecipe()
	{
		ItemStack[] input = new ItemStack[3];

		input[0] = new ItemStack(this);
		input[1] = new ItemStack(Item.getItemFromBlock(Blocks.JUKEBOX)); // Add
		// a
		// jukebox
		input[2] = new ItemStack(Items.REPEATER); // And a repeater to operate
		// it

		Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasCommunicationUpgrade");
	}
}
