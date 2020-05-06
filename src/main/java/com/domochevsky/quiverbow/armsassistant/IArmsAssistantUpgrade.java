package com.domochevsky.quiverbow.armsassistant;

import java.util.function.BiConsumer;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public interface IArmsAssistantUpgrade
{
    public void submitAttributeModifiers(BiConsumer<String, AttributeModifier> out);
}
