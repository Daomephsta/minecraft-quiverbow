package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.LargeRocket;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RPG_Imp extends RPG
{
	public RPG_Imp()
	{
		super("rocket_launcher_imp", 1);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);
		this.Speed = config.get(this.name, "How fast are my projectiles? (default 2.0 BPT (Blocks Per Tick))", 2.0)
				.getDouble();
		this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
				.getInt();
		this.ExplosionSize = config.get(this.name, "How big are my explosions? (default 4.0 blocks, like TNT)", 4.0)
				.getDouble();
		this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true)
				.getBoolean(true);
		this.travelTime = config
				.get(this.name, "How many ticks can my rocket fly before exploding? (default 20 ticks)", 20).getInt();
		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Improved Rocket Launcher (empty)
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "xxx", "yzy", "xxx", 'x',
					Blocks.OBSIDIAN, // Adding an obsidian frame to the RPG
					'y', Items.IRON_INGOT, 'z', Helper.getWeaponStackByClass(RPG.class, true));
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		// Fill the launcher with 1 big rocket
		GameRegistry.addShapelessRecipe(new ItemStack(this), Helper.getAmmoStack(LargeRocket.class, 0),
				new ItemStack(this, 1, 1));
	}
}
