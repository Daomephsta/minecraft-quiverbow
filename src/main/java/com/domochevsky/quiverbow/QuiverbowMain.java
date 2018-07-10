package com.domochevsky.quiverbow;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import com.domochevsky.quiverbow.ammo.*;
import com.domochevsky.quiverbow.armsassistant.EntityArmsAssistant;
import com.domochevsky.quiverbow.blocks.FenLight;
import com.domochevsky.quiverbow.config.QuiverbowConfig;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.domochevsky.quiverbow.miscitems.PackedUpAA;
import com.domochevsky.quiverbow.miscitems.QuiverBowItem;
import com.domochevsky.quiverbow.models.ISpecialRender;
import com.domochevsky.quiverbow.net.PacketHandler;
import com.domochevsky.quiverbow.projectiles.*;
import com.domochevsky.quiverbow.util.RegistryHelper;
import com.domochevsky.quiverbow.weapons.*;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = QuiverbowMain.MODID, name = QuiverbowMain.NAME, version = "b102")
public class QuiverbowMain
{
	public static final String NAME = "QuiverBow";
	public static final String MODID = "quiverchevsky";

	@Instance(QuiverbowMain.MODID)
	public static QuiverbowMain instance;

	@SidedProxy(clientSide = "com.domochevsky.quiverbow.ClientProxy", serverSide = "com.domochevsky.quiverbow.CommonProxy")
	public static CommonProxy proxy;

	public static Logger logger;
	
	//TODO Remove
	protected Configuration config; // Accessible from other files this way

	public static ArrayList<WeaponBase> weapons = new ArrayList<WeaponBase>(); // Holder
	// array
	// for
	// all
	// (fully
	// set
	// up)
	// possible
	// weapons
	public static ArrayList<AmmoBase> ammo = new ArrayList<AmmoBase>(); // Same
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

	public static CreativeTabs QUIVERBOW_TAB = new CreativeTabs(QuiverbowMain.MODID)
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
		QuiverbowConfig.load(event.getSuggestedConfigurationFile());
		logger = event.getModLog();
		this.registerProjectiles();

		PacketHandler.initPackets(); // Used for sending particle packets, so I
		// can do my thing purely on the server
		// side

		// Registering the Arms Assistant
		if (QuiverbowConfig.allowTurret)
		{
			EntityRegistry.registerModEntity(new ResourceLocation(QuiverbowMain.MODID, "turret"),
				EntityArmsAssistant.class, "turret", 0, this, 80, 1, true);
		}
		proxy.registerRenderers();

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
		QuiverbowConfig.loadWeaponProperties();
		//foo.RecipeJSONifier.generateRecipes();
	}
	
	@EventHandler
	public void recieveIMCMessages(FMLInterModComms.IMCEvent event)
	{
		for(IMCMessage message : event.getMessages())
		{
			switch (message.key)
			{
			case "soul_cairn_blacklist":
				SoulShot.blacklistEntity(message.getResourceLocationValue());
				break;
			default:
				throw new IllegalArgumentException("Quiverbow: Recieved IMC message with unknown key: " + message.key);
			}
		}
	}

	void registerProjectiles() // Entities that get shot out of weapons as
	// projectiles
	{
		this.addProjectile(BlazeShot.class, "blaze");
		this.addProjectile(CoinShot.class, "coin");
		this.addProjectile(SmallRocket.class, "rocket_small");
		this.addProjectile(LapisShot.class, "lapis");
		this.addProjectile(Thorn.class, "thorn");
		this.addProjectile(ProxyThorn.class, "proximity_thorn");
		this.addProjectile(SugarRod.class, "sugar");
		this.addProjectile(BigRocket.class, "rocket_big");
		this.addProjectile(SabotArrow.class, "sabot_arrow");
		this.addProjectile(SabotRocket.class, "sabot_rocket");
                                                               
		this.addProjectile(Seed.class, "seed");
		this.addProjectile(PotatoShot.class, "potato");
		this.addProjectile(SnowShot.class, "snow");
                                                               
		this.addProjectile(EnderShot.class, "ender");
		this.addProjectile(ColdIron.class, "cold_iron");
                                                     
		this.addProjectile(OSPShot.class, "osp_shot");
		this.addProjectile(OSRShot.class, "osr_shot");
		this.addProjectile(OWRShot.class, "owr_shot");
                                                     
		this.addProjectile(FenGoop.class, "fen_light");
		this.addProjectile(FlintDust.class, "flint_dust");
                                                          
		this.addProjectile(RedLight.class, "red_light");
		this.addProjectile(SunLight.class, "sunlight");
                                                          
		this.addProjectile(NetherFire.class, "nether_fire");
		this.addProjectile(RedSpray.class, "red_spray");
                                                          
		this.addProjectile(SoulShot.class, "soul");
                                                          
		this.addProjectile(WaterShot.class, "water");
		this.addProjectile(WebShot.class, "web");
                                                          
		this.addProjectile(HealthBeam.class, "health");
                                                               
		this.addProjectile(EnderAccelerator.class, "era_shot");
		this.addProjectile(EnderAno.class, "ano");
	}

	private void addProjectile(Class<? extends ProjectileBase> entityClass, String name)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(QuiverbowMain.MODID, name), entityClass,
				"projectilechevsky_" + name, projectileCount, this, 80, 1, true);
		projectileCount += 1;
	}

	@Mod.EventBusSubscriber(modid = QuiverbowMain.MODID)
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
			registry.registerAll(
					RegistryHelper.registerItem(new QuiverBowItem(), ".misc.", "part_sugar_engine_body"),
					RegistryHelper.registerItem(new QuiverBowItem(), ".misc.", "part_sugar_engine_barrel"),
					RegistryHelper.registerItem(new PackedUpAA(), ".misc.", "arms_assistant"));
		}

		private static void registerWeapons(IForgeRegistry<Item> registry) // The
																			// weapons
																			// themselves
		{
			registry.registerAll(addWeapon(new CrossbowCompact()), addWeapon(new CrossbowDouble()),
					addWeapon(new CrossbowBlaze()), addWeapon(new CrossbowAuto()), addWeapon(new CrossbowAutoImp()),
					addWeapon(new DragonBox()), addWeapon(new DragonBoxQuad()), addWeapon(new RPG()),
					addWeapon(new RPGImp()), addWeapon(new MortarArrow()), addWeapon(new MortarDragon()),
					addWeapon(new Seedling()), addWeapon(new Potatosser()), addWeapon(new SnowCannon()),
					addWeapon(new QuiverBow()), addWeapon(new EnderBow()), addWeapon(new EnderRifle()),
					addWeapon(new FenFire()),
					// TODO: Reimplement addWeapon(new FlintDuster()),
					// TODO: Reimplement addWeapon(new Sunray()),
					addWeapon(new PowderKnuckle()), addWeapon(new PowderKnuckleMod()), addWeapon(new SoulCairn()),
					addWeapon(new AquaAccelerator()), addWeapon(new SilkenSpinner())//,
					// TODO: Reimplement addWeapon(new MediGun()),
					// TODO: Reimplement addWeapon(new ERA()), addWeapon(new AATargeter())
					);
			registerWeaponsWithAmmo(registry);
		}

		// Registers weapons that need their ammo item type as a ctor arg with
		// their ammo
		private static void registerWeaponsWithAmmo(IForgeRegistry<Item> registry)
		{
			// Sugar Engine and Sugar Magazine
			AmmoBase sugarMag = addAmmo(new GatlingAmmo(), "sugar_magazine");
			registry.registerAll(sugarMag, addWeapon(new SugarEngine(sugarMag)));

			// Obsidian weapons and Obsidian Magazine
			AmmoBase obsidianMag = addAmmo(new ObsidianMagazine(), "obsidian_magazine");
			registry.registerAll(obsidianMag, addWeapon(new OSR(obsidianMag)), addWeapon(new OSP(obsidianMag)),
					addWeapon(new OWR(obsidianMag)));

			// Frost Lancer and Cold Iron Clip
			AmmoBase coldIronClip = addAmmo(new AmmoBase(), "cold_iron_clip");
			registry.registerAll(coldIronClip, addWeapon(new FrostLancer(coldIronClip)));

			// Coin Tossers and Gold Magazine
			AmmoBase goldMagazine = addAmmo(new GoldMagazine(), "gold_magazine");
			registry.registerAll(goldMagazine, addWeapon(new CoinTosser(goldMagazine)),
					addWeapon(new CoinTosserMod(goldMagazine)));

			// Hidden Ender Pistol and Ender Quartz Magazine
			AmmoBase enderQuartzMagazine = addAmmo(new EnderQuartzClip(), "ender_quartz_magazine");
			registry.registerAll(enderQuartzMagazine, addWeapon(new Endernymous(enderQuartzMagazine)));

			// Lapis Coil & Lapis Magazine
			AmmoBase lapisMagazine = addAmmo(new LapisMagazine(), "lapis_magazine");
			registry.registerAll(lapisMagazine, addWeapon(new LapisCoil(lapisMagazine)));

			// Nether Bellows and Large Netherrack Magazine
			AmmoBase largeNetherrackMagazine = addAmmo(new LargeNetherrackMagazine(), "large_netherrack_magazine");
			registry.registerAll(largeNetherrackMagazine, addWeapon(new NetherBellows(largeNetherrackMagazine)));

			// Thorn Spitter, Proximity Thorn Thrower and Thorn Magazine
			AmmoBase thornMagazine = addAmmo(new NeedleMagazine(), "thorn_magazine");
			registry.registerAll(thornMagazine, addWeapon(new ThornSpitter(thornMagazine)),
					addWeapon(new ProximityNeedler(thornMagazine)));

			// Redstone Sprayer and Large Redstone Magazine
			AmmoBase largeRedstoneMagazine = addAmmo(new LargeRedstoneMagazine(), "large_redstone_magazine");
			registry.registerAll(largeRedstoneMagazine, addWeapon(new RedSprayer(largeRedstoneMagazine)));

			// Seed Sweeper and Seed Jar
			AmmoBase seedJar = addAmmo(new SeedJar(), "seed_jar");
			registry.registerAll(seedJar, addWeapon(new SeedSweeper(seedJar)));

			// Lightning Red and Redstone Magazine
			/* TODO: Reimplement _AmmoBase redstoneMagazine = addAmmo(new
			 * RedstoneMagazine(), "redstone_magazine");
			 * registry.registerAll(redstoneMagazine, addWeapon(new
			 * LightningRed(redstoneMagazine))); */
		}

		// Helper function for taking care of weapon registration
		private static Item addWeapon(WeaponBase weapon)
		{
			QuiverbowMain.weapons.add(weapon);
			weapon.setRegistryName(QuiverbowMain.MODID, weapon.getName());
			weapon.setUnlocalizedName(QuiverbowMain.MODID + ".weapon." + weapon.getName());
			return weapon;
		}

		private static void registerAmmo(IForgeRegistry<Item> registry) // Items.WITH
																		// which
																		// weapons
																		// can
																		// be
																		// reloaded
		{
			registry.registerAll(addAmmo(new AmmoBase(), "arrow_bundle"),
					addAmmo(new AmmoBase(), "rocket_bundle"), addAmmo(new AmmoBase(), "large_rocket"),
					addAmmo(new BoxOfFlintDust(), "box_of_flint_dust"));
		}

		private static AmmoBase addAmmo(AmmoBase ammoBase, String name)
		{
			QuiverbowMain.ammo.add(ammoBase);
			ammoBase.setUnlocalizedName(QuiverbowMain.MODID + ".ammo." + name);
			ammoBase.setRegistryName(QuiverbowMain.MODID + ":" + name);
			return ammoBase;
		}
		
		@SubscribeEvent
		public static void registerRecipes(RegistryEvent.Register<IRecipe> e)
		{

		}

		@SubscribeEvent
		public static void registerEntities(RegistryEvent.Register<EntityEntry> e)
		{

		}
	}

	@EventBusSubscriber(modid = QuiverbowMain.MODID, value = Side.CLIENT)
	private static class ModelHandler
	{
		@SubscribeEvent
		public static void registerModels(ModelRegistryEvent e)
		{
			for (AmmoBase ammunition : ammo)
			{
				if (ammunition instanceof ISpecialRender) ((ISpecialRender) ammunition).registerRender();
				else
					ModelLoader
							.setCustomModelResourceLocation(ammunition, 0,
									new ModelResourceLocation(
											new ResourceLocation(QuiverbowMain.MODID,
													"ammo/" + ammunition.getRegistryName().getResourcePath()),
											"inventory"));
			}

			for (WeaponBase weapon : weapons)
			{
				if (weapon instanceof ISpecialRender) ((ISpecialRender) weapon).registerRender();
				else
					ModelLoader
							.setCustomModelResourceLocation(weapon, 0,
									new ModelResourceLocation(
											new ResourceLocation(QuiverbowMain.MODID,
													"weapons/" + weapon.getRegistryName().getResourcePath()),
											"inventory"));
			}
			ModelLoader.setCustomModelResourceLocation(ItemRegistry.PART_SUGAR_ENGINE_BODY, 0,
					new ModelResourceLocation(ItemRegistry.PART_SUGAR_ENGINE_BODY.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(ItemRegistry.PART_SUGAR_ENGINE_BARREL, 0,
					new ModelResourceLocation(ItemRegistry.PART_SUGAR_ENGINE_BARREL.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(ItemRegistry.ARMS_ASSISTANT, 0,
					new ModelResourceLocation(ItemRegistry.ARMS_ASSISTANT.getRegistryName(), "inventory"));
		}
	}
}