package com.domochevsky.quiverbow.armsassistant;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.google.gson.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.*;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = QuiverbowMain.MODID)
public class AATransforms
{ 
	private static final ResourceLocation TRANSFORM_FILE_LOCATION = new ResourceLocation(QuiverbowMain.MODID, "models/item/aa_transforms.json");
	private static final Gson DESERIALISER = new GsonBuilder()
		.registerTypeAdapter(AATransform.class, AATransform.DESERIALISER).create();
		
	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent e)
	{
		IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = e.getModelRegistry();
		Map<ModelResourceLocation, AATransform> transforms = loadTransforms();
		// Iterate through the transform list, attaching the transforms to their corresponding models
		for(Map.Entry<ModelResourceLocation, AATransform> transform : transforms.entrySet())
		{
			IBakedModel original = modelRegistry.getObject(transform.getKey());
			if (original != null)
				modelRegistry.putObject(transform.getKey(), new ModelWithAATransform(original, transform.getValue()));
			else
				QuiverbowMain.logger.warn("No model found for " + transform.getKey());
		}
	}
	
	private static Map<ModelResourceLocation, AATransform> loadTransforms() 
	{
		if(FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			throw new IllegalStateException("An attempt was made to load AA transforms on the server");
		Map<ModelResourceLocation, AATransform> transforms = new HashMap<>();
		try
		{
			// Get all the AA Transform files in all loaded resource packs in ascending priority order
			List<IResource> transformFiles = Minecraft.getMinecraft().getResourceManager().getAllResources(TRANSFORM_FILE_LOCATION);
			JsonParser parser = new JsonParser();
			/* Iterate over the transform files, adding their transforms to a map.
			 * Using a map makes sure higher priority resource packs override transforms
			 * defined by lower priority resource packs*/
			for(IResource transformFile : transformFiles)
			{
				JsonObject jsonMap = parser.parse(new InputStreamReader(transformFile.getInputStream())).getAsJsonObject();
				for(Entry<String, JsonElement> member : jsonMap.entrySet())
				{
					ModelResourceLocation mrl = ModelLoader.getInventoryVariant(member.getKey());	
					AATransform transform = DESERIALISER.fromJson(member.getValue(), AATransform.class);
					transforms.put(mrl, transform);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return transforms;
	}

	public static Matrix4f getTransform(IBakedModel model)
	{
		return model instanceof ModelWithAATransform ? ((ModelWithAATransform) model).transforms.getTransformMatrix() : null;
	}
	
	/** Wrapper class that attaches an AATransform to any baked model **/
	private static class ModelWithAATransform implements IBakedModel
	{
		private final IBakedModel wrappedModel;
		private final AATransform transforms;
		
		public ModelWithAATransform(IBakedModel wrappedModel, AATransform transforms)
		{
			this.wrappedModel = wrappedModel;
			this.transforms = transforms;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
		{
			return wrappedModel.getQuads(state, side, rand);
		}

		@Override
		public boolean isAmbientOcclusion()
		{
			return wrappedModel.isAmbientOcclusion();
		}

		@Override
		public boolean isGui3d()
		{
			return wrappedModel.isGui3d();
		}

		@Override
		public boolean isBuiltInRenderer()
		{
			return wrappedModel.isBuiltInRenderer();
		}

		@Override
		public TextureAtlasSprite getParticleTexture()
		{
			return wrappedModel.getParticleTexture();
		}

		@Override
		public ItemOverrideList getOverrides()
		{
			return wrappedModel.getOverrides();
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public ItemCameraTransforms getItemCameraTransforms()
		{
			return wrappedModel.getItemCameraTransforms();
		}
		
		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
		{
			return wrappedModel.handlePerspective(cameraTransformType);
		}
	}
	
	private static class AATransform
	{
		static final JsonDeserializer<AATransform> DESERIALISER = new Deserialiser();
		private final Matrix4f transformMatrix;

		private AATransform(Matrix4f transformMatrix)
		{
			this.transformMatrix = transformMatrix;
		}
		
		Matrix4f getTransformMatrix()
		{
			return transformMatrix;
		}
		
		private static class Deserialiser implements JsonDeserializer<AATransform>
		{
			@Override
			public AATransform deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				JsonObject jsonObj = json.getAsJsonObject();
				Vector3f translation = deserialiseVector3f(JsonUtils.getJsonArray(jsonObj, "translation"), "translation");
				translation.scale(0.0625F);
				translation.clamp(-5.0F, 5.0F);
				Vector3f rotation = deserialiseVector3f(JsonUtils.getJsonArray(jsonObj, "rotation"), "rotation");
				Vector3f scale = deserialiseVector3f(JsonUtils.getJsonArray(jsonObj, "scale"), "scale");
				scale.clamp(-4.0F, 4.0F);
				Matrix4f transformMatrix = new TRSRTransformation(translation, TRSRTransformation.quatFromXYZDegrees(rotation), scale, null).getMatrix();
				return new AATransform(transformMatrix);
			}

			private Vector3f deserialiseVector3f(JsonArray jsonArray, String memberName)
			{
				if(jsonArray.size() != 3) throw new JsonSyntaxException("Expected 3 elements in element " + memberName + ", found " + jsonArray.size());
				float x = jsonArray.get(0).getAsFloat();
				float y = jsonArray.get(1).getAsFloat();
				float z = jsonArray.get(2).getAsFloat();
				return new Vector3f(x, y, z);
			}
		}
	}
}
