package com.domochevsky.quiverbow.util;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraftforge.common.util.Constants.NBT;

public class NBTPrimitiveStreams
{
	public static IntStream toByteStream(NBTTagList list)
	{
		checkTagType(list, NBT.TAG_BYTE);
		return IntStream.range(0, list.tagCount()).map(list::getIntAt);
	}

	public static IntStream toShortStream(NBTTagList list)
	{
		checkTagType(list, NBT.TAG_SHORT);
		return IntStream.range(0, list.tagCount()).map(list::getIntAt);
	}

	public static IntStream toIntStream(NBTTagList list)
	{
		checkTagType(list, NBT.TAG_INT);
		return IntStream.range(0, list.tagCount()).map(list::getIntAt);
	}

	public static LongStream toLongStream(NBTTagList list)
	{
		checkTagType(list, NBT.TAG_LONG);
		return Streams.stream(list).mapToLong(tag -> ((NBTTagLong) tag).getLong());
	}

	public static DoubleStream toFloatStream(NBTTagList list)
	{
		checkTagType(list, NBT.TAG_FLOAT);
		return IntStream.range(0, list.tagCount()).mapToDouble(list::getDoubleAt);
	}

	public static DoubleStream toDoubleStream(NBTTagList list)
	{
		checkTagType(list, NBT.TAG_DOUBLE);
		return IntStream.range(0, list.tagCount()).mapToDouble(list::getDoubleAt);
	}

	public static Stream<byte[]> toByteArrayStream(NBTTagList list)
	{
		checkTagType(list, NBT.TAG_BYTE_ARRAY);
		return Streams.stream(list).map(tag -> ((NBTTagByteArray) tag).getByteArray());
	}

	public static Stream<String> toStringStream(NBTTagList list)
	{
		checkTagType(list, NBT.TAG_STRING);
		return IntStream.range(0, list.tagCount()).mapToObj(list::getStringTagAt);
	}

	public static Stream<int[]> toIntArrayStream(NBTTagList list)
	{
		checkTagType(list, NBT.TAG_BYTE_ARRAY);
		return IntStream.range(0, list.tagCount()).mapToObj(list::getIntArrayAt);
	}

	/* Can't exist because Mojang's toolchain strips NBTTagLongArray#getLongArray()
	 * as it's unused. See https://github.com/MinecraftForge/MinecraftForge/pull/5080
	 * public static Stream<long[]> toLongArrayStream(NBTTagList list)
	 * {
	 *     checkTagType(list, NBT.TAG_BYTE_ARRAY);
	 *     return Streams.stream(list).map(tag -> ((NBTTagLongArray) tag).getLongArray());
	 * }
	 */

	private static void checkTagType(NBTTagList list, int expectedType)
	{
		if (list.getTagType() != expectedType)
		{
			throw new IllegalArgumentException(String.format("Expected type to be %s, was %s",
				NBTBase.getTagTypeName(expectedType), NBTBase.getTagTypeName(list.getTagType())));
		}
	}
}
