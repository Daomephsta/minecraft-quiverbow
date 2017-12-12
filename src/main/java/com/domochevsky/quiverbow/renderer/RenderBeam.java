package com.domochevsky.quiverbow.renderer;

import java.util.Iterator;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.domochevsky.quiverbow.Main.Constants;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;
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
		private Vec3d beamStart;
		// The position where the beam ends
		private Vec3d beamEnd;

		public Beam(int beamColour, Vec3d beamStart, Vec3d beamEnd)
		{
			this.beamColour = beamColour;
			resetDespawnTimer();
			this.beamStart = beamStart;
			this.beamEnd = beamEnd;
		}

		public void resetDespawnTimer()
		{
			this.timeTillDespawn = 5;
		}
	}

	@SubscribeEvent
	public static void renderBeam(RenderWorldLastEvent event)
	{
		for(Iterator<Beam> iter = beams.values().iterator(); iter.hasNext();) 
		{		
			Beam beam = iter.next();
			if(beam.timeTillDespawn-- <= 0)
			{
				//iter.remove();
				//continue;
			}

			Tessellator tess = Tessellator.getInstance();
			VertexBuffer vtxBuf = tess.getBuffer();

			Minecraft mc = Minecraft.getMinecraft();
			double x = mc.player.posX * event.getPartialTicks() + mc.player.prevPosX * (1.0F - event.getPartialTicks());
			double y = mc.player.posY * event.getPartialTicks() + mc.player.prevPosY * (1.0F - event.getPartialTicks());
			double z = mc.player.posZ * event.getPartialTicks() + mc.player.prevPosZ * (1.0F - event.getPartialTicks());

			float colourR = ((beam.beamColour >> 16) & 255) / 255.0F;
			float colourB = ((beam.beamColour >> 8) & 255) / 255.0F;
			float colourG = (beam.beamColour & 255) / 255.0F;

			GlStateManager.pushMatrix();
			GlStateManager.translate(376.0F - x, 10.5F - y, -947.0F - z);
			GlStateManager.disableTexture2D();
			GlStateManager.disableCull();
			vtxBuf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			renderInnerBeam(vtxBuf, colourR, colourG, colourB);
			tess.draw();
			GlStateManager.enableCull();
			GlStateManager.enableTexture2D();
			GlStateManager.popMatrix();
		}
	}

	private static void renderInnerBeam(VertexBuffer vtxBuf, float colourR, float colourG, float colourB)
	{
		//Top
		float height = 2.0F;
		vtxBuf.pos(0.0F, height, 0.0F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.5F, height, 0.0F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.5F, height, 0.5F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.0F, height, 0.5F).color(colourR, colourG, colourB, height).endVertex();
		//North
		vtxBuf.pos(0.0F, height, 0.0F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.5F, height, 0.0F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.5F, 0.0F, 0.0F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.0F, 0.0F, 0.0F).color(colourR, colourG, colourB, height).endVertex();
		//South
		vtxBuf.pos(0.0F, height, 0.5F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.5F, height, 0.5F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.5F, 0.0F, 0.5F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.0F, 0.0F, 0.5F).color(colourR, colourG, colourB, height).endVertex();
		//East
		vtxBuf.pos(0.5F, height, 0.0F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.5F, height, 0.5F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.5F, 0.0F, 0.5F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.5F, 0.0F, 0.0F).color(colourR, colourG, colourB, height).endVertex();
		//West
		vtxBuf.pos(0.0F, height, 0.0F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.0F, height, 0.5F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.0F, 0.0F, 0.5F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.0F, 0.0F, 0.0F).color(colourR, colourG, colourB, height).endVertex();
		//Bottom
		vtxBuf.pos(0.0F, 0.0F, 0.0F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.5F, 0.0F, 0.0F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.5F, 0.0F, 0.5F).color(colourR, colourG, colourB, height).endVertex();
		vtxBuf.pos(0.0F, 0.0F, 0.5F).color(colourR, colourG, colourB, height).endVertex();
	}

	@SuppressWarnings("unchecked")
	public static void updateBeam(Vec3d start, Vec3d end)
	{
		Beam beam = beams.computeIfAbsent(Minecraft.getMinecraft().player, player -> 
		{
			_WeaponBase weapon = (_WeaponBase)player.getActiveItemStack().getItem();
			BeamFiringBehaviour<_WeaponBase> beamFiringBehaviour = (BeamFiringBehaviour<_WeaponBase>) weapon.getFiringBehaviour();

			return new Beam(beamFiringBehaviour.getBeamColour(), start, end);
		});
		beam.resetDespawnTimer();
		beam.beamStart = start;
		beam.beamEnd = end;
	}
}
