package com.domochevsky.quiverbow;

import java.util.ArrayList;

import com.domochevsky.quiverbow.Main.Constants;
import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.ammo.*;
import com.domochevsky.quiverbow.blocks.FenLight;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.domochevsky.quiverbow.miscitems.PackedUpAA;
import com.domochevsky.quiverbow.miscitems.Part_GatlingBarrel;
import com.domochevsky.quiverbow.miscitems.Part_GatlingBody;
import com.domochevsky.quiverbow.models.ISpecialRender;
import com.domochevsky.quiverbow.net.PacketHandler;
import com.domochevsky.quiverbow.projectiles.BigRocket;
import com.domochevsky.quiverbow.projectiles.BlazeShot;
import com.domochevsky.quiverbow.projectiles.CoinShot;
import com.domochevsky.quiverbow.projectiles.ColdIron;
import com.domochevsky.quiverbow.projectiles.EnderAccelerator;
import com.domochevsky.quiverbow.projectiles.EnderAno;
import com.domochevsky.quiverbow.projectiles.EnderShot;
import com.domochevsky.quiverbow.projectiles.FenGoop;
import com.domochevsky.quiverbow.projectiles.FlintDust;
import com.domochevsky.quiverbow.projectiles.HealthBeam;
import com.domochevsky.quiverbow.projectiles.LapisShot;
import com.domochevsky.quiverbow.projectiles.NetherFire;
import com.domochevsky.quiverbow.projectiles.OSP_Shot;
import com.domochevsky.quiverbow.projectiles.OSR_Shot;
import com.domochevsky.quiverbow.projectiles.OWR_Shot;
import com.domochevsky.quiverbow.projectiles.PotatoShot;
import com.domochevsky.quiverbow.projectiles.ProxyThorn;
import com.domochevsky.quiverbow.projectiles.RedLight;
import com.domochevsky.quiverbow.projectiles.RedSpray;
import com.domochevsky.quiverbow.projectiles.RegularArrow;
import com.domochevsky.quiverbow.projectiles.Sabot_Arrow;
import com.domochevsky.quiverbow.projectiles.Sabot_Rocket;
import com.domochevsky.quiverbow.projectiles.ScopedPredictive;
import com.domochevsky.quiverbow.projectiles.Seed;
import com.domochevsky.quiverbow.projectiles.SmallRocket;
import com.domochevsky.quiverbow.projectiles.SnowShot;
import com.domochevsky.quiverbow.projectiles.SoulShot;
import com.domochevsky.quiverbow.projectiles.SugarRod;
import com.domochevsky.quiverbow.projectiles.SunLight;
import com.domochevsky.quiverbow.projectiles.Thorn;
import com.domochevsky.quiverbow.projectiles.WaterShot;
import com.domochevsky.quiverbow.projectiles.WebShot;
import com.domochevsky.quiverbow.projectiles._ProjectileBase;
import com.domochevsky.quiverbow.recipes.RecipeLoadMagazine;
import com.domochevsky.quiverbow.recipes.Recipe_ERA;
import com.domochevsky.quiverbow.recipes.Recipe_Weapon;
import com.domochevsky.quiverbow.util.RegistryHelper;
import com.domochevsky.quiverbow.weapons.AA_Targeter;
import com.domochevsky.quiverbow.weapons.AquaAccelerator;
import com.domochevsky.quiverbow.weapons.CoinTosser;
import com.domochevsky.quiverbow.weapons.CoinTosser_Mod;
import com.domochevsky.quiverbow.weapons.Crossbow_Auto;
import com.domochevsky.quiverbow.weapons.Crossbow_AutoImp;
import com.domochevsky.quiverbow.weapons.Crossbow_Blaze;
import com.domochevsky.quiverbow.weapons.Crossbow_Compact;
import com.domochevsky.quiverbow.weapons.Crossbow_Double;
import com.domochevsky.quiverbow.weapons.DragonBox;
import com.domochevsky.quiverbow.weapons.DragonBox_Quad;
import com.domochevsky.quiverbow.weapons.ERA;
import com.domochevsky.quiverbow.weapons.EnderBow;
import com.domochevsky.quiverbow.weapons.EnderRifle;
import com.domochevsky.quiverbow.weapons.Endernymous;
import com.domochevsky.quiverbow.weapons.FenFire;
import com.domochevsky.quiverbow.weapons.FlintDuster;
import com.domochevsky.quiverbow.weapons.FrostLancer;
import com.domochevsky.quiverbow.weapons.LapisCoil;
import com.domochevsky.quiverbow.weapons.LightningRed;
import com.domochevsky.quiverbow.weapons.MediGun;
import com.domochevsky.quiverbow.weapons.Mortar_Arrow;
import com.domochevsky.quiverbow.weapons.Mortar_Dragon;
import com.domochevsky.quiverbow.weapons.NetherBellows;
import com.domochevsky.quiverbow.weapons.OSP;
import com.domochevsky.quiverbow.weapons.OSR;
import com.domochevsky.quiverbow.weapons.OWR;
import com.domochevsky.quiverbow.weapons.Potatosser;
import com.domochevsky.quiverbow.weapons.PowderKnuckle;
import com.domochevsky.quiverbow.weapons.PowderKnuckle_Mod;
import com.domochevsky.quiverbow.weapons.ProximityNeedler;
import com.domochevsky.quiverbow.weapons.QuiverBow;
import com.domochevsky.quiverbow.weapons.RPG;
import com.domochevsky.quiverbow.weapons.RPG_Imp;
import com.domochevsky.quiverbow.weapons.RedSprayer;
import com.domochevsky.quiverbow.weapons.SeedSweeper;
import com.domochevsky.quiverbow.weapons.Seedling;
import com.domochevsky.quiverbow.weapons.SilkenSpinner;
import com.domochevsky.quiverbow.weapons.SnowCannon;
import com.domochevsky.quiverbow.weapons.SoulCairn;
import com.domochevsky.quiverbow.weapons.SugarEngine;
import com.domochevsky.quiverbow.weapons.Sunray;
import com.domochevsky.quiverbow.weapons.ThornSpitter;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.Side;
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
    public static CreativeTabs 
    QUIVERBOW_TAB = new CreativeTabs(Constants.MODID)
    {
	@Override
	public ItemStack getTabIconItem()
	{
	    return new ItemStack(ItemRegistry.QUIVERBOW);
	}
    };

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

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
	//Init creative tab now that items are initialised
	/*QUIVERBOW_TAB = new CreativeTabs(Constants.MODID)
	    {
		@Override
		public ItemStack getTabIconItem()
		{
		    return new ItemStack(ItemRegistry.QUIVERBOW);
		}
	    };*/
    }

    void registerProjectiles() // Entities that get shot out of weapons as
    // projectiles
    {
	this.addProjectileItem(RegularArrow.class, "arrow", Items.ARROW);
	this.addProjectileItem(BlazeShot.class, "blaze", Items.FIRE_CHARGE);
	this.addProjectileItem(CoinShot.class, "coin", Items.GOLD_NUGGET);
	this.addProjectileItem(SmallRocket.class, "rocket_small", Items.FIREWORKS);

	this.addProjectileItem(LapisShot.class, "lapis", Items.DYE);
	this.addProjectileItem(Thorn.class, "thorn", Blocks.CACTUS);
	this.addProjectileItem(ProxyThorn.class, "proximity_thorn", Blocks.CACTUS);
	this.addProjectileItem(SugarRod.class, "sugar", Items.REEDS);

	this.addProjectileItem(BigRocket.class, "rocket_big", Items.FIREWORKS);

	this.addProjectileItem(Sabot_Arrow.class, "sabot_arrow", Items.ARROW);
	this.addProjectileItem(Sabot_Rocket.class, "sabot_rocket", Items.FIREWORKS);

	this.addProjectileItem(Seed.class, "seed", Items.WHEAT_SEEDS);
	this.addProjectileItem(PotatoShot.class, "potato", Items.BAKED_POTATO);
	this.addProjectileItem(SnowShot.class, "snow", Items.SNOWBALL);

	this.addProjectileItem(ScopedPredictive.class, "predictive", Items.ENDER_EYE);
	this.addProjectileItem(EnderShot.class, "ender", Items.ENDER_PEARL);
	this.addProjectileItem(ColdIron.class, "cold_iron", Items.IRON_INGOT);

	this.addProjectileItem(OSP_Shot.class, "osp_shot", Blocks.OBSIDIAN);
	this.addProjectileItem(OSR_Shot.class, "osr_shot", Blocks.OBSIDIAN);
	this.addProjectileItem(OWR_Shot.class, "owr_shot", Blocks.OBSIDIAN);

	this.addProjectileItem(FenGoop.class, "fen_light", Items.GLOWSTONE_DUST);
	this.addProjectileItem(FlintDust.class, "flint_dust", Items.FLINT);

	this.addProjectileItem(RedLight.class, "red_light", Blocks.REDSTONE_LAMP);
	this.addProjectileItem(SunLight.class, "sunlight", Blocks.GLOWSTONE);

	this.addProjectileItem(NetherFire.class, "nether_fire", Items.BLAZE_POWDER);
	this.addProjectileItem(RedSpray.class, "red_spray", Items.REDSTONE);

	this.addProjectileItem(SoulShot.class, "soul", Blocks.SOUL_SAND);

	this.addProjectileItem(WaterShot.class, "water", Items.WATER_BUCKET);
	this.addProjectileItem(WebShot.class, "web", Blocks.WEB);

	this.addProjectileItem(HealthBeam.class, "health", Items.POTIONITEM);

	this.addProjectileItem(EnderAccelerator.class, "era_shot", Items.IRON_INGOT);
	this.addProjectileItem(EnderAno.class, "ano", Items.ENDER_PEARL);
    }

    private void addProjectileItem(Class<? extends _ProjectileBase> entityClass, String name, Block block)
    {
	Item item = Item.getItemFromBlock(block);
	if(item == null) throw new IllegalArgumentException(block + " does not have an ItemBlock!");
	addProjectileItem(entityClass, name, item);
    }

    private void addProjectileItem(Class<? extends _ProjectileBase> entityClass, String name, Item item)
    {
	addProjectile(entityClass, name);
	proxy.registerItemProjectileRenderer(entityClass, item);
    }

    private void addProjectile(Class<? extends _ProjectileBase> entityClass, String name)
    {
	EntityRegistry.registerModEntity(new ResourceLocation(Constants.MODID, name), entityClass,
		"projectilechevsky_" + name, projectileCount, this, 80, 1, true);
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
		    addWeapon(new DragonBox()),
		    addWeapon(new DragonBox_Quad()),
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
		    addWeapon(new FenFire()),
		    //TODO: Reimplement addWeapon(new FlintDuster()),
		    //TODO: Reimplement addWeapon(new Sunray()),
		    addWeapon(new PowderKnuckle()),
		    addWeapon(new PowderKnuckle_Mod()),
		    addWeapon(new SoulCairn()),
		    addWeapon(new AquaAccelerator()),
		    addWeapon(new SilkenSpinner()),
		    //TODO: Reimplement addWeapon(new MediGun()),
		    addWeapon(new ERA()),
		    addWeapon(new AA_Targeter()));
	    registerWeaponsWithAmmo(registry);
	}

	//Registers weapons that need their ammo item type as a ctor arg with their ammo
	private static void registerWeaponsWithAmmo(IForgeRegistry<Item> registry)
	{      
	    //Sugar Engine and Sugar Magazine
	    _AmmoBase sugarMag = addAmmo(new GatlingAmmo(), "sugar_magazine");
	    registry.registerAll(sugarMag, addWeapon(new SugarEngine(sugarMag)));

	    //Obsidian weapons and Obsidian Magazine 
	    _AmmoBase obsidianMag = addAmmo(new ObsidianMagazine(), "obsidian_magazine");
	    registry.registerAll(obsidianMag,
		    addWeapon(new OSR(obsidianMag)),
		    addWeapon(new OSP(obsidianMag)),
		    addWeapon(new OWR(obsidianMag)));

	    //Frost Lancer and Cold Iron Clip
	    _AmmoBase coldIronClip = addAmmo(new ColdIronClip(), "cold_iron_clip");
	    registry.registerAll(coldIronClip, addWeapon(new FrostLancer(coldIronClip)));

	    //Coin Tossers and Gold Magazine
	    _AmmoBase goldMagazine = addAmmo(new GoldMagazine(), "gold_magazine");
	    registry.registerAll(goldMagazine, addWeapon(new CoinTosser(goldMagazine)), addWeapon(new CoinTosser_Mod(goldMagazine)));

	    //Hidden Ender Pistol and Ender Quartz Magazine
	    _AmmoBase enderQuartzMagazine = addAmmo(new EnderQuartzClip(), "ender_quartz_magazine");
	    registry.registerAll(enderQuartzMagazine, addWeapon(new Endernymous(enderQuartzMagazine)));

	    //Lapis Coil & Lapis Magazine
	    _AmmoBase lapisMagazine = addAmmo(new LapisMagazine(), "lapis_magazine");
	    registry.registerAll(lapisMagazine, addWeapon(new LapisCoil(lapisMagazine)));

	    //Nether Bellows and Large Netherrack Magazine
	    _AmmoBase largeNetherrackMagazine = addAmmo(new LargeNetherrackMagazine(), "large_netherrack_magazine");
	    registry.registerAll(largeNetherrackMagazine, addWeapon(new NetherBellows(largeNetherrackMagazine)));

	    //Thorn Spitter, Proximity Thorn Thrower and Thorn Magazine
	    _AmmoBase thornMagazine = addAmmo(new NeedleMagazine(), "thorn_magazine");
	    registry.registerAll(thornMagazine, addWeapon(new ThornSpitter(thornMagazine)), addWeapon(new ProximityNeedler(thornMagazine)));

	    //Redstone Sprayer and Large Redstone Magazine
	    _AmmoBase largeRedstoneMagazine = addAmmo(new LargeRedstoneMagazine(), "large_redstone_magazine");
	    registry.registerAll(largeRedstoneMagazine, addWeapon(new RedSprayer(largeRedstoneMagazine)));

	    //Seed Sweeper and Seed Jar
	    _AmmoBase seedJar = addAmmo(new SeedJar(), "seed_jar");
	    registry.registerAll(seedJar, addWeapon(new SeedSweeper(seedJar)));
	    
	    //Lightning Red and Redstone Magazine
	    /*TODO: Reimplement _AmmoBase redstoneMagazine = addAmmo(new RedstoneMagazine(), "redstone_magazine");
	    registry.registerAll(redstoneMagazine, addWeapon(new LightningRed(redstoneMagazine)));*/
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
		    addAmmo(new LargeRocket(), "large_rocket"),
		    addAmmo(new BoxOfFlintDust(), "box_of_flint_dust"));
	}

	private static _AmmoBase addAmmo(_AmmoBase ammoBase, String name)
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
    }

    @EventBusSubscriber(modid = Constants.MODID, value = Side.CLIENT)
    private static class ModelHandler
    {
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent e)
	{
	    for (_AmmoBase ammunition : ammo)
	    {
		if(ammunition instanceof ISpecialRender) ((ISpecialRender) ammunition).registerRender();
		else ModelLoader.setCustomModelResourceLocation(ammunition, 0, new ModelResourceLocation(new ResourceLocation(Constants.MODID, "ammo/" + ammunition.getRegistryName().getResourcePath()), "inventory"));
	    }

	    for (_WeaponBase weapon : weapons)
	    {
		if(weapon instanceof ISpecialRender) ((ISpecialRender) weapon).registerRender();
		else ModelLoader.setCustomModelResourceLocation(weapon, 0, new ModelResourceLocation(new ResourceLocation(Constants.MODID, "weapons/" + weapon.getRegistryName().getResourcePath()), "inventory"));
	    }
	    ModelLoader.setCustomModelResourceLocation(ItemRegistry.PART_SUGAR_ENGINE_BODY, 0, new ModelResourceLocation(ItemRegistry.PART_SUGAR_ENGINE_BODY.getRegistryName(), "inventory"));
	    ModelLoader.setCustomModelResourceLocation(ItemRegistry.PART_SUGAR_ENGINE_BARREL, 0, new ModelResourceLocation(ItemRegistry.PART_SUGAR_ENGINE_BARREL.getRegistryName(), "inventory"));
	    ModelLoader.setCustomModelResourceLocation(ItemRegistry.ARMS_ASSISTANT, 0, new ModelResourceLocation(ItemRegistry.ARMS_ASSISTANT.getRegistryName(), "inventory"));
	}
    }
}