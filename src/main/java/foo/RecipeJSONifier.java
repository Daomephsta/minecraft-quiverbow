package foo;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Quiverbow;
import com.domochevsky.quiverbow.ammo.*;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.domochevsky.quiverbow.weapons.*;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** A modified version of williewillus' JSON recipe generator **/
public class RecipeJSONifier
{
	/* Original License Thing You can include this in your mod/a pack/whatever
	 * you want, as long as that work follows the Mojang EULA. The original
	 * source is viewable at
	 * https://gist.github.com/williewillus/a1a899ce5b0f0ba099078d46ae3dae6e
	 * 
	 * This is a janky JSON generator, for porting from below 1.12 to 1.12.
	 * Simply replace calls to GameRegistry.addShapeless/ShapedRecipe with these
	 * methods, which will dump it to a json in RECIPE_DIR Also works with OD,
	 * replace GameRegistry.addRecipe(new ShapedOreRecipe/ShapelessOreRecipe
	 * with the same calls After you are done, call generateConstants() Note
	 * that in many cases, you can combine multiple old recipes into one, since
	 * you can now specify multiple possibilities for an ingredient without
	 * using the OD. See vanilla for examples. */

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static File RECIPE_DIR = null;
	private static final Set<String> USED_OD_NAMES = new TreeSet<>();

	public static void generateRecipes()
	{
		// One coin tosser (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.COIN_TOSSER, 1), "z z", "zxz", " y ", 'x',
				Blocks.PISTON, 'y', Blocks.LEVER, 'z', Items.IRON_INGOT);
		// Ammo
		addMagazineLoadRecipe(GoldMagazine.class, ItemRegistry.COIN_TOSSER);

		// Modifying the Coin Tosser with double piston tech
		addShapelessRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.COIN_TOSSER, 1),
				Helper.getWeaponStackByClass(CoinTosser.class, true), Blocks.STICKY_PISTON, Blocks.TRIPWIRE_HOOK,
				Items.IRON_INGOT, Items.IRON_INGOT);
		// Ammo
		addMagazineLoadRecipe(GoldMagazine.class, ItemRegistry.COIN_TOSSER_MOD);

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.HIDDEN_ENDER_PISTOL, 1), "e e", "ofo", "oto", 'o',
				Blocks.OBSIDIAN, 'e', Blocks.END_STONE, 't', Blocks.TRIPWIRE_HOOK, 'f', Items.FLINT_AND_STEEL);
		// Ammo
		addMagazineLoadRecipe(EnderQuartzClip.class, ItemRegistry.HIDDEN_ENDER_PISTOL);

		// One lapis coil (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.LAPIS_COIL, 1), "z z", "axa", " y ", 'x',
				Blocks.PISTON, 'y', Blocks.LEVER, 'z', Items.IRON_INGOT, 'a', Items.REPEATER);
		addMagazineLoadRecipe(LapisMagazine.class, ItemRegistry.LAPIS_COIL);

		// One redstone sprayer (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.NETHER_BELLOWS, 1), "zxz", "zbz", "cya", 'x',
				Blocks.PISTON, 'y', Blocks.TRIPWIRE_HOOK, 'z', Blocks.OBSIDIAN, 'a', Items.REPEATER, 'b',
				Blocks.STICKY_PISTON, 'c', Items.FLINT_AND_STEEL);
		addMagazineLoadRecipe(LargeNetherrackMagazine.class, ItemRegistry.NETHER_BELLOWS);

		// One Obsidian Splinter
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.SPLINTER_PISTOL, 1), " io", "ipi", "oft", 'o',
				Blocks.OBSIDIAN, 'i', Items.IRON_INGOT, 'p', Blocks.PISTON, 'f', Items.FLINT_AND_STEEL, 't',
				Blocks.TRIPWIRE_HOOK);
		// Reloading with obsidian magazine
		addMagazineLoadRecipe(ObsidianMagazine.class, ItemRegistry.SPLINTER_PISTOL);

		// One obsidigun (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.SPLINTER_RIFLE, 1), "x x", "zbz", "xyx", 'x',
				Blocks.OBSIDIAN, 'y', Blocks.LEVER, 'z', Items.IRON_INGOT, 'b', Blocks.PISTON);
		// Reloading with obsidian magazine
		addMagazineLoadRecipe(ObsidianMagazine.class, ItemRegistry.SPLINTER_RIFLE);

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.PROXIMITY_THORN_THROWER, 1), "ihi", "bpb", "tsi",
				't', Blocks.TRIPWIRE_HOOK, 'b', Blocks.IRON_BARS, 'i', Items.IRON_INGOT, 'h', Blocks.HOPPER, 's',
				Blocks.STICKY_PISTON, 'p', Blocks.PISTON);
		// Ammo
		addMagazineLoadRecipe(NeedleMagazine.class, ItemRegistry.PROXIMITY_THORN_THROWER);

		// One redstone sprayer (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.REDSTONE_SPRAYER, 1), "zxz", "aba", "zyz", 'x',
				Blocks.PISTON, 'y', Blocks.TRIPWIRE_HOOK, 'z', Items.IRON_INGOT, 'a', Items.REPEATER, 'b',
				Blocks.STICKY_PISTON);
		addMagazineLoadRecipe(LargeRedstoneMagazine.class, ItemRegistry.REDSTONE_SPRAYER);
		
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.LARGE_REDSTONE_MAGAZINE, 1), "x x", "x x", "xgx",
				'x', Items.IRON_INGOT, 'g', Blocks.REDSTONE_BLOCK);

		// One Seed Sweeper (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.SEED_SWEEPER, 1), " i ", "ipi", " it", 'p',
				Blocks.PISTON, 'i', Items.IRON_INGOT, 't', Blocks.TRIPWIRE_HOOK);
		addMagazineLoadRecipe(SeedJar.class, ItemRegistry.SEED_SWEEPER);

		// One Sugar Gatling (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.SUGAR_ENGINE, 1), "b b", "b b", " m ", 'b',
				new ItemStack(ItemRegistry.PART_SUGAR_ENGINE_BARREL), 'm',
				new ItemStack(ItemRegistry.PART_SUGAR_ENGINE_BODY));
		addMagazineLoadRecipe(GatlingAmmo.class, ItemRegistry.SUGAR_ENGINE);

		// One Thorn Spitter (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.THORN_SPITTER, 1), "bib", "php", "sts", 't',
				Blocks.TRIPWIRE_HOOK, 'b', Blocks.IRON_BARS, 'i', Items.IRON_INGOT, 'h', Blocks.HOPPER, 's',
				Blocks.STICKY_PISTON, 'p', Blocks.PISTON);
		addMagazineLoadRecipe(NeedleMagazine.class, ItemRegistry.THORN_SPITTER);

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.POTATOSSER, 1), "xax", "zbx", "cdy", 'a',
				Blocks.TRAPDOOR, 'b', Blocks.PISTON, 'c', Blocks.TRIPWIRE_HOOK, 'd', Blocks.STICKY_PISTON, 'x',
				Blocks.IRON_BARS, 'y', Items.IRON_INGOT, 'z', Items.FLINT_AND_STEEL);
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Items.COAL, new int[]{0, 1, 1});
			components.put(Items.POTATO, new int[]{1, 1, 7});
			addAmmoLoadRecipe(ItemRegistry.POTATOSSER, components);
		}

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.THORN_MAGAZINE, 1), "x x", "x x", "xix", 'x',
				Items.LEATHER, 'i', Items.IRON_INGOT);

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.ENDER_QUARTZ_MAGAZINE, 1), "xxx", "ixi", "iii", 'x',
				Items.QUARTZ, 'i', Items.IRON_INGOT);

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.SUGAR_MAGAZINE, 1), "y y", "y y", "yxy", 'x',
				Items.IRON_INGOT, 'y', Blocks.PLANKS);

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.AUTO_CROSSBOW_IMP, 1), "iii", "scs", " i ", 'i',
				Items.IRON_INGOT, 's', Blocks.STICKY_PISTON, 'c',
				Helper.getWeaponStackByClass(CrossbowAuto.class, true));
		// Fill what can be filled. One arrow bundle for 8 shots, for up to 2
		// bundles
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Helper.getAmmoStack(ArrowBundle.class, 0), new int[]{8, 1, 2});
			addAmmoLoadRecipe(ItemRegistry.AUTO_CROSSBOW_IMP, components);
		}

		// One auto-crossbow (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.AUTO_CROSSBOW, 1), "iii", "pcp", " t ", 'i',
				Items.IRON_INGOT, 'p', Blocks.PISTON, 't', Blocks.TRIPWIRE_HOOK, 'c',
				Helper.getWeaponStackByClass(CrossbowDouble.class, true));
		addShapelessRecipe(new ItemStack(ItemRegistry.AUTO_CROSSBOW), // Fill
				// the
				// empty
				// auto-crossbow
				// with one arrow
				// bundle
				Helper.getAmmoStack(ArrowBundle.class, 0),
				Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.AUTO_CROSSBOW, 1));

		// One redstone sprayer (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.SNOW_CANNON, 1), "zxz", "zbz", "aya", 'x',
				Blocks.PISTON, 'y', Blocks.TRIPWIRE_HOOK, 'z', Blocks.WOOL, 'a', Blocks.OBSIDIAN, 'b',
				Blocks.STICKY_PISTON);
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Blocks.SNOW, new int[]{4, 1, 8});
			addAmmoLoadRecipe(ItemRegistry.SNOW_CANNON, components);
		}

		// One Fen Fire (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.FEN_FIRE, 1), "di ", "i i", " ts", 't',
				Blocks.TRIPWIRE_HOOK, 'i', Items.IRON_INGOT, 's', Blocks.STICKY_PISTON, 'd', Blocks.TRAPDOOR);
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Blocks.GLOWSTONE, new int[]{4, 1, 8});
			addAmmoLoadRecipe(ItemRegistry.FEN_FIRE, components);
		}

		// One QuadBox (empty) An upgrade from the regular Dragonbox (so 3
		// more flint&steel + Pistons for reloading mechanism + more
		// barrels)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.QUAD_DRAGONBOX, 1), "ddd", "pdp", "sts", 'p',
				Blocks.PISTON, 's', Blocks.STICKY_PISTON, 't', Blocks.TRIPWIRE_HOOK, 'd',
				Helper.getWeaponStackByClass(DragonBox.class, true));
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Helper.getAmmoStack(RocketBundle.class, 0), new int[]{8, 1, 8});
			addAmmoLoadRecipe(ItemRegistry.QUAD_DRAGONBOX, components);
		}

		// One Dragon Mortar (Empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.DRAGON_MORTAR, 1), "ipi", "isr", "tsf", 't',
				Blocks.TRIPWIRE_HOOK, 'i', Items.IRON_INGOT, 's', Blocks.STICKY_PISTON, 'p', Blocks.PISTON, 'r',
				Items.REPEATER, 'f', Items.FLINT_AND_STEEL);
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Helper.getAmmoStack(RocketBundle.class, 0), new int[]{1, 1, 8});
			addAmmoLoadRecipe(ItemRegistry.DRAGON_MORTAR, components);
		}

		// One Firework Rocket Launcher (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.ROCKET_LAUNCHER, 1), "x  ", "yx ", "zyx", 'x',
				Blocks.PLANKS, 'y', Items.IRON_INGOT, 'z', Items.FLINT_AND_STEEL);
		// Fill the RPG with 1 rocket
		addRecipe(new ItemStack(ItemRegistry.ROCKET_LAUNCHER), " ab", "zya", " x ", 'x',
				Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.ROCKET_LAUNCHER, 1), 'y', Blocks.TNT, 'z',
				Blocks.PLANKS, 'a', Items.PAPER, 'b', Items.STRING);

		addRecipe(new ItemStack(ItemRegistry.ROCKET_BUNDLE), "xxx", "xyx", "xxx", 'x', Items.FIREWORKS, 'y',
				Items.STRING);
		// Bundle of rockets back to 8 rockets
		addShapelessRecipe(new ItemStack(Items.FIREWORKS, 8), new ItemStack(ItemRegistry.ROCKET_BUNDLE));

		// One compact crossbow (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.COMPACT_CROSSBOW, 1), "zxy", "xzy", "zxy", 'x',
				Items.STICK, 'y', Items.STRING, 'z', Blocks.PLANKS);
		addShapelessRecipe(new ItemStack(ItemRegistry.COMPACT_CROSSBOW), // Fill
				// the
				// empty
				// crossbow with
				// one arrow
				Items.ARROW, Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.COMPACT_CROSSBOW, 1));

		// One Soul Cairn (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.SOUL_CAIRN, 1), "e e", "epe", "oto", 'o',
				Blocks.OBSIDIAN, 'e', Blocks.END_STONE, 't', Blocks.TRIPWIRE_HOOK, 'p', Blocks.PISTON);
		addShapelessRecipe(new ItemStack(ItemRegistry.SOUL_CAIRN), Items.DIAMOND,
				Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.SOUL_CAIRN, 1));

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.SILKEN_SPINNER, 1), "ihi", "gpg", "tsi", 'p',
				Blocks.PISTON, 's', Blocks.STICKY_PISTON, 't', Blocks.TRIPWIRE_HOOK, 'i', Items.IRON_INGOT, 'h',
				Blocks.HOPPER, 'g', Blocks.GLASS_PANE);
		// Making web out of string
		addRecipe(new ItemStack(Blocks.WEB), "s s", " s ", "s s", 's', Items.STRING);
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Blocks.WEB, new int[]{1, 1, 8});
			addAmmoLoadRecipe(ItemRegistry.SILKEN_SPINNER, components);
		}

		// One Seedling (fully loaded, meaning 0 damage)
		addRecipe(new ItemStack(ItemRegistry.SEEDLING, 1, 0), "ada", "ada", "bca", 'a', Items.REEDS, 'b',
				Blocks.TRIPWIRE_HOOK, 'c', Blocks.PISTON, 'd', Blocks.MELON_BLOCK);

		// One Improved Rocket Launcher (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.ROCKET_LAUNCHER_IMP, 1), "xxx", "yzy", "xxx", 'x',
				Blocks.OBSIDIAN, // Adding an obsidian frame
				// to the RPG
				'y', Items.IRON_INGOT, 'z', Helper.getWeaponStackByClass(RPG.class, true));
		// Fill the launcher with 1 big rocket
		addShapelessRecipe(new ItemStack(ItemRegistry.ROCKET_LAUNCHER_IMP), Helper.getAmmoStack(LargeRocket.class, 0),
				new ItemStack(ItemRegistry.ROCKET_LAUNCHER_IMP, 1, 1));

		// One quiverbow with 256 damage value (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.QUIVERBOW, 1), "zxy", "xzy", "zxy", 'x', Items.STICK,
				'y', Items.STRING, 'z', Items.LEATHER);
		// Ammo
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Helper.getAmmoStack(ArrowBundle.class, 0), new int[]{8, 1, 8});
			addAmmoLoadRecipe(ItemRegistry.QUIVERBOW, components);
		}

		// Modifying the powder knuckle once
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.POWDER_KNUCKLES_MOD, 1), "ooo", "oco", "i i", 'c',
				Helper.getWeaponStackByClass(PowderKnuckle.class, true), 'o', Blocks.OBSIDIAN, 'i', Items.IRON_INGOT);
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Items.GUNPOWDER, new int[]{1, 1, 8});
			addAmmoLoadRecipe(ItemRegistry.POWDER_KNUCKLES_MOD, components);
		}

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.POWDER_KNUCKLES, 1), "yyy", "xzx", "x x", 'x',
				Items.LEATHER, 'y', Items.IRON_INGOT, 'z', Items.STICK);
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Items.GUNPOWDER, new int[]{1, 1, 8});
			addAmmoLoadRecipe(ItemRegistry.POWDER_KNUCKLES, components);
		}

		// One wither rifle (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.WITHER_RIFLE, 1), "odo", "owo", "oso", 'o',
				Blocks.OBSIDIAN, 'd', Items.DIAMOND, 's', Items.NETHER_STAR, 'w',
				Helper.getWeaponStackByClass(OSR.class, true));
		// Reloading with obsidian magazine, setting its ammo metadata as ours
		addMagazineLoadRecipe(ObsidianMagazine.class, ItemRegistry.WITHER_RIFLE);

		// One Arrow Mortar (Empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.ARROW_MORTAR, 1), "ipi", "isr", "tsr", 't',
				Blocks.TRIPWIRE_HOOK, 'i', Items.IRON_INGOT, 's', Blocks.STICKY_PISTON, 'p', Blocks.PISTON, 'r',
				Items.REPEATER);
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Helper.getAmmoStack(ArrowBundle.class, 0), new int[]{1, 1, 8});
		}

		// One Frost Lancer (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.FROST_LANCER, 1), "qiq", "prs", " o ", 'o',
				Blocks.OBSIDIAN, 'q', Items.QUARTZ, 'i', Items.IRON_INGOT, 'p', Blocks.PISTON, 's',
				Blocks.STICKY_PISTON, 'r', Helper.getWeaponStackByClass(EnderRifle.class, true) // One
		// empty
		// Ender
		// Rifle
		);
		// Reloading with one Frost Clip
		addShapelessRecipe(new ItemStack(ItemRegistry.FROST_LANCER),
				Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.FROST_LANCER, 1),
				Helper.getAmmoStack(ColdIronClip.class, 0));

		// One ender rifle (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.ENDER_RIFLE, 1), "aza", "bcy", "xzx", 'x',
				Blocks.OBSIDIAN, 'y', Blocks.TRIPWIRE_HOOK, 'z', Items.IRON_INGOT, 'a', Items.ENDER_EYE, 'b',
				Blocks.PISTON, 'c', Blocks.STICKY_PISTON);
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Items.IRON_INGOT, new int[]{1, 1, 8});
			addAmmoLoadRecipe(ItemRegistry.ENDER_RIFLE, components);
		}

		// One ender bow, all ready
		addRecipe(new ItemStack(ItemRegistry.ENDER_BOW), "zxy", "xay", "zxy", 'x', Items.STICK, 'y', Items.STRING, 'z',
				Items.ENDER_EYE, 'a', Items.IRON_INGOT);

		// One dragonbox (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.DRAGONBOX, 1), "zxy", "azy", "zxy", 'x', Items.STICK,
				'y', Items.STRING, 'z', Items.IRON_INGOT, 'a', Items.FLINT_AND_STEEL);
		{
			Map<Object, int[]> components = new HashMap<>();
			components.put(Helper.getAmmoStack(RocketBundle.class, 0), new int[]{8, 1, 8});
			addAmmoLoadRecipe(ItemRegistry.DRAGONBOX, components);
		}

		// One blaze crossbow (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.BLAZE_CROSSBOW, 1), "bib", "ici", "bib", 'b',
				Items.BLAZE_POWDER, 'i', Items.IRON_INGOT, 'c',
				Helper.getWeaponStackByClass(CrossbowCompact.class, true));
		addShapelessRecipe(new ItemStack(ItemRegistry.BLAZE_CROSSBOW), Items.BLAZE_ROD,
				Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.BLAZE_CROSSBOW, 1));

		// One Aqua Accelerator (empty)
		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.AQUA_ACCELERATOR, 1), "ihi", "gpg", "iti", 'p',
				Blocks.PISTON, 't', Blocks.TRIPWIRE_HOOK, 'i', Items.IRON_INGOT, 'h', Blocks.HOPPER, 'g',
				Blocks.GLASS_PANE);
		// Fill the AA with one water bucket
		addShapelessRecipe(new ItemStack(ItemRegistry.AQUA_ACCELERATOR), Items.WATER_BUCKET,
				Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.AQUA_ACCELERATOR, 1));

		addRecipe(new ItemStack(ItemRegistry.PART_SUGAR_ENGINE_BODY), "rir", "ror", "tpb", 'o', Blocks.OBSIDIAN, 'i',
				Items.IRON_INGOT, 't', Blocks.TRIPWIRE_HOOK, 'r', Items.REPEATER, 'p', Blocks.PLANKS, 'b',
				Blocks.PISTON);

		// Sugar Gatling, barrel
		// Piston accelerators? Sticky, regular + iron walls
		addRecipe(new ItemStack(ItemRegistry.PART_SUGAR_ENGINE_BARREL), "i i", "ipi", "isi", 'i', Items.IRON_INGOT, 'p',
				Blocks.PISTON, 's', Blocks.STICKY_PISTON);

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.SEED_JAR, 1), "gwg", "g g", "gig", 'g',
				Blocks.GLASS_PANE, 'i', Items.IRON_INGOT, 'w', Blocks.WOODEN_BUTTON);

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.OBSIDIAN_MAGAZINE, 1), "x x", "x x", "xox", 'x',
				Items.IRON_INGOT, 'o', Blocks.OBSIDIAN);

		// A big rocket
		addRecipe(new ItemStack(ItemRegistry.LARGE_ROCKET), "zaa", "aya", "aab", 'y', Blocks.TNT, 'z', Blocks.PLANKS,
				'a', Items.PAPER, 'b', Items.STRING);

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.LARGE_NETHERRACK_MAGAZINE, 1), "x x", "x x", "xgx",
				'x', Blocks.NETHER_BRICK, 'g', Items.IRON_INGOT);

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.LAPIS_MAGAZINE, 1), "x x", "x x", "xgx", 'x',
				Blocks.GLASS_PANE, 'g', new ItemStack(Items.DYE, 1, 4));

		addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.GOLD_MAGAZINE, 1), "x x", "x x", "xgx", 'x',
				Items.IRON_INGOT, 'g', Items.GOLD_INGOT);

		// A bundle of ice-laced iron ingots (4), merged with a slime ball
		addShapelessRecipe(new ItemStack(ItemRegistry.COLD_IRON_CLIP), Items.IRON_INGOT, Items.IRON_INGOT,
				Items.IRON_INGOT, Items.IRON_INGOT, Blocks.ICE, Blocks.ICE, Blocks.ICE, Blocks.ICE, Items.SLIME_BALL);

		

		// One arrow bundle, holding 8 arrows
		addRecipe(new ItemStack(ItemRegistry.ARROW_BUNDLE), "xxx", "xyx", "xxx", 'x', Items.ARROW, 'y', Items.STRING);
		// Bundle of arrows back to 8 arrows
		addShapelessRecipe(new ItemStack(Items.ARROW, 8), new ItemStack(ItemRegistry.ARROW_BUNDLE));
		
		addShapelessRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.DOUBLE_CROSSBOW, 1), Blocks.STICKY_PISTON,
				Items.REPEATER, Helper.getWeaponStackByClass(CrossbowCompact.class, true));
		addShapelessRecipe(new ItemStack(ItemRegistry.DOUBLE_CROSSBOW), // Fill the empty
				// crossbow with
				// two arrows
				Items.ARROW, Items.ARROW, Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.DOUBLE_CROSSBOW, 1));
		addShapelessRecipe(new ItemStack(ItemRegistry.DOUBLE_CROSSBOW, 1, 1), // Fill the
				// empty
				// crossbow
				// with one
				// arrow
				Items.ARROW, Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.DOUBLE_CROSSBOW, 1));
		addShapelessRecipe(new ItemStack(ItemRegistry.DOUBLE_CROSSBOW), // Fill the half
				// empty crossbow
				// with one arrow
				Items.ARROW, new ItemStack(ItemRegistry.DOUBLE_CROSSBOW, 1, 1));

		/*TODO: Reimplement Arms Assistant
		addRecipe(new ItemStack(ItemRegistry.ARMS_ASSISTANT), "ewe", "ibi", "ppp", 'w',
				new ItemStack(Items.SKULL, 1, 1), 'e', Items.ENDER_EYE, 'b', Blocks.IRON_BLOCK, 'i', Items.IRON_INGOT,
				'p', Blocks.STICKY_PISTON);
		// TODO: Reimplement Arms Assistant Upgrades
		addRecipe(new ItemStack(ItemRegistry.AA_TARGET_ASSISTANT), "bi ", "iri", " it", 'b', Blocks.NOTEBLOCK, 'r',
				Items.REPEATER, 't', Blocks.TRIPWIRE_HOOK, 'i', Items.IRON_INGOT);
		*/
		
		/*TODO: Reimplement Beam weapons
		// Use a beacon for this (+ obsidian, tripwire hook... what else)
				addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.RAY_OF_HOPE, 1), "bi ", "ico", " ot", 'b',
						Blocks.BEACON, 'o', Blocks.OBSIDIAN, 't', Blocks.TRIPWIRE_HOOK, 'c', Items.CAULDRON, 'i',
						Items.IRON_INGOT);
				ArrayList<ItemStack> list = new ArrayList<ItemStack>();
				list.add(new ItemStack(Items.POTIONITEM, 1, 8193));
				list.add(new ItemStack(Items.POTIONITEM, 1, 8225));
				{
					Map<Object, int[]> components = new HashMap<>();
					components.put(new ItemStack(Items.POTIONITEM, 1, 8193), new int[]{20, 0, 8});
					components.put(new ItemStack(Items.POTIONITEM, 1, 8225), new int[]{40, 0, 8});
					addAmmoLoadRecipe(ItemRegistry.RAY_OF_HOPE, components);
				}
				// One Lightning Red (empty)
				addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.LIGHTNING_RED, 1), "q q", "qiq", "iti", 'q',
						Items.QUARTZ, 'i', Items.IRON_INGOT, 't', Blocks.TRIPWIRE_HOOK);
				addMagazineLoadRecipe(RedstoneMagazine.class, ItemRegistry.LIGHTNING_RED);
				addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.REDSTONE_MAGAZINE, 1), "x x", "x x", "xgx", 'x',
				Items.IRON_INGOT, 'g', Items.REDSTONE);
				// One Flint Duster (Empty)
				addRecipe(Helper.createEmptyWeaponOrAmmoStack(ItemRegistry.FLINT_DUSTER, 1), "qhq", "qpq", "tsi", 'p',
						Blocks.PISTON, 's', Blocks.STICKY_PISTON, 'h', Blocks.HOPPER, 'q', Blocks.QUARTZ_BLOCK, 'i',
						Items.IRON_INGOT, 't', Blocks.TRIPWIRE_HOOK);
				{
					Map<Object, int[]> components = new HashMap<>();
					components.put(Helper.getAmmoStack(BoxOfFlintDust.class, 0), new int[]{32, 1, 8});
					addAmmoLoadRecipe(ItemRegistry.FLINT_DUSTER, components);
				}
				// A box of flint dust (4 dust per flint, meaning 32 per box), merged
				// with wooden planks
				addShapelessRecipe(new ItemStack(ItemRegistry.BOX_OF_FLINT_DUST), Items.FLINT, Items.FLINT, Items.FLINT,
				Items.FLINT, Items.FLINT, Items.FLINT, Items.FLINT, Items.FLINT, Blocks.PLANKS);
				// Using a beacon and solar panels/Daylight Sensors, meaning a
				// nether star is required. So this is a high power item
				addRecipe(new ItemStack(ItemRegistry.SUNRAY), "bs ", "oos", " rt", 'b', Blocks.BEACON, 'o', Blocks.OBSIDIAN,
						's', Blocks.DAYLIGHT_DETECTOR, 't', Blocks.TRIPWIRE_HOOK, 'r', Items.REPEATER);
		*/

		generateConstants();
	}

	private static void setupDir()
	{
		if (RECIPE_DIR == null)
		{
			RECIPE_DIR = new File(
					"C:\\Users\\David\\Documents\\Minecraft\\Modding Workspace\\=Contributions=\\minecraft-quiverbow\\src\\main\\resources\\assets\\quiverchevsky\\recipes");
		}

		if (!RECIPE_DIR.exists())
		{
			RECIPE_DIR.mkdir();
		}
	}

	private static void addAmmoLoadRecipe(Item weapon, Map<Object, int[]> components)
	{
		setupDir();
		Map<String, Object> json = new LinkedHashMap<>();

		json.put("type", Quiverbow.MODID + ":load_ammo");
		json.put("weapon", weapon.getRegistryName().toString());

		List<Map<String, Object>> componentList = new ArrayList<>();
		for (Entry<Object, int[]> entry : components.entrySet())
		{
			componentList.add(serialiseIngredientAmmoDataEntry(entry));
		}
		json.put("components", componentList);

		File subDir = new File(RECIPE_DIR + "\\ammoloading");
		subDir.mkdirs();
		File f = new File(subDir, weapon.getRegistryName().getResourcePath() + "_load.json");
		
		try (FileWriter w = new FileWriter(f))
		{
			GSON.toJson(json, w);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static Map<String, Object> serialiseIngredientAmmoDataEntry(Map.Entry<Object, int[]> entry)
	{
		Map<String, Object> entryJSON = new LinkedHashMap<>();
		entryJSON.put("ing", serializeItem(entry.getKey()));
		entryJSON.put("data", serialiseAmmoData(entry.getValue()));
		return entryJSON;
	}

	private static Map<String, Object> serialiseAmmoData(int[] data)
	{
		Map<String, Object> dataJSON = new LinkedHashMap<>();
		dataJSON.put("ammoValue", data[0]);
		dataJSON.put("min", data[1]);
		dataJSON.put("max", data[2]);
		return dataJSON;
	}

	private static void addMagazineLoadRecipe(Class<? extends AmmoBase> ammoBase, Item weapon)
	{
		setupDir();
		Map<String, Object> json = new LinkedHashMap<>();

		json.put("type", Quiverbow.MODID + ":load_magazine");
		json.put("ammo", Helper.getAmmoByClass(ammoBase).getRegistryName().toString());
		json.put("weapon", weapon.getRegistryName().toString());

		Map<String, String> enabledCondition = new LinkedHashMap<>();
		enabledCondition.put("type", Quiverbow.MODID + ":weapon_enabled");
		enabledCondition.put("id", weapon.getRegistryName().toString());
		List<Map<String, String>> conditions = Lists.newArrayList(enabledCondition);
		json.put("conditions", conditions);
		
		File subDir = new File(RECIPE_DIR + "\\ammoloading");
		subDir.mkdirs();
		File f = new File(subDir, weapon.getRegistryName().getResourcePath() + "_load.json");

		try (FileWriter w = new FileWriter(f))
		{
			GSON.toJson(json, w);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void addRecipe(ItemStack result, Object... components)
	{
		setupDir();

		// GameRegistry.addShapedRecipe(result, components);

		Map<String, Object> json = new LinkedHashMap<>();

		if (result.getItem() instanceof WeaponBase)
		{
			Map<String, String> enabledCondition = new LinkedHashMap<>();
			enabledCondition.put("type", Quiverbow.MODID + ":weapon_enabled");
			enabledCondition.put("id", result.getItem().getRegistryName().toString());
			List<Map<String, String>> conditions = Lists.newArrayList(enabledCondition);
			json.put("conditions", conditions);
		}

		List<String> pattern = new ArrayList<>();
		int i = 0;
		while (i < components.length && components[i] instanceof String)
		{
			pattern.add((String) components[i]);
			i++;
		}
		json.put("pattern", pattern);

		boolean isOreDict = false;
		Map<String, Map<String, Object>> key = new HashMap<>();
		Character curKey = null;
		for (; i < components.length; i++)
		{
			Object o = components[i];
			if (o instanceof Character)
			{
				if (curKey != null) throw new IllegalArgumentException("Provided two char keys in a row");
				curKey = (Character) o;
			}
			else
			{
				if (curKey == null) throw new IllegalArgumentException("Providing object without a char key");
				if (o instanceof String) isOreDict = true;
				key.put(Character.toString(curKey), serializeItem(o));
				curKey = null;
			}
		}
		json.put("key", key);
		json.put("type", isOreDict ? "forge:ore_shaped" : "minecraft:crafting_shaped");
		json.put("result", serializeItem(result));

		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		String subfolder = result.getItem() instanceof AmmoBase ? "\\ammo"
				: result.getItem() instanceof WeaponBase ? "\\weapons" : "";
		if (result.getItem() instanceof WeaponBase || result.getItem() instanceof AmmoMagazine)
		{
			if (result.getItemDamage() == result.getMaxDamage()) suffix = "_empty";
			else if (result.getItemDamage() == 0) suffix = "_loaded";
		}
		File subDir = new File(RECIPE_DIR + subfolder);
		subDir.mkdirs();
		File f = new File(subDir,
				result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		while (f.exists())
		{
			suffix += "_alt";
			f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		}

		try (FileWriter w = new FileWriter(f))
		{
			GSON.toJson(json, w);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void addShapelessRecipe(ItemStack result, Object... components)
	{
		setupDir();

		// addShapelessRecipe(result, components);

		Map<String, Object> json = new LinkedHashMap<>();

		if (result.getItem() instanceof WeaponBase)
		{
			Map<String, String> enabledCondition = new LinkedHashMap<>();
			enabledCondition.put("type", Quiverbow.MODID + ":weapon_enabled");
			enabledCondition.put("id", result.getItem().getRegistryName().toString());
			List<Map<String, String>> conditions = Lists.newArrayList(enabledCondition);
			json.put("conditions", conditions);
		}

		boolean isOreDict = false;
		List<Map<String, Object>> ingredients = new ArrayList<>();
		for (Object o : components)
		{
			if (o instanceof String) isOreDict = true;
			ingredients.add(serializeItem(o));
		}
		json.put("ingredients", ingredients);
		json.put("type", isOreDict ? "forge:ore_shapeless" : "minecraft:crafting_shapeless");
		json.put("result", serializeItem(result));

		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		String subfolder = result.getItem() instanceof AmmoBase ? "\\ammo"
				: result.getItem() instanceof WeaponBase ? "\\weapons" : "";
		if (result.getItem() instanceof WeaponBase || result.getItem() instanceof AmmoMagazine)
		{
			if (result.getItemDamage() == result.getMaxDamage()) suffix = "_empty";
			else if (result.getItemDamage() == 0) suffix = "_loaded";
		}
		
		File subDir = new File(RECIPE_DIR + subfolder);
		subDir.mkdirs();
		File f = new File(subDir, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		while (f.exists())
		{
			suffix += "_alt";
			f = new File(subDir, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		}

		try (FileWriter w = new FileWriter(f))
		{
			GSON.toJson(json, w);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static Map<String, Object> serializeItem(Object thing)
	{
		if (thing instanceof Item)
		{
			return serializeItem(new ItemStack((Item) thing));
		}
		if (thing instanceof Block)
		{
			return serializeItem(new ItemStack((Block) thing));
		}
		if (thing instanceof ItemStack)
		{
			ItemStack stack = (ItemStack) thing;
			Map<String, Object> ret = new HashMap<>();
			ret.put("item", stack.getItem().getRegistryName().toString());
			if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0)
			{
				ret.put("data", stack.getItemDamage());
			}
			if (stack.getCount() > 1)
			{
				ret.put("count", stack.getCount());
			}

			if (stack.hasTagCompound())
			{
				ret.put("type", "minecraft:item_nbt");
				ret.put("nbt", stack.getTagCompound().toString());
			}

			return ret;
		}
		if (thing instanceof String)
		{
			Map<String, Object> ret = new HashMap<>();
			USED_OD_NAMES.add((String) thing);
			ret.put("item", "#" + ((String) thing).toUpperCase(Locale.ROOT));
			return ret;
		}

		throw new IllegalArgumentException("Not a block, item, stack, or od name");
	}

	// Call this after you are done generating
	private static void generateConstants()
	{
		List<Map<String, Object>> json = new ArrayList<>();
		for (String s : USED_OD_NAMES)
		{
			Map<String, Object> entry = new HashMap<>();
			entry.put("name", s.toUpperCase(Locale.ROOT));
			entry.put("ingredient", ImmutableMap.of("type", "forge:ore_dict", "ore", s));
			json.add(entry);
		}

		try (FileWriter w = new FileWriter(new File(RECIPE_DIR, "_constants.json")))
		{
			GSON.toJson(json, w);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
