package com.domochevsky.quiverbow.miscitems;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.armsassistant.*;
import com.domochevsky.quiverbow.items.ItemRegistry;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class PackedUpAA extends QuiverBowItem
{
	public static final String TAG_UPGRADES = "upgrades", TAG_HEALTH = "currentHealth";

	public static ItemStack withUpgrade(ItemStack stack, IArmsAssistantUpgrade upgrade)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound stackTag = stack.getTagCompound();
		
		if(!stackTag.hasKey(TAG_UPGRADES)) stackTag.setTag(TAG_UPGRADES, new NBTTagList());
		NBTTagList upgradeList = stackTag.getTagList(TAG_UPGRADES, NBT.TAG_STRING);
		upgradeList.appendTag(new NBTTagString(UpgradeRegistry.getUpgradeID(upgrade).toString()));

		return stack;
	}
	
	public static ItemStack createPackedArmsAssistant(Collection<IArmsAssistantUpgrade> upgrades, float health, @Nullable String customName)
	{
		ItemStack stack = new ItemStack(ItemRegistry.ARMS_ASSISTANT);
		stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound stackTag = stack.getTagCompound();
		
		if (!customName.isEmpty()) stack.setStackDisplayName(customName);
		
		if (!upgrades.isEmpty())
		{
			NBTTagList upgradeList = stackTag.getTagList(TAG_UPGRADES, NBT.TAG_STRING);
			for (IArmsAssistantUpgrade upgrade : upgrades)
			{
				ResourceLocation upgradeID = UpgradeRegistry.getUpgradeID(upgrade);
				if (upgradeID == null)
					QuiverbowMain.logger.error("Unknown upgrade with class {}", upgrade.getClass().getName());
				else
					upgradeList.appendTag(new NBTTagString(upgradeID.toString()));
			}
			stackTag.setTag(TAG_UPGRADES, upgradeList);
		}

		stackTag.setFloat(TAG_HEALTH, health);
		
		return stack;
	}
	
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
		super.addInformation(stack, world, list, flags);
		if (stack.hasTagCompound())
		{
			NBTTagCompound stackTag = stack.getTagCompound();
			int maxHealth = hasUpgrade(stack, UpgradeRegistry.ARMOUR) ? 40 : 20;

			list.add(I18n.format(getUnlocalizedName(stack) + ".tooltip.health", stack.getTagCompound().getInteger("currentHealth"), maxHealth));
			
			boolean hasMore = stackTag.hasKey(TAG_UPGRADES);
			if (hasMore)
			{
				if (GuiScreen.isShiftKeyDown())
				{
					if (stackTag.hasKey(TAG_UPGRADES))
					{
						list.add(I18n.format(getUnlocalizedName() + ".tooltip.upgrades", ""));
						NBTTagList upgradeList = stackTag.getTagList(TAG_UPGRADES, NBT.TAG_STRING);
						for (int i = 0; i < upgradeList.tagCount(); i++)
						{
							String upgradeID = upgradeList.getStringTagAt(i).replace(":", ".aa_upgrade.");
							list.add(I18n.format(upgradeID + ".locname"));
						}
					}
				}
				else list.add(I18n.format(QuiverbowMain.MODID + ".misc.sneakformore", "Shift"));
			}
		}
	}
	
	public static boolean hasUpgrade(ItemStack stack, IArmsAssistantUpgrade upgrade)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound stackTag = stack.getTagCompound();
			if (stackTag.hasKey(TAG_UPGRADES))
			{
				String testUpgradeID = UpgradeRegistry.getUpgradeID(upgrade).toString();
				NBTTagList upgradeList = stackTag.getTagList(TAG_UPGRADES, NBT.TAG_STRING);
				for(int i = 0; i < upgradeList.tagCount(); i++)
				{
					String upgradeID = upgradeList.getStringTagAt(i);
					if(upgradeID.equals(testUpgradeID)) return true;
				}
			}
		}
		return false;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote) return EnumActionResult.SUCCESS;
		
		ItemStack stack = player.getHeldItem(hand);

		EntityArmsAssistant turret = new EntityArmsAssistant(world, player);

		turret.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
		world.spawnEntity(turret);

		// Custom name
		if (stack.hasDisplayName()) turret.setCustomNameTag(stack.getDisplayName());

		// Applying upgrades
		if (stack.hasTagCompound())
		{
			NBTTagCompound stackTag = stack.getTagCompound();
			if(stackTag.hasKey(TAG_UPGRADES))
			{
				NBTTagList upgradeList = stackTag.getTagList(TAG_UPGRADES, NBT.TAG_STRING);
				for(int i = 0; i < upgradeList.tagCount(); i++)
				{
					String upgradeID = upgradeList.getStringTagAt(i);
					IArmsAssistantUpgrade upgrade = UpgradeRegistry.getUpgradeInstance(new ResourceLocation(upgradeID));
					if(upgrade == null)
					{
						QuiverbowMain.logger.error("Unknown upgrade with ID {}", upgradeID);
					}
					else turret.applyUpgrade(upgrade);
				}
			}

			 // Tracking health and reapplying it
			if (stackTag.getFloat(TAG_HEALTH) > 0)
				turret.setHealth(stackTag.getFloat(TAG_HEALTH));
		}

		 // Not deducting them in creative mode
		if (player.capabilities.isCreativeMode) return EnumActionResult.SUCCESS;

		stack.shrink(1);
		return EnumActionResult.SUCCESS;
	}
}
