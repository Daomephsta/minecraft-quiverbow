package com.domochevsky.quiverbow.armsassistant;

import java.util.function.BiConsumer;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public class BasicUpgrade implements IArmsAssistantUpgrade
{
	@Override
	public void submitAttributeModifiers(BiConsumer<String, AttributeModifier> out) {}
}