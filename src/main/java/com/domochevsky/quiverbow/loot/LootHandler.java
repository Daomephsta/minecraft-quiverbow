package com.domochevsky.quiverbow.loot;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.armsassistant.IArmsAssistantUpgrade;
import com.domochevsky.quiverbow.armsassistant.UpgradeRegistry;
import com.domochevsky.quiverbow.config.QuiverbowConfig;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;

public class LootHandler
{
    public static final ResourceLocation ARMS_ASSISTANT_TABLE = new ResourceLocation(QuiverbowMain.MODID, "entities/arms_assistant");

    public static void initialise()
    {
        if (QuiverbowConfig.allowTurret)
        {
            LootConditionManager.registerCondition(new ArmAssistantHasUpgrade.Serialiser());
            LootTableList.register(ARMS_ASSISTANT_TABLE);
            LootTableList.register(new ResourceLocation(QuiverbowMain.MODID, "entities/arms_assistant/base"));
            for (IArmsAssistantUpgrade upgrade : UpgradeRegistry.getUpgrades())
                LootTableList.register(upgrade.getLootTable());
        }
    }
}
