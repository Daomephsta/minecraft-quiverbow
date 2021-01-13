package com.domochevsky.quiverbow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import com.domochevsky.quiverbow.accessor.BlockAccessors;
import com.domochevsky.quiverbow.ammo.AmmoBase;
import com.domochevsky.quiverbow.ammo.AmmoMagazine;
import com.domochevsky.quiverbow.ammo.LapisMagazine;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry;
import com.domochevsky.quiverbow.armsassistant.EntityArmsAssistant;
import com.domochevsky.quiverbow.blocks.FenLight;
import com.domochevsky.quiverbow.config.QuiverbowConfig;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.domochevsky.quiverbow.loot.LootHandler;
import com.domochevsky.quiverbow.miscitems.IncompleteEnderRailAccelerator;
import com.domochevsky.quiverbow.miscitems.PackedUpAA;
import com.domochevsky.quiverbow.miscitems.QuiverBowItem;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.net.PacketHandler;
import com.domochevsky.quiverbow.projectiles.*;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.util.RegistryHelper;
import com.domochevsky.quiverbow.weapons.AATargeter;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.domochevsky.quiverbow.weapons.base.Weapon.Effect;
import com.domochevsky.quiverbow.weapons.base.ammosource.*;
import com.domochevsky.quiverbow.weapons.base.effects.DamageWeapon;
import com.domochevsky.quiverbow.weapons.base.effects.Knockback;
import com.domochevsky.quiverbow.weapons.base.effects.PlaySound;
import com.domochevsky.quiverbow.weapons.base.effects.SpawnParticle;
import com.domochevsky.quiverbow.weapons.base.fireshape.*;
import com.domochevsky.quiverbow.weapons.base.trigger.*;
import com.google.common.collect.Lists;

import daomephsta.umbra.resources.ResourceLocationExt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.ForgeEventFactory;
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

@Mod(modid = QuiverbowMain.MODID, name = QuiverbowMain.NAME, version = "GRADLE:VERSION")
public class QuiverbowMain
{
	public static final String NAME = "QuiverBow: Restrung";
	public static final String MODID = "quiverbow_restrung";

	@Instance(QuiverbowMain.MODID)
	public static QuiverbowMain instance;

	@SidedProxy(clientSide = "com.domochevsky.quiverbow.ClientProxy", serverSide = "com.domochevsky.quiverbow.CommonProxy")
	public static CommonProxy proxy;

	public static Logger logger;

	public static ArrayList<Weapon> weapons = new ArrayList<>();
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
					RegistryHelper.registerItem(new PackedUpAA(), ".misc.", "arms_assistant"),
					RegistryHelper.registerItem(new AATargeter(), ".misc.", "aa_target_assistant"));
		}

		private static void registerWeapons(IForgeRegistry<Item> registry)
		{
	        ProjectileFactory crossbowBolt = (world, shooter, properties) ->
	        {
                // Use rider if being ridden to prevent arrows hitting rider upon firing
                if (shooter.getControllingPassenger() instanceof EntityLivingBase)
                    shooter = (EntityLivingBase) shooter.getControllingPassenger();
	            EntityArrow entityarrow = Helper.createArrow(world, shooter);
	            entityarrow.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F,
	                properties.getProjectileSpeed(), 0.5F);
	            //TODO Make actual crossbow bolt projectile, with appropriate render
	            // Divide by speed because this base damage will be multiplied by the speed
	            entityarrow.setDamage(Math.round(properties.generateDamage(world.rand) /
	                properties.getProjectileSpeed()));
	            entityarrow.setKnockbackStrength(properties.getKnockback());
	            return entityarrow;
	        };
			registry.registerAll(
                addWeapon("compact_crossbow",
                    builder -> builder.minimumDamage(14).maximumDamage(20)
                        .projectileSpeed(2.5F).knockback(2).cooldown(25).mobUsable()
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new SemiAutomaticTrigger(new SimpleAmmoSource(1), new SingleShotFireShape(crossbowBolt)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 0.5F)),
                addWeapon("double_crossbow",
                    builder -> builder.minimumDamage(14).maximumDamage(20)
                        .projectileSpeed(2.5F).knockback(2).cooldown(25).mobUsable()
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new SemiAutomaticTrigger(new SimpleAmmoSource(2), new SingleShotFireShape(crossbowBolt)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 0.5F))
                    .cooldownEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.4F)),
				addWeapon("blaze_crossbow",
                    builder -> builder.minimumDamage(20).maximumDamage(30)
                        .projectileSpeed(3.0F).knockback(2).cooldown(10).mobUsable()
                        .intProperty(CommonProperties.FIRE_DUR_ENTITY, 15)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new SemiAutomaticTrigger(new SimpleAmmoSource(1), new SingleShotFireShape(BlazeShot::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 0.5F),
                    new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.5F),
                    new SpawnParticle(EnumParticleTypes.SMOKE_NORMAL, 0.5D)),
				addWeapon("autoloader_crossbow",
                    builder -> builder.minimumDamage(10).maximumDamage(16)
                        .projectileSpeed(2.5F).knockback(1).cooldown(8).mobUsable()
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new AutoLoadingTrigger(new SimpleAmmoSource(8), new SingleShotFireShape(crossbowBolt)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 0.5F)),
				addWeapon("auto_crossbow",
                    builder -> builder.minimumDamage(10).maximumDamage(16)
                        .projectileSpeed(2.5F).knockback(1).cooldown(8).mobUsable()
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new AutomaticTrigger(new SimpleAmmoSource(16), new SingleShotFireShape(crossbowBolt)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 0.5F)));
			ProjectileFactory arrow = (world, user, properties) ->
			{
			    float seconds = (user.getActiveItemStack().getMaxItemUseDuration() - user.getItemInUseCount()) / 20.0F;
			    float velocity = Math.min(seconds * (seconds + 2.0F) / 3.0F, 1.0F);
			    EntityArrow entityarrow = Helper.createArrow(world, user);
			    entityarrow.shoot(user, user.rotationPitch, user.rotationYaw, 0.0F, velocity * 3.0F, 1.0F);
			    if (velocity >= 1.0F)
			        entityarrow.setIsCritical(true);
			    entityarrow.pickupStatus = user instanceof EntityPlayer
			        && ((EntityPlayer) user).capabilities.isCreativeMode
			        ? EntityArrow.PickupStatus.CREATIVE_ONLY
			            : EntityArrow.PickupStatus.ALLOWED;
			    return entityarrow;
			};
            Effect arrowSound = (world, user, stack, properties) ->
            {
                float seconds = (user.getActiveItemStack().getMaxItemUseDuration() - user.getItemInUseCount()) / 20.0F;
                float velocity = Math.min(seconds * (seconds + 2.0F) / 3.0F, 1.0F);
                float pitch = 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F;
                Helper.playSoundAtEntityPos(user, SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, pitch);
            };
			registry.registerAll(
    			addWeapon("quiverbow",
                    builder -> builder
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new DrawnTrigger(new SimpleAmmoSource(256), new SingleShotFireShape(arrow)))
                    .fireEffects(arrowSound),
                addWeapon("ender_bow",
                    builder -> builder.floatProperty(CommonProperties.MAX_ZOOM, 30),
                    new DrawnTrigger(new InventoryAmmoSource(Items.ARROW), new SingleShotFireShape(arrow)))
                    .fireEffects(new DamageWeapon(), arrowSound)
                    .setMaxDamage(256)
            );
            Effect breakIfEmpty = (world, shooter, stack, properties) ->
            {
                if (stack.getItemDamage() >= stack.getMaxDamage())
                {
                    shooter.renderBrokenItemStack(stack);
                    stack.setCount(0);
                    if (!world.isRemote)
                    {
                        shooter.entityDropItem(new ItemStack(Blocks.PISTON), 1.0F);
                        shooter.entityDropItem(new ItemStack(Blocks.TRIPWIRE_HOOK), 1.0F);
                    }
                    Helper.playSoundAtEntityPos(shooter, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.5F);
                }
            };
            registry.register(addWeapon("seedling",
                    builder -> builder.minimumDamage(1)
                        .maximumDamage(1).projectileSpeed(1.3F).mobUsable()
                        .floatProperty(CommonProperties.SPREAD, 5.0F)
                        .intProperty(SpreadFireShape.PROJECTILES, 1)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new AutomaticTrigger(new SimpleAmmoSource(32), new SpreadFireShape(Seed::new)))
                    .fireEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 0.7F),
                        breakIfEmpty)
            );
			registry.registerAll(
				addWeapon("dragonbox",
					builder -> builder.minimumDamage(4).maximumDamage(6)
					    .projectileSpeed(1.3F).knockback(2).kickback(1).cooldown(10).mobUsable()
		                .intProperty(CommonProperties.FIRE_DUR_ENTITY, 6)
		                .floatProperty(CommonProperties.EXPLOSION_SIZE, 1.0F)
		                .booleanProperty(CommonProperties.DAMAGE_TERRAIN, true)
	                    .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
		            new AutomaticTrigger(new SimpleAmmoSource(64), new SingleShotFireShape(SmallRocket::new)))
					.fireEffects(new PlaySound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 1.0F)),
				addWeapon("quad_dragonbox",
                    builder -> builder.minimumDamage(4).maximumDamage(6)
                        .projectileSpeed(1.3F).knockback(2).kickback(1).cooldown(10).mobUsable()
                        .intProperty(CommonProperties.FIRE_DUR_ENTITY, 6)
                        .floatProperty(CommonProperties.EXPLOSION_SIZE, 1.0F)
                        .booleanProperty(CommonProperties.DAMAGE_TERRAIN, true)
                        .floatProperty(CommonProperties.SPREAD, 6)
                        .intProperty(SpreadFireShape.PROJECTILES, 4)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 4),
                    new AutomaticTrigger(new SimpleAmmoSource(64),
                        new SpreadFireShape(SmallRocket::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 1.0F)),
				addWeapon("rocket_launcher",
					builder -> builder.projectileSpeed(2.0F).kickback(3).cooldown(60).mobUsable()
		                .floatProperty(CommonProperties.EXPLOSION_SIZE, 4.0F)
		                .intProperty(BigRocket.TRAVEL_TIME, 20)
		                .booleanProperty(CommonProperties.DAMAGE_TERRAIN, true)
	                    .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new AutomaticTrigger(new SimpleAmmoSource(1), new SingleShotFireShape(BigRocket::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 2.0F, 0.6F)),
				addWeapon("rocket_launcher_imp",
                    builder -> builder.projectileSpeed(2.0F).kickback(3).cooldown(60).mobUsable()
                        .floatProperty(CommonProperties.EXPLOSION_SIZE, 4.0F)
                        .intProperty(BigRocket.TRAVEL_TIME, 20)
                        .booleanProperty(CommonProperties.DAMAGE_TERRAIN, true)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new AutomaticTrigger(new SimpleAmmoSource(1), new SingleShotFireShape(BigRocket::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 2.0F, 0.6F)),
				addWeapon("arrow_mortar",
                    builder -> builder
                        .projectileSpeed(1.5F).kickback(3).cooldown(20).mobUsable()
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1)
                        .withSubProjectileProperties(subBuilder -> subBuilder
                            .minimumDamage(2).maximumDamage(10).projectileSpeed(1.0F)),
                    new SemiAutomaticTrigger(new SimpleAmmoSource(8), new SingleShotFireShape(SabotArrow::new)))
					.fireEffects(new PlaySound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F),
					    new SpawnParticle(EnumParticleTypes.SMOKE_LARGE, 0))
					.cooldownEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 2.0F)),
				addWeapon("dragon_mortar",
                    builder -> builder
                        .projectileSpeed(3.0F).cooldown(20).mobUsable()
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1)
                        .withSubProjectileProperties(subBuilder -> subBuilder
                            .minimumDamage(4).maximumDamage(6).projectileSpeed(1.0F)
                            .intProperty(CommonProperties.FIRE_DUR_ENTITY, 6)
                            .floatProperty(CommonProperties.EXPLOSION_SIZE, 1.0F)
                            .booleanProperty(CommonProperties.DAMAGE_TERRAIN, true)),
                    new SemiAutomaticTrigger(new SimpleAmmoSource(8), new SingleShotFireShape(SabotRocket::new)))
                    .fireEffects(new PlaySound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F),
                        new SpawnParticle(EnumParticleTypes.SMOKE_LARGE, 0))
                    .cooldownEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 2.0F)),
				addWeapon("potatosser",
					builder -> builder.minimumDamage(2).maximumDamage(5)
					    .projectileSpeed(1.5F).cooldown(15).mobUsable()
					    .booleanProperty(CommonProperties.SHOULD_DROP, true)
	                    .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
					new AutomaticTrigger(new SimpleAmmoSource(14), new SingleShotFireShape(PotatoShot::new)))
					.fireEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 0.7F, 0.4F))
					.cooldownEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.3F, 3.0F)),
				addWeapon("snow_cannon",
                    builder -> builder.minimumDamage(1).maximumDamage(2)
                        .projectileSpeed(1.5F).kickback(2).cooldown(15).mobUsable()
                        .floatProperty(CommonProperties.SPREAD, 10.0F)
                        .intProperty(CommonProperties.SLOWNESS_STRENGTH, 3)
                        .intProperty(CommonProperties.SLOWNESS_DUR, 40)
                        .intProperty(SpreadFireShape.PROJECTILES, 4)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 4),
                    new AutomaticTrigger(new SimpleAmmoSource(64), new SpreadFireShape(SnowShot::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F)),
				addWeapon("ender_rifle",
                    builder -> builder.minimumDamage(4).maximumDamage(16)
                        .projectileSpeed(3.0F).knockback(1).kickback(3).cooldown(25).mobUsable()
                        .floatProperty(EnderShot.BONUS_DAMAGE, 1.0F)
                        .floatProperty(CommonProperties.MAX_ZOOM, 30)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new SemiAutomaticTrigger(new SimpleAmmoSource(8), new SingleShotFireShape(EnderShot::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F),
                        new SpawnParticle(EnumParticleTypes.SMOKE_NORMAL, 0.0F))
                    .cooldownEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.7F, 0.2F),
                        new SpawnParticle(EnumParticleTypes.SMOKE_NORMAL, 0.0F)),
				addWeapon("fen_fire",
					builder -> builder.projectileSpeed(1.5F).cooldown(20)
		                .intProperty(CommonProperties.FIRE_DUR_ENTITY, 1)
		                .intProperty(CommonProperties.DESPAWN_TIME.getLeft(),
		                    "How long fen lights stay lit in ticks. Set to 0 for infinite time", 0)
	                    .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new SemiAutomaticTrigger(new SimpleAmmoSource(32), new SingleShotFireShape(FenGoop::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_ARROW_SHOOT, 0.7F, 0.3F))
                    .cooldownEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.8F, 2.0F)),
                addWeapon("flint_duster",
                    builder -> builder.damage(1)
                        .intProperty("maxRange", "The maximum range of this weapon in blocks", 7)
                        .floatProperty(BeamFireShape.MAX_RANGE, 8)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new AutomaticTrigger(new SimpleAmmoSource(256),
                        new BeamFireShape((stack, world, user, target, properties) ->
                        {
                            if (target.typeOfHit == RayTraceResult.Type.ENTITY)
                            {
                                target.entityHit.attackEntityFrom(DamageSource.GENERIC,
                                    properties.generateDamage(world.rand));
                            }
                            else if (target.typeOfHit == RayTraceResult.Type.BLOCK)
                            {
                                BlockPos pos = target.getBlockPos();
                                IBlockState toMine = world.getBlockState(pos);
                                if (toMine.getBlockHardness(world, pos) <= 2 ||
                                    toMine.getBlock().getHarvestLevel(toMine) <= 0)
                                {
                                    Helper.breakBlock(world, user, pos);
                                    for (int i = 0; i < 4; i++)
                                    {
                                        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                                            target.hitVec.x, target.hitVec.y, target.hitVec.z, 0, 0, 0);
                                    }
                                }
                            }
                        }, 0x000000)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_BAT_TAKEOFF, 0.5F, 0.6F),
                        new PlaySound(SoundType.GROUND.getBreakSound(), 1.0F, 1.0F)),
                addWeapon("sunray",
                    builder -> builder.damage(4).kickback(3)
                        .intProperty(CommonProperties.FIRE_DUR_ENTITY, 10)
                        .intProperty(BeamFireShape.PIERCING, 1)
                        .floatProperty(BeamFireShape.MAX_RANGE, 64)
                        .intProperty("minLight", "The minimum light level needed to recharge", 12)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 10),
                    new AutomaticTrigger(new SolarAmmoSource(128),
                        new BeamFireShape((stack, world, user, target, properties) ->
                        {
                            if (target.typeOfHit == RayTraceResult.Type.ENTITY)
                            {
                                target.entityHit.attackEntityFrom(DamageSource.ON_FIRE,
                                    properties.generateDamage(world.rand));
                                target.entityHit.hurtResistantTime = 0;
                                target.entityHit.setFire(properties.getInt(CommonProperties.FIRE_DUR_ENTITY));
                            }
                            else if (target.typeOfHit == RayTraceResult.Type.BLOCK)
                            {
                                BlockPos pos = target.getBlockPos();
                                IBlockState state = world.getBlockState(pos);
                                BlockPos posUp = pos.up();
                                IBlockState upState = world.getBlockState(posUp);
                                if (upState.getBlock().isAir(upState, world, posUp))
                                    world.setBlockState(posUp, Blocks.FIRE.getDefaultState());
                                else if (state.getBlock() == Blocks.SNOW)
                                {
                                    world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState()
                                        .withProperty(BlockSnow.LAYERS, 7));
                                }
                                else if (state.getBlock() == Blocks.SNOW_LAYER)
                                {
                                    int layers = state.getValue(BlockSnow.LAYERS);

                                    world.setBlockState(pos, layers > 1
                                        ? Blocks.SNOW_LAYER.getDefaultState()
                                            .withProperty(BlockSnow.LAYERS, layers - 1)
                                        : Blocks.AIR.getDefaultState());
                                }
                                else if (state.getBlock() == Blocks.ICE)
                                    world.setBlockState(pos, Blocks.WATER.getDefaultState());
                                else if (state.getMaterial() == Material.WATER)
                                    world.setBlockToAir(pos);
                            }
                        }, 0xFFFFFF)))
                    .fireEffects(new Knockback(), new PlaySound(SoundEvents.ENTITY_BLAZE_DEATH, 0.7F, 2.0F),
                        new PlaySound(SoundEvents.ENTITY_FIREWORK_BLAST, 2.0F, 0.1F),
                        new SpawnParticle(EnumParticleTypes.REDSTONE, 0.5F))
                    .cooldownEffects(new PlaySound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 0.5F),
                        new PlaySound(SoundEvents.ENTITY_CAT_HISS, 0.6F, 2.0F)),
				addWeapon("powder_knuckles",
                    builder -> builder
                        .floatProperty(CommonProperties.EXPLOSION_SIZE, 1.5F)
                        .booleanProperty(CommonProperties.DAMAGE_TERRAIN, true)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1)
                        .intProperty(CommonProperties.SELF_DAMAGE, 1),
                    new PunchTrigger(new SimpleAmmoSource(8), new HitscanFireShape((world, user, properties, x, y, z) ->
                    {
                        if (!world.isRemote)
                            world.createExplosion(user, x, y, z, properties.getFloat(CommonProperties.EXPLOSION_SIZE), properties.getBoolean(CommonProperties.DAMAGE_TERRAIN));
                        NetHelper.sendParticleMessageToAllPlayers(world, user, EnumParticleTypes.SMOKE_NORMAL, (byte) 0);
                    })))
				    .fireEffects((world, user, stack, properties) -> Helper.causeSelfDamage(user, ((Weapon) stack.getItem()).getProperties().getInt(CommonProperties.SELF_DAMAGE))),
				addWeapon("powder_knuckles_mod",
                    builder -> builder
                        .floatProperty(CommonProperties.EXPLOSION_SIZE, 1.5F)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new PunchTrigger(new SimpleAmmoSource(8), new HitscanFireShape((world, user, properties, x, y, z) ->
                    {
                        for (int deltaX = -1; deltaX <= 1; deltaX++)
                        {
                            for (int deltaY = -1; deltaY <= 1; deltaY++)
                            {
                                for (int deltaZ = -1; deltaZ <= 1; deltaZ++)
                                {
                                    BlockPos pos = new BlockPos(x + deltaX, y + deltaY, z + deltaZ);
                                    IBlockState toBreak = world.getBlockState(pos);
                                    float hardness = toBreak.getBlockHardness(world, pos);
                                    // hardness 100 means resistant to all but the strongest explosions
                                    if (hardness == -1 || hardness >= 100 || toBreak.getBlock().getHarvestLevel(toBreak) > 1)
                                        continue;
                                    if (user instanceof EntityPlayer && toBreak.getBlock()
                                        .canSilkHarvest(world, pos, toBreak, (EntityPlayer) user))
                                    {
                                        List<ItemStack> items = Lists.newArrayList(BlockAccessors.getSilkTouchDrop(toBreak));
                                        ForgeEventFactory.fireBlockHarvesting(items, world, pos, toBreak, 0, 1.0f, true, (EntityPlayer) user);
                                        world.destroyBlock(pos, false);
                                        for (ItemStack stack : items)
                                            Block.spawnAsEntity(world, pos, stack);
                                    }
                                    else
                                        Helper.breakBlock(world, user, pos);
                                }
                            }
                        }
                        if (!world.isRemote)
                        {
                            // Explosion ignores terrain, block destruction handled above
                            world.createExplosion(user, x, y, z, properties.getFloat(CommonProperties.EXPLOSION_SIZE), false);
                        }
                        NetHelper.sendParticleMessageToAllPlayers(world, user, EnumParticleTypes.SMOKE_NORMAL, (byte) 0);
                    }))),
				addWeapon("soul_cairn",
                    builder -> builder.projectileSpeed(3.0F).kickback(4).cooldown(20)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1)
                        .intProperty(CommonProperties.SELF_DAMAGE, 2),
                    new AutomaticTrigger(new SimpleAmmoSource(1), new SingleShotFireShape(SoulShot::new)))
                    .fireEffects(new PlaySound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F),
                        new PlaySound(SoundEvents.BLOCK_NOTE_BASS, 1.0F, 0.4F),
                        (world, user, stack, properties) -> Helper.causeSelfDamage(user, ((Weapon) stack.getItem()).getProperties().getInt(CommonProperties.SELF_DAMAGE))),
                addWeapon("aqua_accelerator",
                    builder -> builder.projectileSpeed(1.5F),
                    new AutomaticTrigger(new WaterAmmoSource(), new SingleShotFireShape(WaterShot::new)))
                    .fireEffects(new PlaySound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F)),
				addWeapon("silken_spinner",
                    builder -> builder.projectileSpeed(1.5F).cooldown(20)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new SemiAutomaticTrigger(new SimpleAmmoSource(8), new SingleShotFireShape(WebShot::new)))
                    .fireEffects(new PlaySound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F)),
                addWeapon("frost_lancer",
                    builder -> builder.minimumDamage(9).maximumDamage(18)
                        .projectileSpeed(3.5F).knockback(3).kickback(4).cooldown(40).mobUsable()
                        .intProperty(CommonProperties.SLOWNESS_STRENGTH, 3)
                        .intProperty(CommonProperties.SLOWNESS_DUR, 120)
                        .intProperty(CommonProperties.NAUSEA_DUR, 120)
                        .floatProperty(CommonProperties.MAX_ZOOM, 20)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new SemiAutomaticTrigger(new SimpleAmmoSource(4),
                        new SingleShotFireShape(ColdIron::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.8F, 1.5F),
                        new SpawnParticle(EnumParticleTypes.SMOKE_NORMAL, 0.0F))
                    .cooldownEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.7F, 0.2F),
                        new SpawnParticle(EnumParticleTypes.SMOKE_NORMAL, 0.0F)),
                addWeapon("ray_of_hope",
                    builder -> builder.floatProperty(BeamFireShape.MAX_RANGE, 64)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new AutomaticTrigger(new SimpleAmmoSource(320),
                        new BeamFireShape((stack, world, user, target, properties) ->
                        {
                            if (target.entityHit instanceof EntityLivingBase)
                            {
                                EntityLivingBase living = (EntityLivingBase) target.entityHit;
                                if (living.isEntityUndead())
                                    living.addPotionEffect(new PotionEffect(MobEffects.WITHER, 20, 3));
                                else
                                {
                                    if (living.getHealth() >= living.getMaxHealth() &&
                                        living.getAbsorptionAmount() < living.getMaxHealth())
                                    {
                                        living.setAbsorptionAmount(living.getAbsorptionAmount() + 1);
                                    }
                                    living.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 3));
                                }
                            }
                        }, 0xCD5CAB)))
                    .fireEffects(new PlaySound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.7F, 1.4F))
            );
			registry.register(addWeapon("ender_rail_accelerator",
                builder -> builder.minimumDamage(120).maximumDamage(150)
                    .projectileSpeed(5.0F).kickback(30)
                    .floatProperty(EnderAccelerator.SELF_EXPLOSION_SIZE,4.0F)
                    .floatProperty(CommonProperties.EXPLOSION_SIZE, 8.0F)
                    .booleanProperty(CommonProperties.DAMAGE_TERRAIN, true)
                    .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                new ERATrigger(new SimpleAmmoSource(1),
                    new SingleShotFireShape(EnderAccelerator::new)))
                .fireEffects(new Knockback(),
                    (world, user, stack, properties) ->
                    {
                        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("hasEmeraldMuzzle"))
                        {
                            Helper.causeSelfDamage(user, 15.0F);
                            Helper.playSoundAtEntityPos(user,
                                SoundEvents.ENTITY_GENERIC_EXPLODE, 2.0F, 0.1F);
                            NetHelper.sendParticleMessageToAllPlayers(world, user,
                                EnumParticleTypes.SMOKE_LARGE, (byte) 6);
                        }
                        else
                        {
                            Helper.causeSelfDamage(user, 20.0F);
                            if (!world.isRemote)
                            {
                                world.createExplosion(user, user.posX, user.posY, user.posZ,
                                    properties.getFloat(EnderAccelerator.SELF_EXPLOSION_SIZE),
                                    properties.getBoolean(CommonProperties.DAMAGE_TERRAIN));
                            }
                        }
                    },
                    new PlaySound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.8F, 1.5F),
                    new SpawnParticle(EnumParticleTypes.SMOKE_NORMAL, 0.0F))
                .cooldownEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.7F, 0.2F),
                    new SpawnParticle(EnumParticleTypes.SMOKE_NORMAL, 0.0F)));
			registerWeaponsWithAmmo(registry);
		}

		// Registers weapons that need their ammo item type as a ctor arg, and said ammo
		private static void registerWeaponsWithAmmo(IForgeRegistry<Item> registry)
		{
			// Sugar Engine and Sugar Magazine
			AmmoBase sugarMag = addAmmo("sugar_magazine", new AmmoMagazine(4, 4)
			    .fillSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 1.50F)
			    .setMaxDamage(200));
			registry.registerAll(sugarMag, addWeapon("sugar_engine",
                builder -> builder.minimumDamage(1).maximumDamage(3)
                    .projectileSpeed(2.0F).kickback(1).mobUsable()
                    .floatProperty(CommonProperties.SPREAD, 5.0F)
                    .intProperty(SpreadFireShape.PROJECTILES, 1)
                    .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                new SpoolingTrigger(new MagazineAmmoSource(sugarMag)
                        .unloadEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F)),
                    new SpreadFireShape(SugarRod::new)))
                .fireEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.2F),
                    new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 0.6F, 3.0F), new Knockback()));

			// Obsidian weapons and Obsidian Magazine
			AmmoBase obsidianMag = addAmmo("obsidian_magazine", new AmmoMagazine(1, 1)
                .fillSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.3F)
                .setMaxDamage(32));
			registry.registerAll(obsidianMag,
			    addWeapon("splinter_rifle",
                    builder -> builder.minimumDamage(7).maximumDamage(13)
                        .projectileSpeed(3.0F).knockback(2).kickback(4).cooldown(100).mobUsable()
                        .intProperty(CommonProperties.WITHER_STRENGTH, 3)
                        .intProperty(CommonProperties.WITHER_DUR, 61)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 2),
                    new SemiAutomaticTrigger(new MagazineAmmoSource(obsidianMag)
                            .unloadEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F)),
                        new SingleShotFireShape(OSRShot::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.5F, 1.5F),
                        new SpawnParticle(EnumParticleTypes.SMOKE_NORMAL, 0.0F))
                    .cooldownEffects(new SpawnParticle(EnumParticleTypes.SMOKE_NORMAL, 0.0F),
                        new PlaySound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.5F, 1.2F)),
			    addWeapon("splinter_pistol",
                    builder -> builder.minimumDamage(4).maximumDamage(8)
                        .projectileSpeed(1.7F).cooldown(15).mobUsable()
                        .intProperty(CommonProperties.WITHER_STRENGTH, 1)
                        .intProperty(CommonProperties.WITHER_DUR, 61)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new SemiAutomaticTrigger(new MagazineAmmoSource(obsidianMag)
                            .unloadEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F)),
                        new SingleShotFireShape(OSPShot::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.4F, 1.5F),
                        new SpawnParticle(EnumParticleTypes.SMOKE_NORMAL, 0.0F))
                    .cooldownEffects(new PlaySound(SoundEvents.BLOCK_PISTON_EXTEND, 0.3F, 0.4F)),
			    addWeapon("wither_rifle",
                    builder -> builder.minimumDamage(7).maximumDamage(13)
                    .projectileSpeed(3.0F).knockback(2).kickback(6).cooldown(60)
                        .intProperty(OWRShot.MIN_MAGIC_DAMAGE, 6)
                        .intProperty(OWRShot.MAX_MAGIC_DAMAGE, 14)
                        .intProperty(CommonProperties.WITHER_STRENGTH, 3)
                        .intProperty(CommonProperties.WITHER_DUR, 61)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 2),
                    new SemiAutomaticTrigger(new MagazineAmmoSource(obsidianMag)
                            .unloadEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F)),
                        new SingleShotFireShape(OWRShot::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.5F, 1.5F),
                        new SpawnParticle(EnumParticleTypes.SPELL_INSTANT, 0.0F))
                    .cooldownEffects(new SpawnParticle(EnumParticleTypes.SMOKE_NORMAL, 0.0F),
                        new PlaySound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.2F)));

			AmmoBase goldMagazine = addAmmo("gold_magazine", new AmmoMagazine(1, 8)
                .fillSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.3F)
                .setMaxDamage(72));
			registry.registerAll(goldMagazine,
                addWeapon("coin_tosser",
                    builder -> builder.minimumDamage(1).maximumDamage(3)
                        .projectileSpeed(2.5F).kickback(1) .cooldown(15).mobUsable()
                        .booleanProperty(CommonProperties.SHOULD_DROP, true)
                        .floatProperty(CommonProperties.SPREAD, 5)
                        .intProperty(SpreadFireShape.PROJECTILES, 9)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new AutomaticTrigger(new MagazineAmmoSource(goldMagazine)
                        .unloadEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F)),
                        new SpreadFireShape(CoinShot::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 3.0F)),
			    addWeapon("coin_tosser_mod",
                    builder -> builder.minimumDamage(1).maximumDamage(3)
                        .projectileSpeed(2.5F).kickback(1) .cooldown(15).mobUsable()
                        .booleanProperty(CommonProperties.SHOULD_DROP, true)
                        .floatProperty(CommonProperties.SPREAD, 2)
                        .intProperty(SpreadFireShape.PROJECTILES, 3)
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                    new AutomaticTrigger(new MagazineAmmoSource(goldMagazine)
                            .unloadEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F)),
                        new SpreadFireShape(CoinShot::new)))
                    .fireEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 3.0F)));

			// Hidden Ender Pistol and Ender Quartz Magazine
			AmmoBase enderQuartzMagazine = addAmmo("ender_quartz_magazine", new AmmoMagazine(1, 1)
                .fillSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.3F)
                .setMaxDamage(8));
			registry.registerAll(enderQuartzMagazine, addWeapon("hidden_ender_pistol",
                builder -> builder.minimumDamage(16).maximumDamage(24)
                    .projectileSpeed(5.0F).kickback(1).cooldown(20)
                    .intProperty(CommonProperties.DESPAWN_TIME, 40)
                    .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                new SemiAutomaticTrigger(new MagazineAmmoSource(enderQuartzMagazine)
                        .unloadEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.3F)),
                    new SingleShotFireShape(EnderAno::new)))
                .fireEffects(new PlaySound(SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, 1.4F, 0.5F),
                    new SpawnParticle(EnumParticleTypes.PORTAL, 0.0F))
                .cooldownEffects(new PlaySound(SoundEvents.BLOCK_GLASS_BREAK, 0.3F, 0.3F)));

			AmmoBase lapisMagazine = addAmmo("lapis_magazine", new LapisMagazine()
                .fillSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.2F)
                .setMaxDamage(150));
			registry.registerAll(lapisMagazine, addWeapon("lapis_coil",
                builder -> builder.minimumDamage(1).maximumDamage(3)
                    .projectileSpeed(2.5F).cooldown(4).mobUsable()
                    .intProperty(LapisShot.WEAKNESS_STRENGTH, 2)
                    .intProperty(LapisShot.WEAKNESS_DUR, 40)
                    .intProperty(CommonProperties.NAUSEA_DUR, 40)
                    .intProperty(LapisShot.HUNGER_STRENGTH, 2)
                    .intProperty(LapisShot.HUNGER_DUR, 40)
                    .intProperty(CommonProperties.DESPAWN_TIME, 100)
                    .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
                new AutomaticTrigger(new MagazineAmmoSource(lapisMagazine)
                        .unloadEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F)),
                    new SingleShotFireShape(LapisShot::new)))
                .fireEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.5F),
                    new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 3.0F)));

			AmmoBase largeNetherrackMagazine = addAmmo("large_netherrack_magazine", new AmmoMagazine(1, 8)
                .fillSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.3F)
                .setMaxDamage(200));
			registry.registerAll(largeNetherrackMagazine, addWeapon("nether_bellows",
    			builder -> builder.minimumDamage(1).maximumDamage(1)
    			    .projectileSpeed(0.75F).mobUsable()
    			    .intProperty(CommonProperties.FIRE_DUR_ENTITY, 3)
    			    .floatProperty(CommonProperties.SPREAD, 10)
                    .intProperty(SpreadFireShape.PROJECTILES, 5)
                    .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 5),
                new AutomaticTrigger(new MagazineAmmoSource(largeNetherrackMagazine)
                        .unloadEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F)),
                    new SpreadFireShape(NetherFire::new)))
                .fireEffects(new PlaySound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 0.3F)));

			// Thorn Spitter, Proximity Thorn Thrower and Thorn Magazine
			AmmoBase thornMagazine = addAmmo("thorn_magazine", new AmmoMagazine(1, 8)
                .fillSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 1.3F)
                .setMaxDamage(64));
			registry.registerAll(thornMagazine,
			        addWeapon("thorn_spitter",
    		            builder -> builder.minimumDamage(1).maximumDamage(2)
    		                .projectileSpeed(1.75F).cooldown(10).mobUsable()
                            .intProperty(BurstTrigger.BURST_SIZE, 4)
                            .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 1),
    	                new BurstTrigger(new MagazineAmmoSource(largeNetherrackMagazine)
                                .unloadEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 1.3F)),
    	                    new SingleShotFireShape(Thorn::new)))
    	                .fireEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 0.6F)),
					addWeapon("proximity_thorn_thrower",
    					builder -> builder.minimumDamage(1).maximumDamage(2)
    					    .projectileSpeed(2.0F).kickback(2).cooldown(20)
        	                .intProperty(CommonProperties.DESPAWN_TIME, 6000)
        	                .intProperty(ProxyThorn.PROX_CHECK_INTERVAL, 20)
        	                .intProperty(ProxyThorn.THORN_AMOUNT, 32)
        	                .floatProperty(ProxyThorn.TRIGGER_DISTANCE, 2.0F)
                            .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 8),
                        new AutomaticTrigger(new MagazineAmmoSource(thornMagazine)
                                .unloadEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.3F)),
                            new SingleShotFireShape(ProxyThorn::new)))
                        .fireEffects(new PlaySound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.7F, 1.5F)));

			// Redstone Sprayer and Large Redstone Magazine
			AmmoBase largeRedstoneMagazine = addAmmo("large_redstone_magazine", new AmmoMagazine(1, 8)
                .fillSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.3F)
                .setMaxDamage(200));
			registry.registerAll(largeRedstoneMagazine, addWeapon("redstone_sprayer",
                builder -> builder.projectileSpeed(0.5F).mobUsable()
                    .intProperty(CommonProperties.WITHER_STRENGTH, 2)
                    .intProperty(CommonProperties.WITHER_DUR, 20)
                    .intProperty(RedSpray.BLINDNESS_DUR, 20)
                    .floatProperty(CommonProperties.SPREAD, 5)
                    .intProperty(SpreadFireShape.PROJECTILES, 5)
                    .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 5),
                new AutomaticTrigger(new MagazineAmmoSource(largeRedstoneMagazine)
                        .unloadEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F)),
                    new SpreadFireShape(RedSpray::new)))
                .fireEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.6F, 0.9F)));

			AmmoBase seedJar = addAmmo("seed_jar", new AmmoMagazine(8, 8)
                .fillSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 0.7F)
                .setMaxDamage(512));
			registry.registerAll(seedJar, addWeapon("seed_sweeper",
                builder -> builder.minimumDamage(1).maximumDamage(1)
                    .cooldown(15).projectileSpeed(1.6F).mobUsable()
                    .floatProperty(CommonProperties.SPREAD, 13.0F)
                    .intProperty(SpreadFireShape.PROJECTILES, 8)
                    .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 8),
                new AutomaticTrigger(new MagazineAmmoSource(seedJar)
                        .unloadEffects(new PlaySound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.7F, 0.3F)),
                    new SpreadFireShape(Seed::new)))
                .fireEffects(new PlaySound(SoundEvents.ENTITY_ITEM_BREAK, 1.6F, 0.9F)));

			// Lightning Red and Redstone Magazine
			AmmoBase redstoneMagazine = addAmmo("redstone_magazine", new AmmoMagazine(1, 8)
                .fillSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.3F)
                .setMaxDamage(64));
			registry.registerAll(redstoneMagazine, addWeapon("lightning_red",
                    builder -> builder.minimumDamage(8).maximumDamage(16)
                        .projectileSpeed(5.0F).kickback(3).cooldown(40).mobUsable()
                        .intProperty(SimpleAmmoSource.AMMO_CONSUMPTION, 4)
                        .intProperty(BeamFireShape.PIERCING, 5)
                        .floatProperty(BeamFireShape.MAX_RANGE, 64)
                        .floatProperty("lightningChance", "Chance for hit entities "
                            + "to be struck by lightning. 1.0 == 100%.", 0.2F),
                    new AutomaticTrigger(new MagazineAmmoSource(redstoneMagazine),
                        new BeamFireShape((stack, world, user, target, properties) ->
                        {
                            if (target.typeOfHit == RayTraceResult.Type.ENTITY)
                            {
                                target.entityHit.attackEntityFrom(DamageSource.LIGHTNING_BOLT,
                                    properties.generateDamage(world.rand));
                                target.entityHit.hurtResistantTime = 0;
                                if (world.rand.nextFloat() <= properties.getFloat("lightningChance"))
                                {
                                    world.addWeatherEffect(new EntityLightningBolt(world,
                                        target.hitVec.x, target.hitVec.y, target.hitVec.z, false));
                                }
                            }
                            else if (target.typeOfHit == RayTraceResult.Type.BLOCK)
                            {
                                IBlockState toBreak = world.getBlockState(target.getBlockPos());
                                if (toBreak.getBlock().getHarvestLevel(toBreak) <= 1)
                                    Helper.breakBlock(world, user, target.getBlockPos());
                            }
                        }, 0xFF0000)))
                    .fireEffects(new Knockback(), new PlaySound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 1.0F, 0.5F),
                        new PlaySound(SoundEvents.ENTITY_FIREWORK_BLAST, 2.0F, 0.1F),
                        new SpawnParticle(EnumParticleTypes.REDSTONE, 0.5F))
                    .cooldownEffects(new PlaySound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 0.2F)));
		}

		private static Weapon addWeapon(String name, Consumer<WeaponProperties.Builder> propertiesBuilder, Trigger trigger)
		{
		    WeaponProperties.Builder builder = WeaponProperties.builder();
	        builder.booleanProperty(WeaponProperties.ENABLED, true);
	        propertiesBuilder.accept(builder);
		    Weapon weapon = new Weapon(name, builder, trigger);
            QuiverbowMain.weapons.add(weapon);
		    return weapon;
		}

		private static void registerAmmo(IForgeRegistry<Item> registry)
		{
			registry.registerAll(addAmmo("arrow_bundle", new AmmoBase()), addAmmo("cold_iron_clip", new AmmoBase()),
					addAmmo("rocket_bundle", new AmmoBase()), addAmmo("large_rocket", new AmmoBase()),
					addAmmo("box_of_flint_dust", new AmmoBase()));
		}

		private static AmmoBase addAmmo(String name, AmmoBase ammoBase)
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
		    for (Weapon weapon : ReloadSpecificationRegistry.INSTANCE.getRegisteredWeapons())
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
                createEntry("nether_fire", 80, 1, true, NetherFire.class, NetherFire::new),
                createEntry("red_spray", 80, 1, true, RedSpray.class, RedSpray::new),
                createEntry("soul", 80, 1, true, SoulShot.class, SoulShot::new),
                createEntry("water", 80, 1, true, WaterShot.class, WaterShot::new),
                createEntry("web", 80, 1, true, WebShot.class, WebShot::new),
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

			for (Item weapon : weapons)
			{
			    if (weapon == ItemRegistry.AUTOLOADER_CROSSBOW)
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
			setStandardModelLocation(ItemRegistry.AA_TARGET_ASSISTANT);
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
	           if (!AutoLoadingTrigger.isLoaded(stack)) return unchambered;
	           return chambered;
	       });
	    }
	}
}