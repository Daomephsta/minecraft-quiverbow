package com.domochevsky.quiverbow;

import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.Renderer.Render_AA;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerProjectileRenderer(Class<? extends Entity> entityClass)
    {

    }

    @Override
    public void registerWeaponRenderer(Item item, byte number)
    {
	//
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
