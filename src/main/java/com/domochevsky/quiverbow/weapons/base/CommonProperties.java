package com.domochevsky.quiverbow.weapons.base;

import org.apache.commons.lang3.tuple.Pair;

public class CommonProperties
{
	public static final Pair<String, String> FIRE_DUR_ENTITY = Pair.of("fireDurationEntity", "The number of seconds the projectile sets entities on fire for");
	public static final Pair<String, String> SPREAD = Pair.of("spread", "How inaccurate this weapon is");
	public static final Pair<String, String> DESPAWN_TIME = Pair.of("despawnTicks", "How long it takes for projectiles to despawn");
	public static final Pair<String, String> DAMAGE_TERRAIN = Pair.of("damageTerrain", "If true this weapon can damage terrain when used by a player");
	public static final Pair<String, String> EXPLOSION_SIZE = Pair.of("explosionSize", "How large the explosion is in blocks. A TNT explosion is 4.0 blocks");
	public static final Pair<String, String> SHOULD_DROP = Pair.of("shouldDrop", "If true projectiles will drop their item equivalent if they don't hit an entity");
	public static final Pair<String, String> WITHER_STRENGTH = Pair.of("witherStrength", "The strength of the Wither effect applied");
	public static final Pair<String, String> WITHER_DUR = Pair.of("witherDur", "The duration in ticks of the Wither effect applied");
	public static final Pair<String, String> NAUSEA_DUR = Pair.of("nauseaDur", "The duration in ticks of the Nausea effect applied");
	public static final Pair<String, String> SLOWNESS_STRENGTH = Pair.of("slownessStrength", "The strength of the Slowness effect applied");
	public static final Pair<String, String> SLOWNESS_DUR = Pair.of("slownessDur", "The duration of the Slowness effect applied");
	public static final Pair<String, String> MAX_ZOOM = Pair.of("maxZoom", "How far this weapon can zoom in. Larger numbers mean lower maximum zoom");
}
