package com.domochevsky.quiverbow.renderer;

import java.util.Iterator;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.domochevsky.quiverbow.Main.Constants;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.BeamFiringBehaviour;
import com.google.common.collect.MapMaker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class RenderBeam
{
	private static final Map<EntityPlayer, Beam> beams = new MapMaker().weakKeys().makeMap();

	public static class Beam
	{
		// RGB color encoded as an integer
		private final int beamColour;
		// The remaining time till the beam despawns
		private int timeTillDespawn;
		// The position where the beam starts
		private double startPosX, startPosY, startPosZ;
		// The position where the beam started last tick
		private double prevStartPosX, prevStartPosY, prevStartPosZ;
		// The position where the beam ends
		private double endPosX, endPosY, endPosZ;
		// The position where the beam ended last tick
		private double prevEndPosX, prevEndPosY, prevEndPosZ;
		// The length of the beam
		private double length;

		public Beam(int beamColour, Vec3d beamStart, Vec3d beamEnd)
		{
			this.beamColour = beamColour;
			resetDespawnTimer();
			this.startPosX = this.prevStartPosX = beamStart.x;
			this.startPosY = this.prevStartPosY = beamStart.y;
			this.startPosZ = this.prevStartPosZ = beamStart.z;
			this.endPosX = this.prevEndPosX = beamEnd.x;
			this.endPosY = this.prevEndPosY = beamEnd.y;
			this.endPosZ = this.prevEndPosZ = beamEnd.z;
			this.length = beamStart.distanceTo(beamEnd);
		}

		public void updateEnds(Vec3d beamStart, Vec3d beamEnd)
		{
			this.prevStartPosX = this.startPosX;
			this.prevStartPosY = this.startPosY;
			this.prevStartPosZ = this.startPosZ;
			this.startPosX = beamStart.x;
			this.startPosY = beamStart.y;
			this.startPosZ = beamStart.z;

			this.prevEndPosX = this.endPosX;
			this.prevEndPosY = this.endPosY;
			this.prevEndPosZ = this.endPosZ;
			this.endPosX = beamEnd.x;
			this.endPosY = beamEnd.y;
			this.endPosZ = beamEnd.z;
			this.length = beamStart.distanceTo(beamEnd);
		}

		public void resetDespawnTimer()
		{
			this.timeTillDespawn = 5;
		}
	}

	@SubscribeEvent
	public static void renderBeam(RenderWorldLastEvent event)
	{
		for (Iterator<Beam> iter = beams.values().iterator(); iter.hasNext();)
		{
			Beam beam = iter.next();
			if (beam.timeTillDespawn-- <= 0)
			{
				iter.remove();
				continue;
			}

			Tessellator tess = Tessellator.getInstance();
			BufferBuilder bufBuilder = tess.getBuffer();

			Minecraft mc = Minecraft.getMinecraft();
			double interpPlayerX = mc.player.posX * event.getPartialTicks()
					+ mc.player.prevPosX * (1.0F - event.getPartialTicks());
			double interpPlayerY = mc.player.posY * event.getPartialTicks()
					+ mc.player.prevPosY * (1.0F - event.getPartialTicks());
			double interpPlayerZ = mc.player.posZ * event.getPartialTicks()
					+ mc.player.prevPosZ * (1.0F - event.getPartialTicks());

			double interpBeamStartX = beam.startPosX * event.getPartialTicks()
					+ beam.prevStartPosX * (1.0F - event.getPartialTicks());
			double interpBeamStartY = beam.startPosY * event.getPartialTicks()
					+ beam.prevStartPosY * (1.0F - event.getPartialTicks());
			double interpBeamStartZ = beam.startPosZ * event.getPartialTicks()
					+ beam.prevStartPosZ * (1.0F - event.getPartialTicks());

			double interpBeamEndX = beam.endPosX * event.getPartialTicks()
					+ beam.prevEndPosX * (1.0F - event.getPartialTicks());
			double interpBeamEndY = beam.endPosY * event.getPartialTicks()
					+ beam.prevEndPosY * (1.0F - event.getPartialTicks());
			double interpBeamEndZ = beam.endPosZ * event.getPartialTicks()
					+ beam.prevEndPosZ * (1.0F - event.getPartialTicks());

			float colourR = ((beam.beamColour >> 16) & 255) / 255.0F;
			float colourB = ((beam.beamColour >> 8) & 255) / 255.0F;
			float colourG = (beam.beamColour & 255) / 255.0F;

			GlStateManager.pushMatrix();
			GlStateManager.translate(-interpPlayerX, -interpPlayerY, -interpPlayerZ);
			GlStateManager.disableTexture2D();
			GlStateManager.disableCull();
			bufBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			bufBuilder.pos(interpBeamStartX, interpBeamStartY, interpBeamStartZ).color(colourR, colourG, colourB, 1.0F)
					.endVertex();
			bufBuilder.pos(interpBeamEndX, interpBeamEndY, interpBeamEndZ).color(colourR, colourG, colourB, 1.0F)
					.endVertex();
			// renderInnerBeam(vtxBuf, beam, colourR, colourG, colourB);
			tess.draw();
			GlStateManager.enableCull();
			GlStateManager.enableTexture2D();
			GlStateManager.popMatrix();
		}
	}

	private static void renderInnerBeam(BufferBuilder bufBuilder, Beam beam, float colourR, float colourG, float colourB)
	{
		// Top
		bufBuilder.pos(0.0F, beam.length, 0.0F).color(colourR / 2, colourG, colourB, 1.0F).endVertex();
		bufBuilder.pos(0.5F, beam.length, 0.0F).color(colourR / 2, colourG, colourB, 1.0F).endVertex();
		bufBuilder.pos(0.5F, beam.length, 0.5F).color(colourR / 2, colourG, colourB, 1.0F).endVertex();
		bufBuilder.pos(0.0F, beam.length, 0.5F).color(colourR / 2, colourG, colourB, 1.0F).endVertex();
		// North
		bufBuilder.pos(0.0F, beam.length, 0.0F).color(colourR / 2, colourG / 2, colourB, 1.0F).endVertex();
		bufBuilder.pos(0.5F, beam.length, 0.0F).color(colourR / 2, colourG / 2, colourB, 1.0F).endVertex();
		bufBuilder.pos(0.5F, 0.0F, 0.0F).color(colourR / 2, colourG / 2, colourB, 1.0F).endVertex();
		bufBuilder.pos(0.0F, 0.0F, 0.0F).color(colourR / 2, colourG / 2, colourB, 1.0F).endVertex();
		// South
		bufBuilder.pos(0.0F, beam.length, 0.5F).color(colourR, colourG / 2, colourB, 1.0F).endVertex();
		bufBuilder.pos(0.5F, beam.length, 0.5F).color(colourR, colourG / 2, colourB, 1.0F).endVertex();
		bufBuilder.pos(0.5F, 0.0F, 0.5F).color(colourR, colourG / 2, colourB, 1.0F).endVertex();
		bufBuilder.pos(0.0F, 0.0F, 0.5F).color(colourR, colourG / 2, colourB, 1.0F).endVertex();
		// East
		bufBuilder.pos(0.5F, beam.length, 0.0F).color(colourR, colourG / 2, colourB / 2, 1.0F).endVertex();
		bufBuilder.pos(0.5F, beam.length, 0.5F).color(colourR, colourG / 2, colourB / 2, 1.0F).endVertex();
		bufBuilder.pos(0.5F, 0.0F, 0.5F).color(colourR, colourG / 2, colourB / 2, 1.0F).endVertex();
		bufBuilder.pos(0.5F, 0.0F, 0.0F).color(colourR, colourG / 2, colourB / 2, 1.0F).endVertex();
		// West
		bufBuilder.pos(0.0F, beam.length, 0.0F).color(colourR, colourG, colourB / 2, 1.0F).endVertex();
		bufBuilder.pos(0.0F, beam.length, 0.5F).color(colourR, colourG, colourB / 2, 1.0F).endVertex();
		bufBuilder.pos(0.0F, 0.0F, 0.5F).color(colourR, colourG, colourB / 2, 1.0F).endVertex();
		bufBuilder.pos(0.0F, 0.0F, 0.0F).color(colourR, colourG, colourB / 2, 1.0F).endVertex();
		// Bottom
		bufBuilder.pos(0.0F, 0.0F, 0.0F).color(colourR / 2, colourG, colourB / 2, 1.0F).endVertex();
		bufBuilder.pos(0.5F, 0.0F, 0.0F).color(colourR / 2, colourG, colourB / 2, 1.0F).endVertex();
		bufBuilder.pos(0.5F, 0.0F, 0.5F).color(colourR / 2, colourG, colourB / 2, 1.0F).endVertex();
		bufBuilder.pos(0.0F, 0.0F, 0.5F).color(colourR / 2, colourG, colourB / 2, 1.0F).endVertex();
	}

	@SuppressWarnings("unchecked")
	public static void updateBeam(Vec3d start, Vec3d end)
	{
		Beam beam = beams.computeIfAbsent(Minecraft.getMinecraft().player, player ->
		{
			WeaponBase weapon = (WeaponBase) player.getActiveItemStack().getItem();
			BeamFiringBehaviour<WeaponBase> beamFiringBehaviour = (BeamFiringBehaviour<WeaponBase>) weapon
					.getFiringBehaviour();

			return new Beam(beamFiringBehaviour.getBeamColour(), start, end);
		});
		beam.resetDespawnTimer();
		beam.updateEnds(start, end);
	}
}
