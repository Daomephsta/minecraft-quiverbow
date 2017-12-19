package com.domochevsky.quiverbow.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class FenLight extends BlockDirectional
{
	private static final float SIZE_MIN = 0.375F;
	private static final float SIZE_MAX = 0.625F;

	public FenLight(Material material)
	{
		super(material);
		this.setLightLevel(0.95F); // Light, yo
		this.setHardness(0.2F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.GLASS);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		this.setDefaultState(blockState.getBaseState());
	}

	public static void placeFenLight(World world, BlockPos pos, EnumFacing facing)
	{
		world.setBlockState(pos, BlockRegistry.FEN_LIGHT.getDefaultState().withProperty(FACING, facing));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess blockAccess, BlockPos pos)
	{
		return getBoundingBox(state, blockAccess, pos);
	}

	private static final AxisAlignedBB DOWN = new AxisAlignedBB(SIZE_MIN, SIZE_MIN + 0.5F, SIZE_MIN, SIZE_MAX,
			SIZE_MAX + 0.375F, SIZE_MAX);
	private static final AxisAlignedBB UP = new AxisAlignedBB(SIZE_MIN, 0, SIZE_MIN, SIZE_MAX, SIZE_MAX - 0.5F,
			SIZE_MAX);
	private static final AxisAlignedBB WEST = new AxisAlignedBB(SIZE_MIN + 0.5F, SIZE_MIN, SIZE_MIN, SIZE_MAX + 0.375F,
			SIZE_MAX, SIZE_MAX);
	private static final AxisAlignedBB EAST = new AxisAlignedBB(SIZE_MIN - 0.375F, SIZE_MIN, SIZE_MIN, SIZE_MAX - 0.5F,
			SIZE_MAX, SIZE_MAX);
	private static final AxisAlignedBB NORTH = new AxisAlignedBB(SIZE_MIN, SIZE_MIN, SIZE_MIN + 0.5F, SIZE_MAX,
			SIZE_MAX, SIZE_MAX + 0.375F);
	private static final AxisAlignedBB SOUTH = new AxisAlignedBB(SIZE_MIN, SIZE_MIN, SIZE_MIN - 0.375F, SIZE_MAX,
			SIZE_MAX, SIZE_MAX - 0.5F);
	private static final AxisAlignedBB[] BBS = new AxisAlignedBB[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess blockAccess, BlockPos pos)
	{
		return BBS[state.getValue(FACING).getIndex()];
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.VALUES[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getIndex();
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighbourType, BlockPos neighbor)
	{
		// Checking here to see if the block we're attached to is valid (and
		// breaking if it isn't)
		switch (state.getValue(FACING))
		{
		case DOWN :
			if (!world.isSideSolid(pos.offset(EnumFacing.UP), EnumFacing.DOWN)) world.destroyBlock(pos, false);
			break;
		case EAST :
			if (!world.isSideSolid(pos.offset(EnumFacing.WEST), EnumFacing.EAST)) world.destroyBlock(pos, false);
			break;
		case NORTH :
			if (!world.isSideSolid(pos.offset(EnumFacing.SOUTH), EnumFacing.NORTH)) world.destroyBlock(pos, false);
			break;
		case SOUTH :
			if (!world.isSideSolid(pos.offset(EnumFacing.NORTH), EnumFacing.SOUTH)) world.destroyBlock(pos, false);
			break;
		case UP :
			if (!world.isSideSolid(pos.offset(EnumFacing.DOWN), EnumFacing.UP)) world.destroyBlock(pos, false);
			break;
		case WEST :
			if (!world.isSideSolid(pos.offset(EnumFacing.EAST), EnumFacing.WEST)) world.destroyBlock(pos, false);
			break;
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		// If this gets called then someone wants the light to turn back into
		// air, since the timer ran out
		if (!world.isRemote)
		{
			world.destroyBlock(pos, false);

			// SFX
			for (int i = 0; i < 8; ++i)
			{
				world.spawnParticle(EnumParticleTypes.SLIME, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Items.AIR;
	}
}
