package com.domochevsky.quiverbow.armsassistant;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelAA extends ModelBase
{
	// fields
	ModelRenderer head;
	ModelRenderer rail;
	ModelRenderer body;
	ModelRenderer leg1;
	ModelRenderer leg2;
	ModelRenderer leg3;
	ModelRenderer leg4;
	ModelRenderer legB1;
	ModelRenderer legB2;
	ModelRenderer legB3;
	ModelRenderer legB4;
	ModelRenderer legC1;
	ModelRenderer legC2;
	ModelRenderer legC3;
	ModelRenderer legC4;

	ModelRenderer tank;

	ModelRenderer armor1;
	ModelRenderer armor2;

	ModelRenderer rail2;
	ModelRenderer rail2b;

	ModelRenderer seat1;
	ModelRenderer seat2;
	ModelRenderer seat3;

	ModelRenderer plate1;
	ModelRenderer plate2;
	ModelRenderer plate3;
	ModelRenderer plate4;

	ModelRenderer antenna1;
	ModelRenderer antenna2;

	public ModelAA()
	{
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this, 0, 0);
		head.addBox(-4F, -8F, -4F, 8, 8, 8);
		head.setRotationPoint(-5F, 9F, 0F);
		head.setTextureSize(64, 64);
		head.mirror = true;
		setRotation(head, 0F, 0F, 0F);

		rail = new ModelRenderer(this, 0, 18);
		rail.addBox(0F, 0F, 0F, 4, 4, 10);
		rail.setRotationPoint(1F, 5F, -5F);
		rail.setTextureSize(64, 64);
		rail.mirror = true;
		setRotation(rail, 0F, 0F, 0F);

		body = new ModelRenderer(this, 33, 0);
		body.addBox(0F, 0F, 0F, 6, 4, 6);
		body.setRotationPoint(-3F, 9F, -3F);
		body.setTextureSize(64, 64);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);

		leg1 = new ModelRenderer(this, 40, 11);
		leg1.addBox(0F, 0F, 0F, 2, 4, 2);
		leg1.setRotationPoint(2F, 12F, -4F);
		leg1.setTextureSize(64, 64);
		leg1.mirror = true;
		setRotation(leg1, -0.1745329F, -0.7853982F, 0F);

		leg2 = new ModelRenderer(this, 40, 11);
		leg2.addBox(0F, 0F, 0F, 2, 4, 2);
		leg2.setRotationPoint(4F, 12F, 2F);
		leg2.setTextureSize(64, 64);
		leg2.mirror = true;
		setRotation(leg2, -0.1745329F, -2.356194F, 0F);

		leg3 = new ModelRenderer(this, 40, 11);
		leg3.addBox(0F, 0F, 0F, 2, 4, 2);
		leg3.setRotationPoint(-4F, 12F, -2F);
		leg3.setTextureSize(64, 64);
		leg3.mirror = true;
		setRotation(leg3, -0.1745329F, 0.7853982F, 0F);

		leg4 = new ModelRenderer(this, 40, 11);
		leg4.addBox(0F, 0F, 0F, 2, 4, 2);
		leg4.setRotationPoint(-2F, 12F, 4F);
		leg4.setTextureSize(64, 64);
		leg4.mirror = true;
		setRotation(leg4, -0.1745329F, 2.356194F, 0F);

		legB1 = new ModelRenderer(this, 33, 11);
		legB1.addBox(0F, 0F, 0F, 1, 4, 2);
		legB1.setRotationPoint(-3F, 14F, -2F);
		legB1.setTextureSize(64, 64);
		legB1.mirror = true;
		setRotation(legB1, -1.624499F, 0.7853982F, 0F);

		legB2 = new ModelRenderer(this, 33, 11);
		legB2.addBox(0F, 0F, 0F, 1, 4, 2);
		legB2.setRotationPoint(2F, 14F, -3F);
		legB2.setTextureSize(64, 64);
		legB2.mirror = true;
		setRotation(legB2, -1.624499F, -0.7853982F, 0F);

		legB3 = new ModelRenderer(this, 33, 11);
		legB3.addBox(0F, 0F, 0F, 1, 4, 2);
		legB3.setRotationPoint(-2F, 14F, 3F);
		legB3.setTextureSize(64, 64);
		legB3.mirror = true;
		setRotation(legB3, -1.623156F, 2.356194F, 0F);

		legB4 = new ModelRenderer(this, 33, 11);
		legB4.addBox(0F, 0F, 0F, 1, 4, 2);
		legB4.setRotationPoint(3F, 14F, 2F);
		legB4.setTextureSize(64, 64);
		legB4.mirror = true;
		setRotation(legB4, -1.624499F, -2.356194F, 0F);

		legC1 = new ModelRenderer(this, 0, 17);
		legC1.addBox(0F, 0F, 0F, 1, 9, 1);
		legC1.setRotationPoint(-6F, 15F, -5F);
		legC1.setTextureSize(64, 64);
		legC1.mirror = true;
		setRotation(legC1, -0.1745329F, 0.7853982F, 0F);

		legC2 = new ModelRenderer(this, 0, 17);
		legC2.addBox(0F, 0F, 0F, 1, 9, 1);
		legC2.setRotationPoint(6F, 15F, 5F);
		legC2.setTextureSize(64, 64);
		legC2.mirror = true;
		setRotation(legC2, -0.1745329F, -2.356194F, 0F);

		legC3 = new ModelRenderer(this, 0, 17);
		legC3.addBox(0F, 0F, 0F, 1, 9, 1);
		legC3.setRotationPoint(5F, 15F, -6F);
		legC3.setTextureSize(64, 64);
		legC3.mirror = true;
		setRotation(legC3, -0.1745329F, -0.7853982F, 0F);

		legC4 = new ModelRenderer(this, 0, 17);
		legC4.addBox(0F, 0F, 0F, 1, 9, 1);
		legC4.setRotationPoint(-5F, 15F, 6F);
		legC4.setTextureSize(64, 64);
		legC4.mirror = true;
		setRotation(legC4, -0.1745329F, 2.356194F, 0F);

		tank = new ModelRenderer(this, 29, 18);
		tank.addBox(0F, 0F, 0F, 10, 10, 4);
		tank.setRotationPoint(-4F, -1F, 5F);
		tank.setTextureSize(64, 64);
		tank.mirror = true;
		setRotation(tank, 0F, 0F, 0F);

		armor1 = new ModelRenderer(this, 0, 33);
		armor1.addBox(0F, 0F, 0F, 12, 15, 1);
		armor1.setRotationPoint(-11F, 5F, -9F);
		armor1.setTextureSize(64, 64);
		armor1.mirror = true;
		setRotation(armor1, 0F, 0F, 0F);

		armor2 = new ModelRenderer(this, 27, 33);
		armor2.addBox(0F, 0F, 0F, 3, 2, 11);
		armor2.setRotationPoint(-6F, 9F, -8F);
		armor2.setTextureSize(64, 64);
		armor2.mirror = true;
		setRotation(armor2, 0F, 0F, 0F);

		rail2 = new ModelRenderer(this, 0, 18);
		rail2.addBox(0F, 0F, 0F, 4, 4, 10);
		rail2.setRotationPoint(6F, 9F, -3F);
		rail2.setTextureSize(64, 64);
		rail2.mirror = true;
		setRotation(rail2, 0F, 0F, 0F);

		rail2b = new ModelRenderer(this, 45, 33);
		rail2b.addBox(0F, 0F, 0F, 3, 2, 4);
		rail2b.setRotationPoint(3F, 9F, -2F);
		rail2b.setTextureSize(64, 64);
		rail2b.mirror = true;
		setRotation(rail2b, 0F, 0F, 0F);

		seat1 = new ModelRenderer(this, 0, 52);
		seat1.addBox(0F, 0F, 0F, 8, 2, 10);
		seat1.setRotationPoint(-3F, -2F, -6F);
		seat1.setTextureSize(64, 64);
		seat1.mirror = true;
		setRotation(seat1, 0F, 0F, 0F);

		seat2 = new ModelRenderer(this, 27, 54);
		seat2.addBox(0F, 0F, 0F, 8, 3, 2);
		seat2.setRotationPoint(-3F, -5F, 2F);
		seat2.setTextureSize(64, 64);
		seat2.mirror = true;
		setRotation(seat2, 0F, 0F, 0F);

		seat3 = new ModelRenderer(this, 48, 47);
		seat3.addBox(0F, 0F, 0F, 2, 9, 4);
		seat3.setRotationPoint(-1F, 0F, -2.466667F);
		seat3.setTextureSize(64, 64);
		seat3.mirror = true;
		setRotation(seat3, 0F, 0F, 0F);

		//

		plate1 = new ModelRenderer(this, 0, 38);
		plate1.addBox(0F, 0F, 0F, 12, 10, 1);
		plate1.setRotationPoint(-7F, 10F, 6F);
		plate1.setTextureSize(64, 64);
		plate1.mirror = true;
		setRotation(plate1, -0.1858931F, 1.570796F, 0F);

		plate2 = new ModelRenderer(this, 0, 36);
		plate2.addBox(0F, 0F, 0F, 12, 11, 1);
		plate2.setRotationPoint(6F, 9F, 7F);
		plate2.setTextureSize(64, 64);
		plate2.mirror = true;
		setRotation(plate2, -0.1858931F, 3.141593F, 0F);

		plate3 = new ModelRenderer(this, 0, 36);
		plate3.addBox(0F, 0F, 0F, 12, 12, 1);
		plate3.setRotationPoint(-6F, 8F, -6F);
		plate3.setTextureSize(64, 64);
		plate3.mirror = true;
		setRotation(plate3, -0.1858931F, 0F, 0F);

		plate4 = new ModelRenderer(this, 0, 38);
		plate4.addBox(0F, 0F, 0F, 12, 9, 1);
		plate4.setRotationPoint(7F, 11F, -6F);
		plate4.setTextureSize(64, 64);
		plate4.mirror = true;
		setRotation(plate4, -0.1858931F, -1.570796F, 0F);

		//

		antenna1 = new ModelRenderer(this, 58, 0);
		antenna1.addBox(0F, 0F, 0F, 1, 15, 1);
		antenna1.setRotationPoint(-6F, -5F, 5F);
		antenna1.setTextureSize(64, 64);
		antenna1.mirror = true;
		setRotation(antenna1, 0F, 0F, 0F);

		antenna2 = new ModelRenderer(this, 19, 18);
		antenna2.addBox(0F, 0F, 0F, 4, 1, 2);
		antenna2.setRotationPoint(-6F, 9F, 3F);
		antenna2.setTextureSize(64, 64);
		antenna2.mirror = true;
		setRotation(antenna2, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);

		this.setRotationAngles(x, y, z, yaw, pitch, f5, entity);

		head.render(f5);
		rail.render(f5);
		body.render(f5);
		leg1.render(f5);
		leg2.render(f5);
		leg3.render(f5);
		leg4.render(f5);
		legB1.render(f5);
		legB2.render(f5);
		legB3.render(f5);
		legB4.render(f5);
		legC1.render(f5);
		legC2.render(f5);
		legC3.render(f5);
		legC4.render(f5);

		EntityAA turret = (EntityAA) entity;

		if (turret.hasStorageUpgrade)
		{
			tank.render(f5);
		}

		if (turret.hasArmorUpgrade)
		{
			armor1.render(f5);
			armor2.render(f5);
		}

		if (turret.hasWeaponUpgrade)
		{
			rail2.render(f5);
			rail2b.render(f5);
		}

		if (turret.hasRidingUpgrade)
		{
			seat1.render(f5);
			seat2.render(f5);
			seat3.render(f5);
		}

		if (turret.hasHeavyPlatingUpgrade)
		{
			plate1.render(f5);
			plate2.render(f5);
			plate3.render(f5);
			plate4.render(f5);
		}

		if (turret.hasCommunicationUpgrade)
		{
			antenna1.render(f5);
			antenna2.render(f5);
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float x, float y, float z, float yaw, float pitch, float tick, Entity entity)
	{
		super.setRotationAngles(x, y, z, yaw, pitch, tick, entity);

		// Looking at things
		this.head.rotateAngleY = yaw / (180F / (float) Math.PI);
		this.head.rotateAngleX = pitch / (180F / (float) Math.PI);

		// Leg movement when wandering around?
		this.legC1.rotateAngleX = MathHelper.cos(x * 0.6662F) * 1.4F * y;
		this.legC1.rotateAngleY = 0.0F;

		this.legC2.rotateAngleX = MathHelper.cos(x * 0.6662F + (float) Math.PI) * 1.4F * y;
		this.legC2.rotateAngleY = 0.0F;

		this.legC3.rotateAngleX = MathHelper.cos(x * 0.6662F) * 1.4F * y;
		this.legC3.rotateAngleY = 0.0F;

		this.legC4.rotateAngleX = MathHelper.cos(x * 0.6662F + (float) Math.PI) * 1.4F * y;
		this.legC4.rotateAngleY = 0.0F;
	}
}
