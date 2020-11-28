package com.domochevsky.quiverbow.config;

import java.io.File;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.projectiles.SoulShot;
import com.domochevsky.quiverbow.weapons.base.Weapon;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = QuiverbowMain.MODID)
public class QuiverbowConfig
{
	/* If this is false then projectiles can't break blocks (Don't care about
	 * TNT) */
	public static boolean breakGlass;
	// If this is false inventory sprites are used in hand too.
	public static boolean useModels;
	// If this is true then disabled weapons won't show up in the creative menu
	public static boolean noCreative;
	// If this is false then the Arms Assistant will not be available
	public static boolean allowTurret;
	/* If this is false then the AA is not allowed to attack players (ignores
	 * them) */
	public static boolean allowTurretPlayerAttacks;
	// If this is true then turret targeting range is limited to 32 blocks
	public static boolean restrictTurretRange;
	/* If this is false then Helper.tryBlockBreak() won't send a BlockBreak
	 * event. Used by protection plugins. */
	public static boolean sendBlockBreak;
	// How fast scoped weapons zoom in and out. Smaller numbers zoom faster.
	public static float zoomSpeed;

	private static Configuration config;

	public static void load(File suggestedFile)
	{
		config = new Configuration(suggestedFile);

		config.load();
		//Load general config properties. The # makes the sorter put it at the top
		breakGlass = config.getBoolean("breakGlass", "#general", true,
				"Can we break glass and other fragile things with our projectiles?");
		useModels = config.getBoolean("useModels", "#general", true, "Are we using models or icons for held weapons?");
		noCreative = config.getBoolean("noCreative", "#general", false,
				"Are we removing disabled weapons from the creative menu too?");
		allowTurret = config.getBoolean("allowTurret", "#general", true, "Disables AA crafting if false");
		allowTurretPlayerAttacks = config.getBoolean("allowTurretPlayerAttacks", "#general", true,
				"If this is false then AAs will not attack players");
		restrictTurretRange = config.getBoolean("restrictTurretRange", "#general", true,
				"If this is true then AA targeting range is limited to 32 blocks");
		sendBlockBreak = config.getBoolean("sendBlockBreak", "#general", true,
				"Do we send a BlockBreak event when breaking things with our projectiles?");
		zoomSpeed = config.getFloat("zoomSpeed", "#general", 15.0F, 1.0F, 100.0F, "How fast scoped weapons zoom in and out. Smaller numbers zoom faster.");
		String[] soulCairnBlacklistStr = config.getStringList("soulCairnBlacklist", "#general", new String[0], "If an entity has its registry name in this list, the soul cairn cannot capture it.");
		for(String entry : soulCairnBlacklistStr)
		{
			SoulShot.blacklistEntity(new ResourceLocation(entry));
		}
		config.save();
	}

	public static void loadWeaponProperties()
	{
		for (Weapon weapon : QuiverbowMain.weapons)
            weapon.getProperties().loadFromConfig(config.getCategory(weapon.getRegistryName().getResourcePath()));
		config.save();
	}

	@SubscribeEvent
	public static void syncConfig(ConfigChangedEvent e)
	{
		if (e.getModID().equals(QuiverbowMain.MODID)) ConfigManager.sync(QuiverbowMain.MODID, Config.Type.INSTANCE);
	}
}
