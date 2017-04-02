package com.domochevsky.quiverbow.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

public class Newliner
{
    public static String[] parse(String key)
    {
	return key.split("#n");
    }
    
    public static String[] translateAndParse(String key, Object... args)
    {
	return I18n.format(key, args).split("#n");
    }
}
