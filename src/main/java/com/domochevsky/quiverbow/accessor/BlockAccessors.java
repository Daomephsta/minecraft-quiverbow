package com.domochevsky.quiverbow.accessor;

import java.lang.invoke.MethodHandle;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class BlockAccessors extends AbstractAccessors
{
    private static final MethodHandle Block$getSilkTouchDrop;
    static
    {
        try
        {
            Block$getSilkTouchDrop = createMethodInvoker(Block.class, ItemStack.class, "func_180643_i", IBlockState.class);
        }
        catch (Throwable t)
        {
            throw new RuntimeException("Failed to initialize Block accessor method handles", t);
        }
    }

    public static ItemStack getSilkTouchDrop(IBlockState state)
    {
        try
        {
            return (ItemStack) Block$getSilkTouchDrop.invokeExact(state.getBlock(), state);
        }
        catch (Throwable e)
        {
            throw new RuntimeException("Could not invoke Block silk touch drop getter method handle", e);
        }
    }
}
