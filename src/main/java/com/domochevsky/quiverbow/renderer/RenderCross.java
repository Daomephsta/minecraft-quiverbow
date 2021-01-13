package com.domochevsky.quiverbow.renderer;

import org.lwjgl.opengl.GL11;

import com.domochevsky.quiverbow.projectiles.ProjectileBase;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderCross extends Render<ProjectileBase>
{
    private final ResourceLocation textureLocation;
    private final int widthPx, lengthPx;
    
    public RenderCross(RenderManager renderManager, ResourceLocation textureLocation, int widthPx, int lengthPx)
    {
        super(renderManager);
        this.textureLocation = textureLocation;
        this.widthPx = widthPx;
        this.lengthPx = lengthPx;
    }

    @Override
    public void doRender(ProjectileBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.bindEntityTexture(entity);
        GlStateManager.pushMatrix();
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); //Reset the color
            GlStateManager.disableLighting(); //Disable lighting, projectiles should be fullbright
            GlStateManager.disableCull(); //Disable backface culling so quads render from both sides
            GlStateManager.translate(x, y, z); // Translate to render position
            //Rotate to match entity
            GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(90.0F + entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder bufBuilder = tess.getBuffer();
            float halfWidth = 0.5F * this.widthPx / 16;
            float halfLength = 0.5F * this.lengthPx / 16;
            bufBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            //Plane A
            bufBuilder.pos(-halfWidth, -halfLength, 0.0F).tex(1.0D, 1.0D).endVertex();
            bufBuilder.pos(halfWidth, -halfLength, 0.0F).tex(0.0D, 1.0D).endVertex();
            bufBuilder.pos(halfWidth, halfLength, 0.0F).tex(0.0D, 0.0D).endVertex();
            bufBuilder.pos(-halfWidth, halfLength, 0.0F).tex(1.0D, 0.0D).endVertex();
            //Plane B
            bufBuilder.pos(0.0F, -halfLength, -halfWidth).tex(1.0D, 1.0D).endVertex();
            bufBuilder.pos(0.0F, halfLength, -halfWidth).tex(1.0D, 0.0D).endVertex();
            bufBuilder.pos(0.0F, halfLength, halfWidth).tex(0.0D, 0.0D).endVertex();
            bufBuilder.pos(0.0F, -halfLength, halfWidth).tex(0.0D, 1.0D).endVertex();
            tess.draw();
             //Reset the GL state as best we can 
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
        }
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
    
    
    @Override
    protected ResourceLocation getEntityTexture(ProjectileBase entity)
    {
        return textureLocation;
    }
}
