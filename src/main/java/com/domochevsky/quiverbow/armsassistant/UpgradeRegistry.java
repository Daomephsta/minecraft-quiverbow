package com.domochevsky.quiverbow.armsassistant;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.ResourceLocation;

public class UpgradeRegistry
{
	private static final BiMap<ResourceLocation, IArmsAssistantUpgrade> ID_INSTANCE_MAP = HashBiMap.create();
	private static final Int2ObjectMap<IArmsAssistantUpgrade> INT_ID_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();
	private static final Object2IntMap<IArmsAssistantUpgrade> INSTANCE_INT_ID_MAP = new Object2IntOpenHashMap<>();
	private static int nextID = 0;
	
	public static final IArmsAssistantUpgrade EXTRA_WEAPON = register(
			new ResourceLocation(QuiverbowMain.MODID, "extra_weapon"), new BasicUpgrade());
	public static final IArmsAssistantUpgrade RIDING = register(new ResourceLocation(QuiverbowMain.MODID, "riding"),
			new BasicUpgrade());
	public static final IArmsAssistantUpgrade COMMUNICATIONS = register(
			new ResourceLocation(QuiverbowMain.MODID, "communications"), new BasicUpgrade());
	public static final IArmsAssistantUpgrade STORAGE = register(
		new ResourceLocation(QuiverbowMain.MODID, "storage"), new BasicUpgrade());
	public static final IArmsAssistantUpgrade MOBILITY = register(
		new ResourceLocation(QuiverbowMain.MODID, "mobility"), new BasicUpgrade());
	public static final IArmsAssistantUpgrade ARMOUR = register(
		new ResourceLocation(QuiverbowMain.MODID, "armour"), new BasicUpgrade());
	public static final IArmsAssistantUpgrade HEAVY_PLATING = register(
		new ResourceLocation(QuiverbowMain.MODID, "heavy_plating"), new BasicUpgrade());

	public static IArmsAssistantUpgrade register(ResourceLocation id, IArmsAssistantUpgrade upgradeInstance)
	{
		ID_INSTANCE_MAP.put(id, upgradeInstance);
		INT_ID_INSTANCE_MAP.put(nextID, upgradeInstance);
		INSTANCE_INT_ID_MAP.put(upgradeInstance, nextID++);
		return upgradeInstance;
	}

	public static IArmsAssistantUpgrade getUpgradeInstance(ResourceLocation id)
	{
		return ID_INSTANCE_MAP.get(id);
	}
	
	public static IArmsAssistantUpgrade getUpgradeInstance(int id)
	{
		return INT_ID_INSTANCE_MAP.get(id);
	}

	public static ResourceLocation getUpgradeID(IArmsAssistantUpgrade upgrade)
	{
		return ID_INSTANCE_MAP.inverse().get(upgrade);
	}
	
	public static int getUpgradeIntegerID(IArmsAssistantUpgrade upgrade)
	{
		return INSTANCE_INT_ID_MAP.get(upgrade);
	}
}
