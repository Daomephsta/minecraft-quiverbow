package com.domochevsky.quiverbow;

import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.projectiles._ProjectileBase;
import com.domochevsky.quiverbow.renderer.Render_AA;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerItemProjectileRenderer(Class<? extends _ProjectileBase> entityClass, final Item item)
    {
	RenderingRegistry.registerEntityRenderingHandler(entityClass, new IRenderFactory<_ProjectileBase>()
	{
	    @Override
	    public Render<? super _ProjectileBase> createRenderFor(RenderManager manager)
	    {
		return new RenderSnowball<>(manager, item, Minecraft.getMinecraft().getRenderItem());
	    }
	});
    }

    @Override
    public void registerTurretRenderer()
    {
	RenderingRegistry.registerEntityRenderingHandler(Entity_AA.class, new IRenderFactory<Entity_AA>()
	{
	    @Override
	    public Render<Entity_AA> createRenderFor(RenderManager manager)
	    {
		return new Render_AA(manager);
	    }
	});
    }
}
