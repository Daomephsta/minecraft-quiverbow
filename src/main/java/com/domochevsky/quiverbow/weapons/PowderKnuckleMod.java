package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class PowderKnuckleMod extends PowderKnuckle
{
	public PowderKnuckleMod()
	{
		super("powder_knuckles_mod", 8);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		// Right click
		if (this.getDamage(stack) >= stack.getMaxDamage())
		{
			return EnumActionResult.FAIL;
		} // Not loaded

		this.consumeAmmo(stack, player, 1);
		// Not safe for clients past here.
		if (world.isRemote) return EnumActionResult.SUCCESS;

		// SFX
		NetHelper.sendParticleMessageToAllPlayers(world, player.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
				(byte) 4); // smoke

		// Dmg
		world.createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), getProperties().getFloat(CommonProperties.PROP_EXPLOSION_SIZE), true); // 4.0F
		// is
		// TNT

		// Mining
		for (int xAxis = -1; xAxis <= 1; xAxis++) // Along the x axis
		{
			for (int yAxis = -1; yAxis <= 1; yAxis++) // Along the y axis
			{
				for (int zAxis = -1; zAxis <= 1; zAxis++) // Along the z axis
				{
					this.doMining(world, (EntityPlayerMP) player, pos.add(xAxis, yAxis, zAxis));
				}
			}
		}

		return EnumActionResult.SUCCESS;
	}

	void doMining(World world, EntityPlayerMP player, BlockPos pos)
	{
		IBlockState toBeBroken = world.getBlockState(pos);

		if (toBeBroken.getBlockHardness(world, pos) == -1)
		{
			return;
		} // Unbreakable

		if (toBeBroken.getBlock().getHarvestLevel(toBeBroken) > 1)
		{
			return;
		}
		if (toBeBroken.getMaterial() == Material.WATER)
		{
			return;
		}
		if (toBeBroken.getMaterial() == Material.LAVA)
		{
			return;
		}
		if (toBeBroken.getMaterial() == Material.AIR)
		{
			return;
		}
		if (toBeBroken.getMaterial() == Material.PORTAL)
		{
			return;
		}

		// Need to do checks here against invalid blocks
		if (toBeBroken == Blocks.WATER)
		{
			return;
		}
		if (toBeBroken == Blocks.FLOWING_WATER)
		{
			return;
		}
		if (toBeBroken == Blocks.LAVA)
		{
			return;
		}
		if (toBeBroken == Blocks.FLOWING_LAVA)
		{
			return;
		}
		if (toBeBroken == Blocks.OBSIDIAN)
		{
			return;
		}
		if (toBeBroken == Blocks.MOB_SPAWNER)
		{
			return;
		}
		GameType gametype = world.getWorldInfo().getGameType();
		int result = ForgeHooks.onBlockBreakEvent(world, gametype, player, pos);
		if (result == -1)
		{
			return;
		} // Not allowed to do this
		world.destroyBlock(pos, true);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(2).maximumDamage(14)
				.floatProperty(CommonProperties.PROP_EXPLOSION_SIZE, CommonProperties.COMMENT_EXPLOSION_SIZE, 1.5F)
				.booleanProperty(CommonProperties.PROP_DAMAGE_TERRAIN, CommonProperties.COMMENT_DAMAGE_TERRAIN, true)
				.build();
	}
}
