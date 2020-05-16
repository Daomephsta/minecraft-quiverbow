package com.domochevsky.quiverbow.armsassistant;

import java.util.function.BiConsumer;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

public interface IArmsAssistantUpgrade
{
    public void submitAttributeModifiers(BiConsumer<String, AttributeModifier> out);
    public ResourceLocation getRegistryId();
    /**@return the id of a loot table that specifies what should be dropped when an AA with this upgrade dies*/
    public ResourceLocation getLootTable();
}
