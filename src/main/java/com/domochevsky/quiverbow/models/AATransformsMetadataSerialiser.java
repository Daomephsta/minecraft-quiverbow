package com.domochevsky.quiverbow.models;

import java.lang.reflect.Type;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.google.gson.*;

import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.JsonUtils;

public enum AATransformsMetadataSerialiser implements IMetadataSectionSerializer<AATransformsMetadataSerialiser.AATransforms>
{
	INSTANCE;

	public static final String SECTION_NAME = QuiverbowMain.MODID + ":aa_transforms";
	private static final float ONE_DEGREE_IN_RADIANS = (float) (Math.PI / 180.0F);
	private static final String
		TRANSLATION = "translation",
		ROTATION = "rotation",
		SCALE = "scale",
		LEFT_RAIL = "left_rail",
		RIGHT_RAIL = "right_rail";
	private static final Vector3f 
		X = new Vector3f(1, 0, 0),
		Y = new Vector3f(0, 1, 0),
		Z = new Vector3f(0, 0, 1);
	
	@Override
	public AATransformsMetadataSerialiser.AATransforms deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject jsonObj = JsonUtils.getJsonObject(json, SECTION_NAME);

		Matrix4f leftRail = jsonObj.has(LEFT_RAIL)
			? deserialiseTransform(JsonUtils.getJsonObject(jsonObj, LEFT_RAIL))
			: new Matrix4f();
			
		//Falls back to left rail transform matrix, and then to identity matrix
		Matrix4f rightRail = jsonObj.has(RIGHT_RAIL)
			? deserialiseTransform(JsonUtils.getJsonObject(jsonObj, RIGHT_RAIL))
			: (leftRail != null ? leftRail : new Matrix4f());
			
		return new AATransforms(leftRail, rightRail);
	}

	public Matrix4f deserialiseTransform(JsonObject transformJson)
	{
		Vector3f translation = transformJson.has(TRANSLATION) 
			? deserialiseVector3f(JsonUtils.getJsonArray(transformJson, TRANSLATION), TRANSLATION)
			: new Vector3f();
		translation.scale(0.0625F);
		Vector3f rotation = transformJson.has(ROTATION)
			? deserialiseVector3f(JsonUtils.getJsonArray(transformJson, ROTATION), ROTATION)
			: new Vector3f();
		Vector3f scale = transformJson.has(SCALE)
			? deserialiseVector3f(JsonUtils.getJsonArray(transformJson, SCALE), SCALE)
			: new Vector3f(1, 1, 1);
		return createTransformMatrix(translation, rotation, scale);
	}

	private Vector3f deserialiseVector3f(JsonArray jsonArray, String memberName)
	{
		if(jsonArray.size() != 3) throw new JsonSyntaxException("Expected 3 elements in element " + memberName + ", found " + jsonArray.size());
		float x = jsonArray.get(0).getAsFloat();
		float y = jsonArray.get(1).getAsFloat();
		float z = jsonArray.get(2).getAsFloat();
		return new Vector3f(x, y, z);
	}
	
	public static Matrix4f createTransformMatrix(Vector3f translation, Vector3f rotation, Vector3f scale)
	{
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.translate(translation);
		matrix4f.rotate(rotation.x * ONE_DEGREE_IN_RADIANS, X);
		matrix4f.rotate(rotation.y * ONE_DEGREE_IN_RADIANS, Y);
		matrix4f.rotate(rotation.z * ONE_DEGREE_IN_RADIANS, Z);
		matrix4f.scale(scale);
		return matrix4f;
	}

	@Override
	public String getSectionName()
	{
		return SECTION_NAME;
	}		
	
	public static class AATransforms implements IMetadataSection
	{
		public static final AATransforms NONE = new AATransforms(Matrix4f.setIdentity(new Matrix4f()), Matrix4f.setIdentity(new Matrix4f())); 
		private final Matrix4f leftRailTransform,
							   rightRailTransform;

		private AATransforms(Matrix4f leftRailTransform, Matrix4f rightRailTransform)
		{
			this.leftRailTransform = leftRailTransform;
			this.rightRailTransform = rightRailTransform;
		}
		
		public Matrix4f forRail(EnumHand rail)
		{
			switch (rail)
			{
			case MAIN_HAND:
				return leftRailTransform;
			case OFF_HAND:
				return rightRailTransform;
			default:
				throw new RuntimeException("Unknown rail " + rail);
			}
		}
	}
}