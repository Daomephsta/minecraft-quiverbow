package com.domochevsky.quiverbow.weapons;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class RPGImp extends RPG
{
	public RPGImp()
	{
		super("rocket_launcher_imp", 1);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);
		this.speed = config.get(this.name, "How fast are my projectiles? (default 2.0 BPT (Blocks Per Tick))", 2.0)
				.getDouble();
		this.kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3)
				.getInt();
		this.explosionSize = config.get(this.name, "How big are my explosions? (default 4.0 blocks, like TNT)", 4.0)
				.getDouble();
		this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true)
				.getBoolean(true);
		this.travelTime = config
				.get(this.name, "How many ticks can my rocket fly before exploding? (default 20 ticks)", 20).getInt();
		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}
}
