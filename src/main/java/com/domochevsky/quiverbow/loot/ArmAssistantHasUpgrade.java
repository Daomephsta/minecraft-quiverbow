package com.domochevsky.quiverbow.loot;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.armsassistant.EntityArmsAssistant;
import com.domochevsky.quiverbow.armsassistant.IArmsAssistantUpgrade;
import com.domochevsky.quiverbow.armsassistant.UpgradeRegistry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class ArmAssistantHasUpgrade implements LootCondition
{
    private static final Logger LOGGER = LogManager.getLogger();
    private IArmsAssistantUpgrade upgrade;

    private ArmAssistantHasUpgrade(IArmsAssistantUpgrade upgrade)
    {
        this.upgrade = upgrade;
    }

    @Override
    public boolean testCondition(Random rand, LootContext context)
    {
        if (context.getLootedEntity() instanceof EntityArmsAssistant)
            return ((EntityArmsAssistant) context.getLootedEntity()).hasUpgrade(upgrade);
        LOGGER.error("{} expected looted entity to be an Arms Assistant, was {}", QuiverbowMain.MODID + ":arms_assistant_upgrade", context.getLootedEntity());
        return false;
    }

    public static class Serialiser extends LootCondition.Serializer<ArmAssistantHasUpgrade>
    {
        public Serialiser()
        {
            super(new ResourceLocation(QuiverbowMain.MODID, "arms_assistant_upgrade"), ArmAssistantHasUpgrade.class);
        }

        @Override
        public void serialize(JsonObject json, ArmAssistantHasUpgrade value, JsonSerializationContext context)
        {
            json.add("upgrade", context.serialize(UpgradeRegistry.getUpgradeID(value.upgrade)));
        }

        @Override
        public ArmAssistantHasUpgrade deserialize(JsonObject json, JsonDeserializationContext context)
        {
            ResourceLocation upgradeId = new ResourceLocation(JsonUtils.getString(json, "upgrade"));
            IArmsAssistantUpgrade upgradeInstance = UpgradeRegistry.getUpgradeInstance(upgradeId);
            if (upgradeInstance == null)
                throw new JsonSyntaxException("Invalid upgrade ID " + upgradeId);
            return new ArmAssistantHasUpgrade(upgradeInstance);
        }
    }
}
