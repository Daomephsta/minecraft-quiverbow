package com.domochevsky.quiverbow.util;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;

import com.google.common.base.Preconditions;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTCollectors
{
	/**@author InsomniaKitten*/
	public static <T extends NBTBase> Collector<Map.Entry<String, T>, ?, NBTTagCompound> toCompoundTag() {
        return Collector.of(NBTTagCompound::new, (c, e) -> c.setTag(e.getKey(), e.getValue()), NBTCollectors::merge);
    }

	/**@author InsomniaKitten*/
    public static <T extends NBTBase, K> Collector<Map.Entry<K, T>, ?, NBTTagCompound> toCompoundTag(final KeySupplier<K> keySupplier) {
        return Collector.of(NBTTagCompound::new, (c, e) -> c.setTag(keySupplier.apply(e.getKey()), e.getValue()), NBTCollectors::merge);
    }

    /**@author InsomniaKitten*/
    public static <T extends NBTBase, V> Collector<Map.Entry<String, V>, ?, NBTTagCompound> toCompoundTag(final Function<V, T> mappingFunction) {
        return Collector.of(NBTTagCompound::new, (c, e) -> c.setTag(e.getKey(), mappingFunction.apply(e.getValue())), NBTCollectors::merge);
    }

    /**@author InsomniaKitten*/
    public static <T extends NBTBase, K, V> Collector<Map.Entry<K, V>, ?, NBTTagCompound> toCompoundTag(final KeySupplier<K> keySupplier, final Function<V, T> mappingFunction) {
        return Collector.of(NBTTagCompound::new, (c, e) -> {
            final String k = keySupplier.apply(e.getKey());
            final T v = mappingFunction.apply(e.getValue());
            c.setTag(k, v);
        }, NBTCollectors::merge);
    }

	public static <T extends NBTBase> Collector<T, ?, NBTTagList> toNBTList(Class<T> expectedClass)
	{
		return Collector.of(NBTTagList::new,
			appendWithExpectedType(expectedClass), NBTCollectors::merge);
	}

	/**
     * Delegating call to {@link NBTTagCompound#merge(NBTTagCompound)} that then returns
     * the {@code first} compound tag being mutated in the context
	 * @author InsomniaKitten
     *
     * @param first The compound tag to be mutated and returned
     * @param second The compound tag to be merged into the {@code first}
     * @return The mutated {@code first} compound tag
     */
    public static NBTTagCompound merge(final NBTTagCompound first, final NBTTagCompound second) {
        first.merge(second);
        return first;
    }

    /**
     * Merges the {@code second} {@link NBTTagList} into the {@code first} {@link NBTTagList}
     * @author InsomniaKitten
     *
     * @param first The list tag to be mutated and returned
     * @param second The list tag to be merged into the {@code first}
     * @return The mutated {@code first} list tag
     */
    public static NBTTagList merge(final NBTTagList first, final NBTTagList second) {
        for (final NBTBase tag : second) {
            first.appendTag(tag);
        }
        return first;
    }

    /**
     * Creates an accumulator with preconditions to expect the given {@code type}
     * @author InsomniaKitten
     *
     * @param t The {@link Class} type to expect when accumulating values
     * @param <T> The generic {@code type} inferred from the given {@link Class}
     * @return A new accumulator in the form of a {@link BiConsumer}
     */
    private static <T extends NBTBase> BiConsumer<NBTTagList, T> appendWithExpectedType(final Class<T> t) {
        return (l, e) -> {
            Preconditions.checkState(t.isInstance(e), "Expected type %s but received %s: %s", t, e.getClass(), e);
            l.appendTag(e);
        };
    }

	public interface KeySupplier<K> extends Function<K, String> {}
}
