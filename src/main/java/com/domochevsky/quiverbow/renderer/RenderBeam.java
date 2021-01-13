package com.domochevsky.quiverbow.renderer;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.google.common.collect.MapMaker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@Mod.EventBusSubscriber(modid = QuiverbowMain.MODID)
public class RenderBeam
{
    private static final Map<EntityLivingBase, Beam> beams = new MapMaker().weakKeys().makeMap();

    public static class Beam
    {
        /**RGB color encoded as an integer*/
        private int colour;
        /**The remaining time till the beam despawns*/
        private int timeTillDespawn;
        /**The length of the beam*/
        private double length;

        public Beam(int beamColour, double length)
        {
            this.colour = beamColour;
            this.length = length;
            resetDespawnTimer();
        }

        public void resetDespawnTimer()
        {
            this.timeTillDespawn = 10;
        }
    }

    // Cleanup unused beams on client tick so beam despawn rate is not dependent on FPS
    @SubscribeEvent
    public static void cleanupBeams(ClientTickEvent event)
    {
        for (Iterator<Entry<EntityLivingBase, Beam>> iter = beams.entrySet().iterator(); iter.hasNext();)
        {
            Entry<EntityLivingBase, Beam> next = iter.next();
            EntityLivingBase owner = next.getKey();
            Beam beam = next.getValue();
            // Null owner means owner has been garbage collected
            beam.timeTillDespawn -= 1;
            if (owner == null || beam.timeTillDespawn <= 0)
            {
                iter.remove();
                continue;
            }
        }
    }

    @SubscribeEvent
    public static void renderBeam(RenderWorldLastEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        float partialTicks = event.getPartialTicks();
        EntityLivingBase client = mc.player;
        for (Entry<EntityLivingBase, Beam> beamEntry : beams.entrySet())
        {
            EntityLivingBase owner = beamEntry.getKey();
            Beam beam = beamEntry.getValue();
            // Null owner means owner has been garbage collected
            if (owner == null)
                continue;

            GlStateManager.pushMatrix();
            mc.getTextureManager().bindTexture(TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM);
            double x = lerp(owner.prevPosX, owner.posX, partialTicks)
                - lerp(client.prevPosX, client.posX, partialTicks) - 0.5;
            double y = lerp(owner.prevPosY, owner.posY, partialTicks)
                - lerp(client.prevPosY, client.posY, partialTicks);
            double z = lerp(owner.prevPosZ, owner.posZ, partialTicks)
                - lerp(client.prevPosZ, client.posZ, partialTicks) - 0.5;
            float yaw = (float) -lerp(owner.prevRotationYawHead, owner.rotationYawHead, partialTicks);
            float pitch = 90 + (float) lerp(owner.prevRotationPitch, owner.rotationPitch, partialTicks);
            GlStateManager.translate(0, 1.2, 0);
            GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
            float colourR = ((beam.colour >> 16) & 255) / 255.0F;
            float colourB = ((beam.colour >> 8) & 255) / 255.0F;
            float colourG = (beam.colour & 255) / 255.0F;
            float[] colours = new float[] {colourR, colourB, colourG};
            TileEntityBeaconRenderer.renderBeamSegment(x, y, z, mc.getRenderPartialTicks(),
                1.0F, mc.world.getTotalWorldTime(), 0, (int) Math.ceil(beam.length), colours);
            GlStateManager.popMatrix();
        }
    }

    private static double lerp(double previous, double current, float partialTicks)
    {
        return current * partialTicks + previous * (1.0F - partialTicks);
    }

    public static boolean updateOrCreateBeam(EntityLivingBase owner, double length, int colour)
    {
        Beam beam = beams.get(owner);
        boolean createBeam = beam == null;
        if (beam == null)
        {
            beam = new Beam(colour, length);
            beams.put(owner, beam);
        }
        beam.length = length;
        beam.colour = colour;
        beam.resetDespawnTimer();
        return createBeam;
    }
}
