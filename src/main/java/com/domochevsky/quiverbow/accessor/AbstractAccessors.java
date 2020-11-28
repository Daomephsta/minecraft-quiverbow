package com.domochevsky.quiverbow.accessor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public abstract class AbstractAccessors
{
    protected static MethodHandle createFieldGetter(Class<?> clazz, String srgName)
        throws IllegalAccessException, NoSuchFieldException, SecurityException
    {
        return MethodHandles.lookup().unreflectGetter(
            ObfuscationReflectionHelper.findField(clazz, srgName));
    }

    protected static MethodHandle createFieldSetter(Class<?> clazz, String srgName)
        throws IllegalAccessException, NoSuchFieldException, SecurityException
    {
        return MethodHandles.lookup().unreflectSetter(
            ObfuscationReflectionHelper.findField(clazz, srgName));
    }

    protected static MethodHandle createMethodInvoker(Class<?> clazz, String srgName,
        Class<?>... parameterTypes) throws IllegalAccessException, NoSuchMethodException, SecurityException
    {
        return MethodHandles.lookup().unreflect(
            ObfuscationReflectionHelper.findMethod(clazz, srgName, Void.TYPE, parameterTypes));
    }

    protected static MethodHandle createMethodInvoker(Class<?> clazz, Class<?> returnType, String srgName,
        Class<?>... parameterTypes) throws IllegalAccessException, NoSuchMethodException, SecurityException
    {
        return MethodHandles.lookup().unreflect(
            ObfuscationReflectionHelper.findMethod(clazz, srgName, returnType, parameterTypes));
    }
}
