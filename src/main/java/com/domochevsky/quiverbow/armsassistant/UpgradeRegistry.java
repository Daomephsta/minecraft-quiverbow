package com.domochevsky.quiverbow.armsassistant;

import java.util.function.BiConsumer;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import daomephsta.umbra.entity.attributes.AttributeHelper;
import daomephsta.umbra.entity.attributes.AttributeHelper.AttributeModifierOperation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

public class UpgradeRegistry
{
	private static final BiMap<ResourceLocation, IArmsAssistantUpgrade> ID_INSTANCE_MAP = HashBiMap.create();
	private static final Int2ObjectMap<IArmsAssistantUpgrade> INT_ID_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();
	private static final Object2IntMap<IArmsAssistantUpgrade> INSTANCE_INT_ID_MAP = new Object2IntOpenHashMap<>();
	private static int nextID = 0;

	public static final IArmsAssistantUpgrade EXTRA_WEAPON = register(new BasicUpgrade(new ResourceLocation(QuiverbowMain.MODID, "extra_weapon")));
	public static final IArmsAssistantUpgrade RIDING = register(new BasicUpgrade(new ResourceLocation(QuiverbowMain.MODID, "riding")));
	public static final IArmsAssistantUpgrade COMMUNICATIONS = register(new BasicUpgrade(new ResourceLocation(QuiverbowMain.MODID, "communications")));
	public static final IArmsAssistantUpgrade STORAGE = register(new BasicUpgrade(new ResourceLocation(QuiverbowMain.MODID, "storage")));
	public static final IArmsAssistantUpgrade MOBILITY = register(new BasicUpgrade(new ResourceLocation(QuiverbowMain.MODID, "mobility"))
	    {
	        @Override
	        public void submitAttributeModifiers(BiConsumer<String, AttributeModifier> out)
	        {
	            out.accept(SharedMonsterAttributes.MOVEMENT_SPEED.getName(),
	                AttributeHelper.createModifier("Mobility upgrade", 0.5D, AttributeModifierOperation.ADDITIVE));
	        }
	    });
	public static final IArmsAssistantUpgrade ARMOUR = register(new BasicUpgrade(new ResourceLocation(QuiverbowMain.MODID, "armour"))
	    {
	        @Override
            public void submitAttributeModifiers(BiConsumer<String,AttributeModifier> out)
	        {
	            out.accept(SharedMonsterAttributes.MAX_HEALTH.getName(),
	                AttributeHelper.createModifier("Health upgrade", 20.0D, AttributeModifierOperation.ADDITIVE));
	        };
	    });
	public static final IArmsAssistantUpgrade HEAVY_PLATING = register(new BasicUpgrade(new ResourceLocation(QuiverbowMain.MODID, "heavy_plating"))
        {
            @Override
            public void submitAttributeModifiers(BiConsumer<String,AttributeModifier> out)
            {
                out.accept(SharedMonsterAttributes.ARMOR.getName(),
                    AttributeHelper.createModifier("Armour upgrade", 3.0D, AttributeModifierOperation.ADDITIVE));
                out.accept(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(),
                    AttributeHelper.createModifier("Armour upgrade", 0.5D, AttributeModifierOperation.ADDITIVE));
            };
        });

	public static IArmsAssistantUpgrade register(IArmsAssistantUpgrade upgradeInstance)
	{
		ID_INSTANCE_MAP.put(upgradeInstance.getRegistryId(), upgradeInstance);
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

	public static Iterable<IArmsAssistantUpgrade> getUpgrades()
	{
	    return ID_INSTANCE_MAP.values();
	}
}
