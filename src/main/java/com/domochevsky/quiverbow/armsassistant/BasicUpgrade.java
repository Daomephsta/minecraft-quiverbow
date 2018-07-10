package com.domochevsky.quiverbow.armsassistant;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public class BasicUpgrade implements IArmsAssistantUpgrade
{
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers()
	{
		return ImmutableMultimap.of();
	}
}