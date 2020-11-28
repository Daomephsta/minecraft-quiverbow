package com.domochevsky.quiverbow;

import com.domochevsky.quiverbow.armsassistant.EntityArmsAssistant;
import com.domochevsky.quiverbow.client.render.EnderBowPredictionRenderer;
import com.domochevsky.quiverbow.models.AATransformsMetadataSerialiser;
import com.domochevsky.quiverbow.models.WeaponModel;
import com.domochevsky.quiverbow.projectiles.*;
import com.domochevsky.quiverbow.renderer.RenderAA;
import com.domochevsky.quiverbow.renderer.RenderCross;

import daomephsta.umbra.reflection.SRGReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
	    MinecraftForge.EVENT_BUS.register(new ListenerClient());
		registerRenderers();
		ModelLoaderRegistry.registerLoader(WeaponModel.Loader.INSTANCE);
		((MetadataSerializer) SRGReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "field_110452_an"))
		.registerMetadataSectionType(AATransformsMetadataSerialiser.INSTANCE, AATransformsMetadataSerialiser.AATransforms.class);
	}

	public void registerRenderers()
	{
		registerCrossStyleRender(BlazeShot.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/rod.png"), 2, 6);
		registerCrossStyleRender(SmallRocket.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/rocket.png"), 2, 8);
		registerCrossStyleRender(SabotRocket.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/rocketsabot.png"), 3, 10);
		registerCrossStyleRender(BigRocket.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/rocket.png"), 3, 10);
		registerCrossStyleRender(LapisShot.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/lapis.png"), 2, 8);
		registerCrossStyleRender(Thorn.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/thorn.png"), 2, 2);
		registerCrossStyleRender(ProxyThorn.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/thorn.png"), 4, 8);
		registerCrossStyleRender(ColdIron.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/coldiron.png"), 2, 8);
		registerCrossStyleRender(SugarRod.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/sugar.png"), 2, 5);
		registerCrossStyleRender(SabotArrow.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/arrowsabot.png"), 3, 10);
		registerCrossStyleRender(EnderShot.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/ender.png"), 2, 4);
		registerCrossStyleRender(OSPShot.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/obsidian.png"), 2, 4);
		registerCrossStyleRender(OSRShot.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/obsidian.png"), 2, 16);
		registerCrossStyleRender(OWRShot.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/obsidian.png"), 2, 16);
		registerCrossStyleRender(SoulShot.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/soulshot.png"), 2, 10);
		registerCrossStyleRender(RedSpray.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/redspray.png"), 2, 2);
		registerCrossStyleRender(NetherFire.class, new ResourceLocation(QuiverbowMain.MODID, "textures/entity/netherspray.png"), 2, 2);
		registerSnowballStyleRender(CoinShot.class, Items.GOLD_NUGGET);
		registerSnowballStyleRender(Seed.class, Items.MELON_SEEDS);
		registerSnowballStyleRender(PotatoShot.class, Items.BAKED_POTATO);
		registerSnowballStyleRender(SnowShot.class, Items.SNOWBALL);
		registerSnowballStyleRender(WaterShot.class, Items.WATER_BUCKET);
		registerSnowballStyleRender(FenGoop.class, Items.GLOWSTONE_DUST);
		registerSnowballStyleRender(WebShot.class, Items.SNOWBALL);
		registerInvisibleRender(EnderAno.class);
		registerInvisibleRender(EnderAccelerator.class);
		RenderingRegistry.registerEntityRenderingHandler(EntityArmsAssistant.class, RenderAA::new);
		RenderingRegistry.registerEntityRenderingHandler(EnderBowPredictionRenderer.Tracer.class, EnderBowPredictionRenderer.RenderTracer::new);
	}

	private <T extends ProjectileBase> void registerCrossStyleRender(Class<T> entityClass, ResourceLocation texture, int width, int length)
	{
		RenderingRegistry.<T>registerEntityRenderingHandler(entityClass, manager -> new RenderCross(manager, texture, width, length));
	}

	private void registerSnowballStyleRender(Class<? extends Entity> entityClass, Item toRender)
	{
		RenderingRegistry.registerEntityRenderingHandler(entityClass, manager -> new RenderSnowball<>(manager, toRender, Minecraft.getMinecraft().getRenderItem()));
	}

	private void registerInvisibleRender(Class<? extends Entity> entityClass)
	{
		RenderingRegistry.registerEntityRenderingHandler(entityClass, manager -> new Render<Entity>(manager)
		{
			@Override
			protected ResourceLocation getEntityTexture(Entity entity)
			{
				return null;
			}
		});
	}
}
