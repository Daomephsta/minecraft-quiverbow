package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ERA_Model extends ModelBase
{
	// fields
	ModelRenderer Barrel1;
	ModelRenderer Barrel2;
	ModelRenderer Stock1;
	ModelRenderer Stock2;
	ModelRenderer Trigger;
	ModelRenderer Chest;

	public ERA_Model()
	{
		textureWidth = 64;
		textureHeight = 32;

		Barrel1 = new ModelRenderer(this, 0, 0);
		Barrel1.addBox(0F, 0F, 0F, 12, 5, 4);
		Barrel1.setRotationPoint(-4F, -1F, -0.9666666F);
		Barrel1.setTextureSize(64, 32);
		Barrel1.mirror = true;
		setRotation(Barrel1, 0F, 0F, 0F);
		Barrel2 = new ModelRenderer(this, 0, 10);
		Barrel2.addBox(0F, 0F, 0F, 6, 4, 3);
		Barrel2.setRotationPoint(8F, -0.5F, -0.5F);
		Barrel2.setTextureSize(64, 32);
		Barrel2.mirror = true;
		setRotation(Barrel2, 0F, 0F, 0F);
		Stock1 = new ModelRenderer(this, 32, 10);
		Stock1.addBox(0F, 0F, 0F, 6, 4, 2);
		Stock1.setRotationPoint(-13F, 1F, 0F);
		Stock1.setTextureSize(64, 32);
		Stock1.mirror = true;
		setRotation(Stock1, 0F, 0F, 0F);
		Stock2 = new ModelRenderer(this, 19, 10);
		Stock2.addBox(0F, 0F, 0F, 4, 4, 2);
		Stock2.setRotationPoint(-8F, 0F, 0F);
		Stock2.setTextureSize(64, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		Trigger = new ModelRenderer(this, 0, 18);
		Trigger.addBox(0F, 0F, 0F, 4, 2, 1);
		Trigger.setRotationPoint(-7.533333F, 4F, 0.5F);
		Trigger.setTextureSize(64, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
		Chest = new ModelRenderer(this, 33, 0);
		Chest.addBox(0F, 0F, 0F, 7, 4, 4);
		Chest.setRotationPoint(-2F, 4F, -1F);
		Chest.setTextureSize(64, 32);
		Chest.mirror = true;
		setRotation(Chest, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		setRotationAngles(x, y, z, yaw, pitch, f5, entity);

		Barrel1.render(f5);
		Barrel2.render(f5);
		Stock1.render(f5);
		Stock2.render(f5);
		Trigger.render(f5);
		Chest.render(f5);
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
	}
}
