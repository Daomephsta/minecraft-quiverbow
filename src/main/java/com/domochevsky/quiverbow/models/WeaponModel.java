package com.domochevsky.quiverbow.models;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.models.AATransformsMetadataSerialiser.AATransforms;
import com.google.common.collect.ImmutableSet;

import daomephsta.umbra.resources.ResourceLocationExt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;

public class WeaponModel implements IModel
{
	private static final Logger LOGGER = LogManager.getLogger();
	private final ResourceLocation 
		baseModelIdentifier, 
		baseModelLocation;
	private IModel baseModel;
	
	private WeaponModel(ResourceLocation baseModelIdentifier)
	{
		this.baseModelIdentifier = ResourceLocationExt.subPath(baseModelIdentifier, 1);
		this.baseModelLocation = ResourceLocationExt.suffixPath(baseModelIdentifier, ".json");
	}
	
	private IModel getBaseModel()
	{
		if (baseModel == null)
		{
			baseModel = ModelLoaderRegistry.getModelOrLogError(baseModelIdentifier, String.format(
				"Could not load vanilla base model %s", baseModelIdentifier));
		}
		return baseModel;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	{
		try
		{
			IBakedModel baseModelBaked = getBaseModel().bake(state, format, bakedTextureGetter);
			AATransforms aaTransforms = getAATransforms(Minecraft.getMinecraft().getResourceManager());
			return new BakedWeaponModel(baseModelBaked, aaTransforms);
		}
		catch (Exception e)
		{
			throw new RuntimeException("An exception was thrown while baking a weapon model from " + baseModelIdentifier, e);
		}
	}
	
	private AATransforms getAATransforms(IResourceManager resourceManager) throws IOException
	{
		AATransforms transforms = resourceManager.getResource(baseModelLocation).getMetadata(AATransformsMetadataSerialiser.SECTION_NAME);
		//Fallback to parent transforms
		if (transforms == null)
		{
			Optional<ModelBlock> vanillaModelOptional = getVanillaModel(baseModelIdentifier);
			if (!vanillaModelOptional.isPresent())
			{
				LOGGER.debug("No AA transforms found for {} or any of its parents, falling back to defaults.", baseModelIdentifier);
				return AATransforms.NONE;
			}
			ResourceLocation parentLocation = vanillaModelOptional.get().getParentLocation();
			IResource resource = getModelResource(resourceManager, parentLocation);
			while (!resource.hasMetadata())
			{
				vanillaModelOptional = getVanillaModel(parentLocation);
				if (!vanillaModelOptional.isPresent())
				{
					LOGGER.debug("No AA transforms found for {} or any of its parents, falling back to defaults.", baseModelIdentifier);
					return AATransforms.NONE;
				}
				resource.close();
				resource = getModelResource(resourceManager, parentLocation);
				parentLocation = vanillaModelOptional.get().getParentLocation();
			}
			return resource.getMetadata(AATransformsMetadataSerialiser.SECTION_NAME);
		}
		return transforms;
	}

	private Optional<ModelBlock> getVanillaModel(ResourceLocation location)
	{
		return ModelLoaderRegistry.getModelOrLogError(location, String.format("Could not load vanilla model %s", location))
		.asVanillaModel();
	}

	private IResource getModelResource(IResourceManager resourceManager, ResourceLocation parentLocation) throws IOException
	{
		String actualResourcePath = parentLocation.getResourcePath().endsWith("_internal")
			? parentLocation.getResourcePath().substring(0, parentLocation.getResourcePath().length() - "_internal".length())
			: parentLocation.getResourcePath();
		String filePath = "models/" + actualResourcePath + ".json";
		IResource resource = resourceManager.getResource(ResourceLocationExt.withPath(parentLocation, filePath));
		return resource;
	}
	
	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		return ImmutableSet.of(baseModelIdentifier);
	}
	
	@Override
	public Optional<ModelBlock> asVanillaModel()
	{
		return getBaseModel().asVanillaModel();
	}

	public class BakedWeaponModel implements IBakedModel
	{
		private final IBakedModel baseModel;
		private final AATransforms aaTransforms;
		
		public BakedWeaponModel(IBakedModel baseModel, AATransforms aaTransforms)
		{
			this.baseModel = baseModel;
			this.aaTransforms = aaTransforms;
		}

		public AATransforms getAATransforms()
		{
			return aaTransforms;
		}
		
		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
		{
			return baseModel.getQuads(state, side, rand);
		}

		@Override
		public boolean isAmbientOcclusion()
		{
			return baseModel.isAmbientOcclusion();
		}

		@Override
		public boolean isGui3d()
		{
			return baseModel.isGui3d();
		}

		@Override
		public TextureAtlasSprite getParticleTexture()
		{
			return baseModel.getParticleTexture();
		}

		@Override
		public ItemOverrideList getOverrides()
		{
			return baseModel.getOverrides();
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
		{
			return baseModel.handlePerspective(cameraTransformType);
		}

		@Override
		public boolean isBuiltInRenderer()
		{
			return false;
		}
	}
	
	public static enum Loader implements ICustomModelLoader
	{
		INSTANCE;
		
		@Override
		public boolean accepts(ResourceLocation modelLocation)
		{
			if (modelLocation instanceof ModelResourceLocation) return false;
			return modelLocation.getResourceDomain().equals(QuiverbowMain.MODID)
				&& modelLocation.getResourcePath().contains("weapons")
				&& modelLocation.getResourcePath().endsWith("_internal");
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation) throws Exception
		{
			//Trim _internal from the end
			String resourcePath = modelLocation.getResourcePath().substring(0, modelLocation.getResourcePath().length() - "_internal".length());
			ResourceLocation baseModel = new ResourceLocation(modelLocation.getResourceDomain(), resourcePath);
			return new WeaponModel(baseModel);
		}
		
		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {}
	}
}
