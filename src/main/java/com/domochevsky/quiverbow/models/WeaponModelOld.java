package com.domochevsky.quiverbow.models;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.domochevsky.quiverbow.util.ResourceLocationExt;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

public class WeaponModelOld implements IModel {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResourceLocation baseModelIdentifier;
    private IModel baseModel;

    private WeaponModelOld(ResourceLocation baseModelIdentifier) {
        this.baseModelIdentifier = ResourceLocationExt.subPath(baseModelIdentifier, 1);
    }

    private IModel getBaseModel() {
        if (this.baseModel != null) return this.baseModel;
        this.baseModel = ModelLoaderRegistry.getModelOrLogError(this.baseModelIdentifier, String.format("Could not load vanilla base model %s", new Object[]{this.baseModelIdentifier}));
        return this.baseModel;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        try {
            IBakedModel baseModelBaked = this.getBaseModel().bake(state, format, bakedTextureGetter);
            AATransformsMetadataSerialiser.AATransforms aaTransforms = this.getAATransforms(Minecraft.getMinecraft().getResourceManager());
            return new BakedWeaponModel(baseModelBaked, aaTransforms);
        }
        catch (Exception e) {
            throw new RuntimeException("An exception was thrown while baking a weapon model from " + this.baseModelIdentifier, e);
        }
    }

    private AATransformsMetadataSerialiser.AATransforms getAATransforms(IResourceManager resourceManager) throws IOException {
        AATransformsMetadataSerialiser.AATransforms transforms = (AATransformsMetadataSerialiser.AATransforms)resourceManager.getResource(ResourceLocationExt.addToPath(this.baseModelIdentifier, "models/", ".json")).getMetadata("quiverbow_restrung:aa_transforms");
        if (transforms != null) return transforms;
        Optional<ModelBlock> vanillaModelOptional = this.getVanillaModel(this.baseModelIdentifier);
        if (!vanillaModelOptional.isPresent()) {
            LOGGER.debug("No AA transforms found for {} or any of its parents, falling back to defaults.", this.baseModelIdentifier);
            return AATransformsMetadataSerialiser.AATransforms.NONE;
        }
        ResourceLocation parentLocation = vanillaModelOptional.get().getParentLocation();
        if (parentLocation == null) {
            LOGGER.debug("No AA transforms found for {} or any of its parents, falling back to defaults.", this.baseModelIdentifier);
            return AATransformsMetadataSerialiser.AATransforms.NONE;
        }
        IResource resource = this.getModelResource(resourceManager, parentLocation);
        while (!resource.hasMetadata()) {
            vanillaModelOptional = this.getVanillaModel(parentLocation);
            if (!vanillaModelOptional.isPresent()) {
                LOGGER.debug("No AA transforms found for {} or any of its parents, falling back to defaults.", this.baseModelIdentifier);
                return AATransformsMetadataSerialiser.AATransforms.NONE;
            }
            resource.close();
            resource = this.getModelResource(resourceManager, parentLocation);
            parentLocation = vanillaModelOptional.get().getParentLocation();
        }
        return (AATransformsMetadataSerialiser.AATransforms)resource.getMetadata("quiverbow_restrung:aa_transforms");
    }

    private Optional<ModelBlock> getVanillaModel(ResourceLocation location) {
        return ModelLoaderRegistry.getModelOrLogError(location, String.format("Could not load vanilla model %s", new Object[]{location})).asVanillaModel();
    }

    private IResource getModelResource(IResourceManager resourceManager, ResourceLocation parentLocation) throws IOException {
        String actualResourcePath = parentLocation.getResourcePath().endsWith("_internal") ? parentLocation.getResourcePath().substring(0, parentLocation.getResourcePath().length() - "_internal".length()) : parentLocation.getResourcePath();
        String filePath = "models/" + actualResourcePath + ".json";
        return resourceManager.getResource(ResourceLocationExt.withPath(parentLocation, filePath));
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableSet.of(this.baseModelIdentifier);
    }

    @Override
    public Optional<ModelBlock> asVanillaModel() {
        return this.getBaseModel().asVanillaModel();
    }

    public static class BakedWeaponModel implements IBakedModel
    {
        private final IBakedModel baseModel;
        private final AATransformsMetadataSerialiser.AATransforms aaTransforms;

        public BakedWeaponModel(IBakedModel baseModel, AATransformsMetadataSerialiser.AATransforms aaTransforms)
        {
            this.baseModel = baseModel;
            this.aaTransforms = aaTransforms;
        }

        public AATransformsMetadataSerialiser.AATransforms getAATransforms() {
            return aaTransforms;
        }

        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
            return baseModel.getQuads(state, side, rand);
        }

        @Override
        public boolean isAmbientOcclusion() {
            return baseModel.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return baseModel.isGui3d();
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return baseModel.getParticleTexture();
        }

        @Override
        public ItemOverrideList getOverrides() {
            return baseModel.getOverrides();
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
            return baseModel.handlePerspective(cameraTransformType);
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }
    }

    public enum Loader implements ICustomModelLoader
    {
        INSTANCE;

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            if (modelLocation instanceof ModelResourceLocation) {
                return false;
            }
            if (!modelLocation.getResourceDomain().equals("quiverbow_restrung")) return false;
            if (!modelLocation.getResourcePath().contains("weapons")) return false;
            if (!modelLocation.getResourcePath().endsWith("_internal")) return false;
            if (WeaponModel.PORTED.contains(modelLocation)) return false;
            return true;
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            String resourcePath = modelLocation.getResourcePath().substring(0, modelLocation.getResourcePath().length() - "_internal".length());
            ResourceLocation baseModel = new ResourceLocation(modelLocation.getResourceDomain(), resourcePath);
            return new WeaponModelOld(baseModel);
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
        }
    }
}

