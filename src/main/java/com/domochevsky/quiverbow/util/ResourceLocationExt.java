package com.domochevsky.quiverbow.util;

import java.util.List;

import com.google.common.base.Splitter;

import net.minecraft.util.ResourceLocation;

public class ResourceLocationExt
{
	public static final String PATH_SEPARATOR = "/";
	public static final char PATH_SEPARATOR_CHAR = '/';
	private static final Splitter PATH_SPLITTER = Splitter.on(PATH_SEPARATOR_CHAR);

	public static ResourceLocation withDomain(ResourceLocation base, String domain)
	{
		return new ResourceLocation(domain, base.getResourcePath());
	}

	public static ResourceLocation withPath(ResourceLocation base, String path)
	{
		return new ResourceLocation(base.getResourceDomain(), path);
	}

	public static ResourceLocation prefixPath(ResourceLocation base, String prefix)
	{
		return new ResourceLocation(base.getResourceDomain(), prefix + base.getResourcePath());
	}

	public static ResourceLocation suffixPath(ResourceLocation base, String suffix)
	{
		return new ResourceLocation(base.getResourceDomain(), base.getResourcePath() + suffix);
	}

	public static ResourceLocation addToPath(ResourceLocation base, String prefix, String suffix)
	{
		return new ResourceLocation(base.getResourceDomain(), prefix + base.getResourcePath() + suffix);
	}

	public static ResourceLocation subPath(ResourceLocation base, int start)
	{
		return subPath(base, start, -1);
	}

	public static ResourceLocation subPath(ResourceLocation base, int start, int end)
	{
		List<String> pathElements = PATH_SPLITTER.splitToList(base.getResourcePath());
		if (end == -1)
			end = pathElements.size();
		return new ResourceLocation(base.getResourceDomain(), String.join(PATH_SEPARATOR, pathElements.subList(start, end)));
	}
}
