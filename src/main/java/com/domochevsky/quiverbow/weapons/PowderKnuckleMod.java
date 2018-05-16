package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.net.NetHelper;

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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
		world.createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), (float) this.explosionSize, true); // 4.0F
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
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What's my minimum damage, when I'm empty? (default 2)", 2).getInt();
		this.damageMax = config.get(this.name, "What's my maximum damage when I explode? (default 14)", 14).getInt();

		this.explosionSize = config
				.get(this.name, "How big are my explosions? (default 1.5 blocks. TNT is 4.0 blocks)", 1.5).getDouble();
		this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true)
				.getBoolean(true);

		this.isMobUsable = config.get(this.name,
				"Can I be used by QuiverMobs? (default false. They don't know where the trigger on this thing is.)",
				false).getBoolean(false);
	}
}
