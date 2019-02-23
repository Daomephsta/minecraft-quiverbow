package com.domochevsky.quiverbow.client.render;

import com.domochevsky.quiverbow.armsassistant.EntityArmsAssistant;
import com.domochevsky.quiverbow.armsassistant.UpgradeRegistry;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Arms Assistant - Daomephsta
 * Created using Tabula 7.0.0
 */
public class ModelArmsAssistant extends ModelBase 
{
	public ModelRenderer body,
						 sprocketsL,
						 sprocketsR,
						 lowerTurntable,
						 linkFL,
						 linkBL,
						 linkFR,
						 linkBR,
						 upperTurntable,
						 turret,
						 chest,
						 ammoHopper1,
						 ammoHopper2,
						 rightRail,
						 leftRail,
						 ammoFeed1R,
						 eye,
						 seat,
						 bookSpine,
						 ammoFeed1L,
						 aerial,
						 ammoFeed2R,
						 bookPages,
						 bookCoverF,
						 bookCoverB,
						 ammoFeed2L;

    public ModelArmsAssistant() 
    {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.linkBR = new ModelRenderer(this, 0, 31);
        this.linkBR.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.linkBR.addBox(-2.0F, -1.5F, 8.0F, 2, 3, 1, 0.0F);
        this.ammoFeed2R = new ModelRenderer(this, 40, 2);
        this.ammoFeed2R.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ammoFeed2R.addBox(-6.0F, -2.5F, 6.0F, 1, 1, 3, 0.0F);
        this.sprocketsR = new ModelRenderer(this, 0, 19);
        this.sprocketsR.setRotationPoint(-6.0F, 1.5F, 0.0F);
        this.sprocketsR.addBox(-2.0F, -2.5F, -8.0F, 2, 5, 16, 0.0F);
        this.linkFR = new ModelRenderer(this, 30, 31);
        this.linkFR.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.linkFR.addBox(-2.0F, -1.5F, -9.0F, 2, 3, 1, 0.0F);
        this.ammoFeed1L = new ModelRenderer(this, 40, 0);
        this.ammoFeed1L.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ammoFeed1L.addBox(0.5F, -2.5F, 8.0F, 5, 1, 1, 0.0F);
        this.sprocketsL = new ModelRenderer(this, 0, 19);
        this.sprocketsL.setRotationPoint(6.0F, 1.5F, 0.0F);
        this.sprocketsL.addBox(0.0F, -2.5F, -8.0F, 2, 5, 16, 0.0F);
        this.ammoFeed2L = new ModelRenderer(this, 40, 2);
        this.ammoFeed2L.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ammoFeed2L.addBox(5.0F, -2.5F, 6.0F, 1, 1, 3, 0.0F);
        this.bookCoverF = new ModelRenderer(this, 51, 44);
        this.bookCoverF.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bookCoverF.addBox(-2.0F, -7.5F, 3.0F, 3, 3, 1, 0.0F);
        this.bookCoverB = new ModelRenderer(this, 51, 44);
        this.bookCoverB.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bookCoverB.addBox(-2.0F, -7.5F, 1.0F, 3, 3, 1, 0.0F);
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, 20.0F, 0.0F);
        this.body.addBox(-6.0F, 0.0F, -8.0F, 12, 3, 16, 0.0F);
        this.lowerTurntable = new ModelRenderer(this, 0, 0);
        this.lowerTurntable.setRotationPoint(0.0F, -0.5F, 0.0F);
        this.lowerTurntable.addBox(-2.0F, 0.0F, -2.0F, 4, 1, 4, 0.0F);
        this.chest = new ModelRenderer(this, 20, 19);
        this.chest.setRotationPoint(0.0F, 0.0F, 13.0F);
        this.chest.addBox(-3.5F, -7.5F, -9.0F, 7, 3, 7, 0.0F);
        this.ammoHopper1 = new ModelRenderer(this, 0, 19);
        this.ammoHopper1.setRotationPoint(0.0F, 0.0F, 16.0F);
        this.ammoHopper1.addBox(-2.0F, -4.5F, -9.0F, 4, 2, 4, 0.0F);
        this.turret = new ModelRenderer(this, 0, 40);
        this.turret.setRotationPoint(0.0F, -8.0F, 0.0F);
        this.turret.addBox(-4.5F, -4.5F, -5.0F, 9, 4, 12, 0.0F);
        this.ammoFeed1R = new ModelRenderer(this, 40, 0);
        this.ammoFeed1R.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ammoFeed1R.addBox(-5.5F, -2.5F, 8.0F, 5, 1, 1, 0.0F);
        this.setRotateAngle(ammoFeed1R, 0.0F, -0.012740903539558604F, 0.0F);
        this.ammoHopper2 = new ModelRenderer(this, 0, 25);
        this.ammoHopper2.setRotationPoint(0.0F, 0.0F, 16.0F);
        this.ammoHopper2.addBox(-1.0F, -2.5F, -8.0F, 2, 1, 2, 0.0F);
        this.linkBL = new ModelRenderer(this, 0, 31);
        this.linkBL.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.linkBL.addBox(0.0F, -1.5F, 8.0F, 2, 3, 1, 0.0F);
        this.aerial = new ModelRenderer(this, 0, 47);
        this.aerial.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.aerial.addBox(3.5F, -8.5F, -5.0F, 1, 4, 1, 0.0F);
        this.upperTurntable = new ModelRenderer(this, 48, 3);
        this.upperTurntable.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.upperTurntable.addBox(-2.0F, -9.0F, -2.0F, 4, 9, 4, 0.0F);
        this.seat = new ModelRenderer(this, 30, 46);
        this.seat.setRotationPoint(0.0F, 0.0F, -5.0F);
        this.seat.addBox(-2.0F, -5.5F, 1.0F, 4, 1, 5, 0.0F);
        this.eye = new ModelRenderer(this, 0, 40);
        this.eye.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.eye.addBox(-1.5F, -4.0F, -6.0F, 3, 3, 1, 0.0F);
        this.setRotateAngle(eye, 0.0017453292519943296F, 0.0F, 0.0F);
        this.leftRail = new ModelRenderer(this, 30, 30);
        this.leftRail.setRotationPoint(4.5F, -2.5F, 5.0F);
        this.leftRail.addBox(0.0F, -1.0F, -9.0F, 2, 2, 10, 0.0F);
        this.linkFL = new ModelRenderer(this, 30, 31);
        this.linkFL.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.linkFL.addBox(0.0F, -1.5F, -9.0F, 2, 3, 1, 0.0F);
        this.rightRail = new ModelRenderer(this, 30, 30);
        this.rightRail.setRotationPoint(-3.5F, -2.5F, 5.0F);
        this.rightRail.addBox(-3.0F, -1.0F, -9.0F, 2, 2, 10, 0.0F);
        this.bookPages = new ModelRenderer(this, 51, 51);
        this.bookPages.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bookPages.addBox(-2.0F, -7.0F, 2.0F, 3, 4, 1, 0.0F);
        this.bookSpine = new ModelRenderer(this, 43, 42);
        this.bookSpine.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bookSpine.addBox(1.0F, -7.5F, 1.0F, 1, 3, 3, 0.0F);
        this.sprocketsR.addChild(this.linkBR);
        this.ammoFeed1R.addChild(this.ammoFeed2R);
        this.body.addChild(this.sprocketsR);
        this.sprocketsR.addChild(this.linkFR);
        this.turret.addChild(this.ammoFeed1L);
        this.body.addChild(this.sprocketsL);
        this.ammoFeed1L.addChild(this.ammoFeed2L);
        this.bookSpine.addChild(this.bookCoverF);
        this.bookSpine.addChild(this.bookCoverB);
        this.body.addChild(this.lowerTurntable);
        this.turret.addChild(this.chest);
        this.turret.addChild(this.ammoHopper1);
        this.upperTurntable.addChild(this.turret);
        this.turret.addChild(this.ammoFeed1R);
        this.turret.addChild(this.ammoHopper2);
        this.sprocketsL.addChild(this.linkBL);
        this.turret.addChild(this.aerial);
        this.lowerTurntable.addChild(this.upperTurntable);
        this.turret.addChild(this.seat);
        this.turret.addChild(this.eye);
        this.turret.addChild(this.leftRail);
        this.sprocketsL.addChild(this.linkFL);
        this.turret.addChild(this.rightRail);
        this.bookSpine.addChild(this.bookPages);
        this.turret.addChild(this.bookSpine);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) 
    {
    	EntityArmsAssistant armsAssistant = (EntityArmsAssistant) entity;
    	setVisibility(false, bookSpine);
    	setVisibility(armsAssistant.hasUpgrade(UpgradeRegistry.EXTRA_WEAPON), rightRail, ammoFeed1R);
    	setVisibility(armsAssistant.hasUpgrade(UpgradeRegistry.MOBILITY), sprocketsL, sprocketsR);
    	setVisibility(armsAssistant.hasUpgrade(UpgradeRegistry.COMMUNICATIONS), aerial);
    	setVisibility(armsAssistant.hasUpgrade(UpgradeRegistry.STORAGE), chest);
    	setVisibility(armsAssistant.hasUpgrade(UpgradeRegistry.RIDING), seat);
        this.body.render(f5);
    }
    
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
    	upperTurntable.rotateAngleY = netHeadYaw * (float) (Math.PI / 180.0F);
    	rightRail.rotateAngleX = leftRail.rotateAngleX = headPitch * (float) (Math.PI / 180.0F);
    }
    
    private void setVisibility(boolean visibility, ModelRenderer... modelRenderers)
    {
    	for (ModelRenderer renderer : modelRenderers)
    	{
    		renderer.showModel = visibility;
    	}
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) 
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}