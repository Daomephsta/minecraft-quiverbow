package com.domochevsky.quiverbow.blocks;

import java.util.Random;

import com.domochevsky.quiverbow.Main;

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
	world.setBlockState(pos, Main.fenLight.getDefaultState().withProperty(FACING, facing));
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
	return null;
    }

    private static final AxisAlignedBB DOWN = new AxisAlignedBB(SIZE_MIN, SIZE_MIN + 0.375F, SIZE_MIN, SIZE_MAX,
	    SIZE_MAX + 0.375F, SIZE_MAX);
    private static final AxisAlignedBB UP = new AxisAlignedBB(SIZE_MIN, SIZE_MIN - 0.375F, SIZE_MIN, SIZE_MAX,
	    SIZE_MAX - 0.375F, SIZE_MAX);
    private static final AxisAlignedBB NORTH = new AxisAlignedBB(SIZE_MIN + 0.375F, SIZE_MIN, SIZE_MIN,
	    SIZE_MAX + 0.375F, SIZE_MAX, SIZE_MAX);
    private static final AxisAlignedBB SOUTH = new AxisAlignedBB(SIZE_MIN - 0.375F, SIZE_MIN, SIZE_MIN,
	    SIZE_MAX - 0.375F, SIZE_MAX, SIZE_MAX);
    private static final AxisAlignedBB EAST = new AxisAlignedBB(SIZE_MIN, SIZE_MIN, SIZE_MIN + 0.375F, SIZE_MAX,
	    SIZE_MAX, SIZE_MAX + 0.375F);
    private static final AxisAlignedBB WEST = new AxisAlignedBB(SIZE_MIN, SIZE_MIN, SIZE_MIN - 0.375F, SIZE_MAX,
	    SIZE_MAX, SIZE_MAX - 0.375F);

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
	switch (state.getValue(FACING))
	{
	case DOWN:
	    return DOWN;
	case EAST:
	    return EAST;
	case NORTH:
	    return NORTH;
	case SOUTH:
	    return SOUTH;
	case UP:
	    return UP;
	case WEST:
	    return WEST;
	default:
	    return FULL_BLOCK_AABB;
	}
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
	return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
	return new BlockStateContainer(this, FACING);
    }

    @Override
    public void onNeighborChange(IBlockAccess blockAccess, BlockPos pos, BlockPos neighbor)
    {
	// Ensure the blockAccess is a world, not a chunkcache
	if (!(blockAccess instanceof World)) return;
	World world = (World) blockAccess;

	// Checking here to see if the block we're attached to is valid (and
	// breaking if it isn't)
	IBlockState state = world.getBlockState(pos);
	switch (state.getValue(FACING))
	{
	case DOWN:
	    if (world.isSideSolid(pos.offset(EnumFacing.UP), EnumFacing.DOWN)) world.setBlockToAir(pos);
	    break;
	case EAST:
	    if (world.isSideSolid(pos.offset(EnumFacing.WEST), EnumFacing.EAST)) world.setBlockToAir(pos);
	    break;
	case NORTH:
	    if (world.isSideSolid(pos.offset(EnumFacing.SOUTH), EnumFacing.NORTH)) world.setBlockToAir(pos);
	    break;
	case SOUTH:
	    if (world.isSideSolid(pos.offset(EnumFacing.NORTH), EnumFacing.SOUTH)) world.setBlockToAir(pos);
	    break;
	case UP:
	    if (world.isSideSolid(pos.offset(EnumFacing.DOWN), EnumFacing.UP)) world.setBlockToAir(pos);
	    break;
	case WEST:
	    if (world.isSideSolid(pos.offset(EnumFacing.EAST), EnumFacing.WEST)) world.setBlockToAir(pos);
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
	    world.setBlockToAir(pos);

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
