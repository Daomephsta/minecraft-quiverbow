package com.domochevsky.quiverbow.weapons.base;

public class CommonProperties
{
	public static final String PROP_FIRE_DUR_ENTITY = "fireDurationEntity",
			COMMENT_FIRE_DUR_ENTITY = "The number of seconds the projectile sets entities on fire for";
	public static final String PROP_SPREAD = "spread", COMMENT_SPREAD = "How inaccurate this weapon is";
	public static final String PROP_DESPAWN_TIME = "despawnTicks",
			COMMENT_DESPAWN_TIME = "How long it takes for projectiles to despawn";
	public static final String PROP_DAMAGE_TERRAIN = "damageTerrain",
			COMMENT_DAMAGE_TERRAIN = "If true this weapon can damage terrain when used by a player";
	public static final String PROP_EXPLOSION_SIZE = "explosionSize",
			COMMENT_EXPLOSION_SIZE = "How large the explosion is in blocks. A TNT explosion is 4.0 blocks";
	public static final String PROP_SHOULD_DROP = "shouldDrop",
			COMMENT_SHOULD_DROP = "If true projectiles will drop their item equivalent if they don't hit an entity";
	public static final String PROP_WITHER_STRENGTH = "witherStrength",
			COMMENT_WITHER_STRENGTH = "The strength of the Wither effect applied";
	public static final String PROP_WITHER_DUR = "witherDur",
			COMMENT_WITHER_DUR = "The duration in ticks of the Wither effect applied";
	public static final String PROP_NAUSEA_DUR = "nauseaDur",
			COMMENT_NAUSEA_DUR = "The duration in ticks of the Nausea effect applied";
	public static final String PROP_SLOWNESS_STRENGTH = "slownessStrength",
			COMMENT_SLOWNESS_STRENGTH = "The strength of the Slowness effect applied";
	public static final String PROP_SLOWNESS_DUR = "slownessDur",
			COMMENT_SLOWNESS_DUR = "The duration of the Slowness effect applied";
	public static final String PROP_MAX_ZOOM = "maxZoom",
			COMMENT_MAX_ZOOM = "How far this weapon can zoom in. Larger numbers mean lower maximum zoom";
}
