package com.domochevsky.quiverbow.client.render;

import org.lwjgl.opengl.GL11;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.items.ItemRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemBow;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = QuiverbowMain.MODID)
public class EnderBowPredictionRenderer
{
    private static float lastPitch = Float.NaN,
                         lastYaw = Float.NaN;
    private static double lastX = Double.NaN,
                          lastY = Double.NaN,
                          lastZ = Double.NaN;
    private static Vec3d target = Vec3d.ZERO;

    @SubscribeEvent
    public static void onRenderTick(RenderWorldLastEvent event)
    {
        Minecraft client = Minecraft.getMinecraft();
        World world = client.world;
        EntityPlayer player = client.player;
        if (world == null || player.getActiveItemStack().getItem() != ItemRegistry.ENDER_BOW)
            return;
        if (isTargetOutdated(player))
            updateTarget(world, player);

        double lerpX = lerp(player.lastTickPosX, player.posX, event.getPartialTicks());
        double lerpY = lerp(player.lastTickPosY, player.posY, event.getPartialTicks());
        double lerpZ = lerp(player.lastTickPosZ, player.posZ, event.getPartialTicks());
        GlStateManager.pushMatrix();
        GlStateManager.translate(-lerpX, -lerpY, -lerpZ);
        GlStateManager.disableTexture2D();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(target.x - 1, target.y + 1, target.z).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(target.x, target.y, target.z).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(target.x + 1, target.y + 1, target.z).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(target.x, target.y, target.z).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(target.x, target.y + 1, target.z - 1).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(target.x, target.y, target.z).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(target.x, target.y + 1, target.z + 1).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        tess.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private static boolean isTargetOutdated(EntityPlayer player)
    {
        if (player.rotationPitch != lastPitch || player.rotationYaw != lastYaw ||
            player.posX != lastX || player.posY != lastY || player.posZ != lastZ)
        {
            lastPitch = player.rotationPitch;
            lastYaw = player.rotationYaw;
            lastX = player.posX;
            lastY = player.posY;
            lastZ = player.posZ;
            return true;
        }
        else return false;
    }

    private static void updateTarget(World world, EntityPlayer player)
    {
        Tracer tracer = new Tracer(world, player);
        float velocity = ItemBow.getArrowVelocity(20);
        tracer.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity * 3.0F, 0.0F);
        world.spawnEntity(tracer);
    }

    private static double lerp(double previous, double current, float partialTicks)
    {
        return previous + (current - previous) * partialTicks;
    }

    public static class Tracer extends EntityThrowable
    {
        private static final float ARROW_GRAVITY = 0.05F;

        public Tracer(World worldIn, EntityLivingBase shooter)
        {
            super(worldIn, shooter);
        }

        @Override
        protected float getGravityVelocity()
        {
            return ARROW_GRAVITY;
        }

        @Override
        protected void onImpact(RayTraceResult impactInfo)
        {
            if (impactInfo.typeOfHit == RayTraceResult.Type.BLOCK)
                target = impactInfo.hitVec;
            else if (impactInfo.typeOfHit == RayTraceResult.Type.ENTITY)
                target = impactInfo.entityHit.getPositionVector();
            setDead();
        }
    }

    public static class RenderTracer extends Render<Tracer>
    {
        public RenderTracer(RenderManager renderManager)
        {
            super(renderManager);
        }

        @Override
        protected ResourceLocation getEntityTexture(Tracer entity)
        {
            return null;
        }
    }
}
