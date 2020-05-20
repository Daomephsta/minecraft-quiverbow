package com.domochevsky.quiverbow;

import java.util.ArrayList;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import com.domochevsky.quiverbow.ammo.*;
import com.domochevsky.quiverbow.armsassistant.EntityArmsAssistant;
import com.domochevsky.quiverbow.blocks.FenLight;
import com.domochevsky.quiverbow.config.QuiverbowConfig;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.domochevsky.quiverbow.loot.LootHandler;
import com.domochevsky.quiverbow.miscitems.IncompleteEnderRailAccelerator;
import com.domochevsky.quiverbow.miscitems.PackedUpAA;
import com.domochevsky.quiverbow.miscitems.QuiverBowItem;
import com.domochevsky.quiverbow.net.PacketHandler;
import com.domochevsky.quiverbow.projectiles.*;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.util.RegistryHelper;
import com.domochevsky.quiverbow.weapons.*;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import daomephsta.umbra.resources.ResourceLocationExt;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = QuiverbowMain.MODID, name = QuiverbowMain.NAME, version = "b102")
public class QuiverbowMain
{
	public static final String NAME = "QuiverBow: Restrung";
	public static final String MODID = "quiverbow_restrung";

	@Instance(QuiverbowMain.MODID)
	public static QuiverbowMain instance;

	@SidedProxy(clientSide = "com.domochevsky.quiverbow.ClientProxy", serverSide = "com.domochevsky.quiverbow.CommonProxy")
	public static CommonProxy proxy;

	public static Logger logger;

	public static ArrayList<WeaponBase> weapons = new ArrayList<WeaponBase>();
	public static ArrayList<AmmoBase> ammo = new ArrayList<AmmoBase>();

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
		PacketHandler.initPackets();
		if (QuiverbowConfig.allowTurret)
		{
			EntityRegistry.registerModEntity(new ResourceLocation(QuiverbowMain.MODID, "turret"),
				EntityArmsAssistant.class, "turret", 0, this, 80, 1, true);
		}
		LootHandler.initialise();
		MinecraftForge.EVENT_BUS.register(new Listener());
        proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		QuiverbowConfig.loadWeaponProperties();
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
					RegistryHelper.registerItem(new IncompleteEnderRailAccelerator(), ".misc.", "incomplete_ender_rail_accelerator"),
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
					addWeapon(new AquaAccelerator()), addWeapon(new SilkenSpinner()),
					// TODO: Reimplement addWeapon(new MediGun()),
					addWeapon(new ERA())
	                // TODO: Reimplement addWeapon(new AATargeter())
					);
			registerWeaponsWithAmmo(registry);
		}

		// Registers weapons that need their ammo item type as a ctor arg, and said ammo
		private static void registerWeaponsWithAmmo(IForgeRegistry<Item> registry)
		{
			// Sugar Engine and Sugar Magazine
			AmmoBase sugarMag = addAmmo(new GatlingAmmo(), "sugar_magazine");
			registry.registerAll(sugarMag, addWeapon(new SugarEngine(sugarMag)));

			// Obsidian weapons and Obsidian Magazine
			AmmoBase obsidianMag = addAmmo(new ObsidianMagazine(), "obsidian_magazine");
			registry.registerAll(obsidianMag, addWeapon(new OSR(obsidianMag)), addWeapon(new OSP(obsidianMag)),
					addWeapon(new OWR(obsidianMag)));

			AmmoBase coldIronClip = addAmmo(new AmmoBase(), "cold_iron_clip");
			registry.registerAll(coldIronClip, addWeapon(new FrostLancer(coldIronClip)));

			AmmoBase goldMagazine = addAmmo(new GoldMagazine(), "gold_magazine");
			registry.registerAll(goldMagazine, addWeapon(new CoinTosser(goldMagazine)),
					addWeapon(new CoinTosserMod(goldMagazine)));

			// Hidden Ender Pistol and Ender Quartz Magazine
			AmmoBase enderQuartzMagazine = addAmmo(new EnderQuartzClip(), "ender_quartz_magazine");
			registry.registerAll(enderQuartzMagazine, addWeapon(new Endernymous(enderQuartzMagazine)));

			AmmoBase lapisMagazine = addAmmo(new LapisMagazine(), "lapis_magazine");
			registry.registerAll(lapisMagazine, addWeapon(new LapisCoil(lapisMagazine)));

			AmmoBase largeNetherrackMagazine = addAmmo(new LargeNetherrackMagazine(), "large_netherrack_magazine");
			registry.registerAll(largeNetherrackMagazine, addWeapon(new NetherBellows(largeNetherrackMagazine)));

			// Thorn Spitter, Proximity Thorn Thrower and Thorn Magazine
			AmmoBase thornMagazine = addAmmo(new NeedleMagazine(), "thorn_magazine");
			registry.registerAll(thornMagazine, addWeapon(new ThornSpitter(thornMagazine)),
					addWeapon(new ProximityNeedler(thornMagazine)));

			// Redstone Sprayer and Large Redstone Magazine
			AmmoBase largeRedstoneMagazine = addAmmo(new LargeRedstoneMagazine(), "large_redstone_magazine");
			registry.registerAll(largeRedstoneMagazine, addWeapon(new RedSprayer(largeRedstoneMagazine)));

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

		private static void registerAmmo(IForgeRegistry<Item> registry)
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
		    ReloadSpecificationRegistry.INSTANCE.loadData();
		    for (WeaponBase weapon : ReloadSpecificationRegistry.INSTANCE.getRegisteredWeapons())
                e.getRegistry().register(new RecipeLoadAmmo(weapon).setRegistryName(MODID, "load_" + weapon.getRegistryName().getResourcePath()));
		}

		@SubscribeEvent
		public static void registerEntities(RegistryEvent.Register<EntityEntry> e)
		{
		    if (QuiverbowConfig.allowTurret)
		        e.getRegistry().register(createEntry("arms_assistant", 80, 1, true, EntityArmsAssistant.class, EntityArmsAssistant::new));
		    e.getRegistry().registerAll(
    		    createEntry("blaze", 80, 1, true, BlazeShot.class, BlazeShot::new),
                createEntry("coin", 80, 1, true, CoinShot.class, CoinShot::new),
                createEntry("rocket_small", 80, 1, true, SmallRocket.class, SmallRocket::new),
                createEntry("lapis", 80, 1, true, LapisShot.class, LapisShot::new),
                createEntry("thorn", 80, 1, true, Thorn.class, Thorn::new),
                createEntry("proximity_thorn", 80, 1, true, ProxyThorn.class, ProxyThorn::new),
                createEntry("sugar", 80, 1, true, SugarRod.class, SugarRod::new),
                createEntry("rocket_big", 80, 1, true, BigRocket.class, BigRocket::new),
                createEntry("sabot_arrow", 80, 1, true, SabotArrow.class, SabotArrow::new),
                createEntry("sabot_rocket", 80, 1, true, SabotRocket.class, SabotRocket::new),
                createEntry("seed", 80, 1, true, Seed.class, Seed::new),
                createEntry("potato", 80, 1, true, PotatoShot.class, PotatoShot::new),
                createEntry("snow", 80, 1, true, SnowShot.class, SnowShot::new),
                createEntry("ender", 80, 1, true, EnderShot.class, EnderShot::new),
                createEntry("cold_iron", 80, 1, true, ColdIron.class, ColdIron::new),
                createEntry("osp_shot", 80, 1, true, OSPShot.class, OSPShot::new),
                createEntry("osr_shot", 80, 1, true, OSRShot.class, OSRShot::new),
                createEntry("owr_shot", 80, 1, true, OWRShot.class, OWRShot::new),
                createEntry("fen_light", 80, 1, true, FenGoop.class, FenGoop::new),
                createEntry("flint_dust", 80, 1, true, FlintDust.class, FlintDust::new),
                createEntry("red_light", 80, 1, true, RedLight.class, RedLight::new),
                createEntry("sunlight", 80, 1, true, SunLight.class, SunLight::new),
                createEntry("nether_fire", 80, 1, true, NetherFire.class, NetherFire::new),
                createEntry("red_spray", 80, 1, true, RedSpray.class, RedSpray::new),
                createEntry("soul", 80, 1, true, SoulShot.class, SoulShot::new),
                createEntry("water", 80, 1, true, WaterShot.class, WaterShot::new),
                createEntry("web", 80, 1, true, WebShot.class, WebShot::new),
                createEntry("health", 80, 1, true, HealthBeam.class, HealthBeam::new),
                createEntry("era_shot", 80, 1, true, EnderAccelerator.class, EnderAccelerator::new),
                createEntry("ano", 80, 1, true, EnderAno.class, EnderAno::new));
		}

	    private static int nextEntityNetworkId = 0;
		private static EntityEntry createEntry(String name, int trackingRange, int updateFrequency, boolean sendVelocityUpdates, Class<? extends Entity> entityClass, Function<World, Entity> factory)
		{
		    return EntityEntryBuilder.create()
		        .entity(entityClass)
                .id(new ResourceLocation(QuiverbowMain.MODID, name), nextEntityNetworkId++)
                .name(name)
                .tracker(trackingRange, updateFrequency, sendVelocityUpdates)
                .factory(factory)
                .build();
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
					ModelLoader.setCustomModelResourceLocation(ammunition, 0,
						new ModelResourceLocation(ResourceLocationExt.prefixPath(ammunition.getRegistryName(), "ammo/"), "inventory"));
			}

			for (WeaponBase weapon : weapons)
			{
			    if (weapon == ItemRegistry.AUTO_CROSSBOW)
			    {
			        registerCrossbowModel(weapon);
			        continue;
			    }
				ModelLoader.setCustomModelResourceLocation(weapon, 0, new ModelResourceLocation(
					ResourceLocationExt.addToPath(weapon.getRegistryName(), "weapons/", "_internal"), "inventory"));
				ModelBakery.registerItemVariants(weapon, ResourceLocationExt.prefixPath(weapon.getRegistryName(), "weapons/"));
			}
			setStandardModelLocation(ItemRegistry.PART_SUGAR_ENGINE_BODY);
			setStandardModelLocation(ItemRegistry.PART_SUGAR_ENGINE_BARREL);
			setStandardModelLocation(ItemRegistry.ARMS_ASSISTANT);
			setStandardModelLocation(ItemRegistry.INCOMPLETE_ENDER_RAIL_ACCELERATOR);
		}

        private static void setStandardModelLocation(Item item)
        {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }

	    private static void registerCrossbowModel(Item crossbow)
	    {
	       String path = crossbow.getRegistryName().getResourcePath();
	       final ModelResourceLocation empty = new ModelResourceLocation(
	               new ResourceLocation(QuiverbowMain.MODID, "weapons/" + path + "_empty"),
	               "inventory");
	       final ModelResourceLocation unchambered = new ModelResourceLocation(new ResourceLocation(QuiverbowMain.MODID,
	               "weapons/" + path + "_unchambered"), "inventory");
	       final ModelResourceLocation chambered = new ModelResourceLocation(
	               new ResourceLocation(QuiverbowMain.MODID, "weapons/" + path), "inventory");
	       ModelBakery.registerItemVariants(crossbow, empty, unchambered, chambered);
	       ModelLoader.setCustomMeshDefinition(crossbow, stack ->
	       {
	           if (stack.getItemDamage() >= stack.getMaxDamage()) return empty;
	           if (!CrossbowAuto.isChambered(stack)) return unchambered;
	           return chambered;
	       });
	    }
	}
}