package com.domochevsky.quiverbow.util;

import java.util.regex.Pattern;

import net.minecraft.client.resources.I18n;

public class Newliner
{
    private static final Pattern NEWLINE_MATCHER = Pattern.compile("#n");
    
    public static String[] parse(String key)
    {
	return NEWLINE_MATCHER.split(key);
    }
    
    public static String[] translateAndParse(String key, Object... args)
    {
	return NEWLINE_MATCHER.split(I18n.format(key, args));
    }
}
