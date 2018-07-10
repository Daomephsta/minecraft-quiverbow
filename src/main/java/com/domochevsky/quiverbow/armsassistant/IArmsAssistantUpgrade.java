package com.domochevsky.quiverbow.armsassistant;

import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public interface IArmsAssistantUpgrade
{
	public Multimap<String, AttributeModifier> getAttributeModifiers();
}
