package com.domochevsky.quiverbow;

import java.util.ArrayList;

import com.domochevsky.quiverbow.Main.Constants;
import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.ammo.*;
import com.domochevsky.quiverbow.blocks.FenLight;
import com.domochevsky.quiverbow.miscitems.*;
import com.domochevsky.quiverbow.net.PacketHandler;
import com.domochevsky.quiverbow.projectiles.*;
import com.domochevsky.quiverbow.recipes.*;
import com.domochevsky.quiverbow.util.RegistryHelper;
import com.domochevsky.quiverbow.weapons.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.oredict.RecipeSorter;

@Mod(modid = Constants.MODID, name = Constants.NAME, version = "b102")
public class Main
{
    public static class Constants
    {
	public static final String MODID = "quiverchevsky";
	public static final String NAME = "QuiverBow";
    }

    @Instance(Constants.MODID)
    public static Main instance;

    @SidedProxy(clientSide = "com.domochevsky.quiverbow.ClientProxy", serverSide = "com.domochevsky.quiverbow.CommonProxy")
    public static CommonProxy proxy;

    protected Configuration config; // Accessible from other files this way

    public static ArrayList<_WeaponBase> weapons = new ArrayList<_WeaponBase>(); // Holder
    // array
    // for
    // all
    // (fully
    // set
    // up)
    // possible
    // weapons
    public static ArrayList<_AmmoBase> ammo = new ArrayList<_AmmoBase>(); // Same
    // with
    // ammo,
    // since
    // they
    // got
    // recipes
    // as
    // well

    private static int projectileCount = 1; // A running number, to register
    // projectiles by

    // Config
    public static boolean breakGlass; // If this is false then we're not allowed
    // to break blocks with projectiles (Don't
    // care about TNT)
    public static boolean useModels; // If this is false then we're reverting
    // back to held icons
    public static boolean noCreative; // If this is true then disabled weapons
    // won't show up in the creative menu
    // either
    public static boolean allowTurret; // If this is false then the Arms
    // Assistant will not be available
    public static boolean allowTurretPlayerAttacks; // If this is false then the
    // AA is not allowed to
    // attack players (ignores
    // them)
    public static boolean restrictTurretRange; // If this is false then we're
    // not capping the targeting
    // range at 32 blocks
    public static boolean sendBlockBreak; // If this is false then
    // Helper.tryBlockBreak() won't send a
    // BlockBreak event. Used by
    // protection plugins.

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
	this.config = new Configuration(event.getSuggestedConfigurationFile()); // Starting
	// config

	this.config.load(); // And loading it up

	breakGlass = this.config.get("generic",
		"Can we break glass and other fragile things with our projectiles? (default true)", true).getBoolean();
	sendBlockBreak = this.config
		.get("generic",
			"Do we send a BlockBreak event when breaking things with our projectiles? (default true)", true)
		.getBoolean();
	useModels = this.config.get("generic",
		"Are we using models or icons for held weapons? (default true for models. False for icons)", true)
		.getBoolean();
	noCreative = this.config.get("generic",
		"Are we removing disabled weapons from the creative menu too? (default false. On there, but uncraftable)",
		false).getBoolean();

	allowTurret = this.config.get("Arms Assistant", "Am I enabled? (default true)", true).getBoolean();
	restrictTurretRange = this.config.get("Arms Assistant",
		"Is my firing range limited to a maximum of 32 blocks? (default true. Set false for 'Shoot as far as your weapon can handle'.)",
		true).getBoolean();

	this.registerProjectiles();

	addAllProps(event, this.config); // All items are registered now. Making
	// recipes and recording props

	this.config.save(); // Done with config, saving it

	PacketHandler.initPackets(); // Used for sending particle packets, so I
	// can do my thing purely on the server
	// side

	// Registering the Arms Assistant
	EntityRegistry.registerModEntity(new ResourceLocation(Constants.MODID, "turret"), Entity_AA.class, "turret", 0,
		this, 80, 1, true);
	// EntityRegistry.registerModEntity(Entity_BB.class,
	// "quiverchevsky_flyingBB", 1, this, 80, 1, true);

	proxy.registerTurretRenderer();

	RecipeSorter.register(Constants.MODID + ":ender_rail_accelerator", Recipe_ERA.class,
		RecipeSorter.Category.SHAPED, "after:minecraft:shapeless");
	RecipeSorter.register(Constants.MODID + ":era_upgrade", Recipe_Weapon.class, RecipeSorter.Category.SHAPED,
		"after:minecraft:shapeless");
	RecipeSorter.register(Constants.MODID + ":load_magazine", RecipeLoadMagazine.class,
		RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

	Listener listener = new Listener();

	MinecraftForge.EVENT_BUS.register(listener);

	if (event.getSide().isServer())
	{
	    return;
	} // Client-only from this point.

	ListenerClient listenerClient = new ListenerClient();

	MinecraftForge.EVENT_BUS.register(listenerClient);
    }



    void registerProjectiles() // Entities that get shot out of weapons as
    // projectiles
    {
	this.addProjectile(RegularArrow.class, true, "arrow");
	this.addProjectile(BlazeShot.class, true, "blaze");
	this.addProjectile(CoinShot.class, true, "coin");
	this.addProjectile(SmallRocket.class, true, "rocket_small");

	this.addProjectile(LapisShot.class, true, "lapis");
	this.addProjectile(Thorn.class, true, "thorn");
	this.addProjectile(ProxyThorn.class, true, "proximity_thorn");
	this.addProjectile(SugarRod.class, true, "sugar");

	this.addProjectile(BigRocket.class, true, "rocket_big");

	this.addProjectile(Sabot_Arrow.class, true, "sabot_arrow");
	this.addProjectile(Sabot_Rocket.class, true, "sabot_rocket");

	this.addProjectile(Seed.class, true, "seed");
	this.addProjectile(PotatoShot.class, true, "potato");
	this.addProjectile(SnowShot.class, true, "snow");

	this.addProjectile(ScopedPredictive.class, true, "predictive");
	this.addProjectile(EnderShot.class, true, "ender");
	this.addProjectile(ColdIron.class, true, "cold_iron");

	this.addProjectile(OSP_Shot.class, true, "osp_shot");
	this.addProjectile(OSR_Shot.class, true, "osr_shot");
	this.addProjectile(OWR_Shot.class, true, "owr_shot");

	this.addProjectile(FenGoop.class, true, "fen_light");
	this.addProjectile(FlintDust.class, true, "flint_dust");

	this.addProjectile(RedLight.class, true, "red_light");
	this.addProjectile(SunLight.class, true, "sunlight");

	this.addProjectile(NetherFire.class, true, "nether_fire");
	this.addProjectile(RedSpray.class, true, "red_spray");

	this.addProjectile(SoulShot.class, true, "soul");

	this.addProjectile(WaterShot.class, true, "water");
	this.addProjectile(WebShot.class, true, "web");

	this.addProjectile(HealthBeam.class, true, "health");

	this.addProjectile(EnderAccelerator.class, true, "era_shot");
	this.addProjectile(EnderAno.class, true, "ano");
    }

    private void addProjectile(Class<? extends Entity> entityClass, boolean hasRenderer, String name)
    {
	EntityRegistry.registerModEntity(new ResourceLocation(Constants.MODID, name), entityClass,
		"projectilechevsky_" + name, projectileCount, this, 80, 1, true);

	if (hasRenderer)
	{
	    proxy.registerProjectileRenderer(entityClass);
	} // Entity-specific renderer

	projectileCount += 1;
    }

    // Adding props and recipes for all registered weapons now
    private static void addAllProps(FMLPreInitializationEvent event, Configuration config)
    {
	// Ammo first
	for (_AmmoBase ammunition : ammo)
	{
	    ammunition.addRecipes();
	}

	// Weapons last
	for (_WeaponBase weapon : weapons)
	{
	    weapon.addProps(event, config);
	    weapon.addRecipes();
	}
    }

    @Mod.EventBusSubscriber(modid = Constants.MODID)
    public static class RegistryHandler
    {
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e)
	{
	    e.getRegistry().register(RegistryHelper.registerBlock(new FenLight(Material.GLASS), "fen_light"));
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e)
	{
	    registerMiscItems(e.getRegistry());
	    registerWeapons(e.getRegistry());
	    registerAmmo(e.getRegistry());
	}

	private static void registerMiscItems(IForgeRegistry<Item> registry)
	{
	    registry.registerAll(RegistryHelper.registerItem(new Part_GatlingBody(), ".misc.", "part_sugar_engine_body"),
		    RegistryHelper.registerItem(new Part_GatlingBarrel(), ".misc.", "part_sugar_engine_barrel"),
		    RegistryHelper.registerItem(new PackedUpAA(), ".misc.", "arms_assistant"));
	}

	private static void registerWeapons(IForgeRegistry<Item> registry) // The weapons themselves
	{
	    registry.registerAll(addWeapon(new Crossbow_Compact()),
		    addWeapon(new Crossbow_Double()),
		    addWeapon(new Crossbow_Blaze()),
		    addWeapon(new Crossbow_Auto()),
		    addWeapon(new Crossbow_AutoImp()),
		    addWeapon(new CoinTosser()),
		    addWeapon(new CoinTosser_Mod()),
		    addWeapon(new DragonBox()),
		    addWeapon(new DragonBox_Quad()),
		    addWeapon(new LapisCoil()),
		    addWeapon(new ThornSpitter()),
		    addWeapon(new ProximityNeedler()),
		    addWeapon(new SugarEngine()),
		    addWeapon(new RPG()),
		    addWeapon(new RPG_Imp()),
		    addWeapon(new Mortar_Arrow()),
		    addWeapon(new Mortar_Dragon()),
		    addWeapon(new Seedling()),
		    addWeapon(new Potatosser()),
		    addWeapon(new SnowCannon()),
		    addWeapon(new QuiverBow()),
		    addWeapon(new EnderBow()),
		    addWeapon(new EnderRifle()),
		    addWeapon(new FrostLancer()),
		    addWeapon(new OSP()),
		    addWeapon(new OSR()),
		    addWeapon(new OWR()),
		    addWeapon(new FenFire()),
		    addWeapon(new FlintDuster()),
		    addWeapon(new LightningRed()),
		    addWeapon(new Sunray()),
		    addWeapon(new PowderKnuckle()),
		    addWeapon(new PowderKnuckle_Mod()),
		    addWeapon(new NetherBellows()),
		    addWeapon(new RedSprayer()),
		    addWeapon(new SoulCairn()),
		    addWeapon(new AquaAccelerator()),
		    addWeapon(new SilkenSpinner()),
		    addWeapon(new SeedSweeper()),
		    addWeapon(new MediGun()),
		    addWeapon(new ERA()),
		    addWeapon(new AA_Targeter()),
		    addWeapon(new Endernymous()));
	}

	// Helper function for taking care of weapon registration
	private static Item addWeapon(_WeaponBase weapon)
	{
	    Main.weapons.add(weapon);
	    weapon.setRegistryName(Constants.MODID, weapon.getName());
	    weapon.setUnlocalizedName(Constants.MODID + ".weapon." + weapon.getName());
	    return weapon;
	}

	private static void registerAmmo(IForgeRegistry<Item> registry) // Items.WITH which weapons can be reloaded
	{
	    registry.registerAll(addAmmo(new ArrowBundle(), "arrow_bundle"),
		    addAmmo(new RocketBundle(), "rocket_bundle"),
		    addAmmo(new GatlingAmmo(), "sugar_magazine"),
		    addAmmo(new LargeRocket(), "large_rocket"),
		    addAmmo(new ColdIronClip(), "cold_iron_clip"),
		    addAmmo(new BoxOfFlintDust(), "box_of_flint_dust"),
		    addAmmo(new SeedJar(), "seed_jar"),
		    addAmmo(new ObsidianMagazine(), "obsidian_magazine"),
		    addAmmo(new GoldMagazine(), "gold_magazine"),
		    addAmmo(new NeedleMagazine(), "thorn_magazine"),
		    addAmmo(new LapisMagazine(), "lapis_magazine"),
		    addAmmo(new RedstoneMagazine(), "redstone_magazine"),
		    addAmmo(new LargeNetherrackMagazine(), "large_netherrack_magazine"),
		    addAmmo(new LargeRedstoneMagazine(), "large_redstone_magazine"),
		    addAmmo(new EnderQuartzClip(), "ender_quartz_magazine"));
	}

	private static Item addAmmo(_AmmoBase ammoBase, String name)
	{
	    Main.ammo.add(ammoBase);
	    ammoBase.setUnlocalizedName(Constants.MODID + ".ammo." + name);
	    ammoBase.setRegistryName(Constants.MODID + ":" + name);
	    return ammoBase;
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> e)
	{

	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent e)
	{
	    for (_AmmoBase ammunition : ammo)
	    {
		ModelLoader.setCustomModelResourceLocation(ammunition, 0, new ModelResourceLocation(new ResourceLocation(Constants.MODID, "ammo/" + ammunition.getRegistryName().getResourcePath()), "inventory"));
	    }

	    for (_WeaponBase weapon : weapons)
	    {
		ModelLoader.setCustomModelResourceLocation(weapon, 0, new ModelResourceLocation(new ResourceLocation(Constants.MODID, "weapons/" + weapon.getRegistryName().getResourcePath()), "inventory"));
	    }
	}
    }
}