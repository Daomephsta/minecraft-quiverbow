package com.domochevsky.quiverbow.armsassistant;

import java.util.function.BiConsumer;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

public class BasicUpgrade implements IArmsAssistantUpgrade
{
	private final ResourceLocation registryId, lootTable;
	private final String translationKey;

    public BasicUpgrade(ResourceLocation registryId)
    {
        this.registryId = registryId;
        this.lootTable = new ResourceLocation(registryId.getResourceDomain(), "entities/arms_assistant/upgrade_" + registryId.getResourcePath());
        this.translationKey = registryId.getResourceDomain() + ".arms_assistant.upgrade." + registryId.getResourcePath();
    }

    @Override
	public void submitAttributeModifiers(BiConsumer<String, AttributeModifier> out) {}

    @Override
    public ResourceLocation getRegistryId()
    {
        return registryId;
    }

    @Override
    public String getTranslationKey()
    {
        return translationKey;
    }

    @Override
    public ResourceLocation getLootTable()
    {
        return lootTable;
    }
}