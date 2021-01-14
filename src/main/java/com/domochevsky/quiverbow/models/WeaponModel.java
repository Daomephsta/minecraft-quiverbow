package com.domochevsky.quiverbow.models;

import static java.util.stream.Collectors.toCollection;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.config.QuiverbowConfig;
import com.domochevsky.quiverbow.models.AATransformsMetadataSerialiser.AATransforms;
import com.domochevsky.quiverbow.util.ResourceLocationExt;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

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
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

public class WeaponModel implements IModel
{
    public static final Set<ResourceLocation> PORTED = Stream.of("coin_tosser", "coin_tosser_empty", "coin_tosser_mod", "coin_tosser_mod_empty")
        .map(s -> new ResourceLocation(QuiverbowMain.MODID, "models/item/weapons/" + s + "_internal"))
        .collect(toCollection(HashSet::new));
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResourceLocation threedIdentifier, inventoryIdentifier;
    private IModel threed, inventory;

    private WeaponModel(ResourceLocation baseModelIdentifier)
    {
        this.inventoryIdentifier = ResourceLocationExt.subPath(baseModelIdentifier, 1);
        this.threedIdentifier = ResourceLocationExt.suffixPath(inventoryIdentifier, "_3d");
    }

    private IModel get3dModel()
    {
        if (!QuiverbowConfig.use3dModels)
            return getInventoryModel();
        if (threed == null)
        {
            threed = ModelLoaderRegistry.getModelOrLogError(threedIdentifier, String.format(
                "Could not load vanilla 3d model %s", threedIdentifier));
        }
        return threed;
    }

    private IModel getInventoryModel()
    {
        if (inventory == null)
        {
            inventory = ModelLoaderRegistry.getModelOrLogError(inventoryIdentifier, String.format(
                "Could not load vanilla inventory model %s", inventoryIdentifier));
        }
        return inventory;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        try
        {
            IBakedModel threedBaked = get3dModel().bake(state, format, bakedTextureGetter);
            IBakedModel inventoryBaked = getInventoryModel().bake(state, format, bakedTextureGetter);
            AATransforms aaTransforms = getAATransforms(Minecraft.getMinecraft().getResourceManager());
            return new Baked(threedBaked, inventoryBaked, aaTransforms);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception was thrown while baking a weapon model from " + inventoryIdentifier, e);
        }
    }

    private AATransforms getAATransforms(IResourceManager resourceManager) throws IOException
    {
        AATransforms transforms = resourceManager.getResource(
            ResourceLocationExt.addToPath(inventoryIdentifier, "models/", ".json"))
                .getMetadata(AATransformsMetadataSerialiser.SECTION_NAME);
        //Fallback to parent transforms
        if (transforms == null)
        {
            Optional<ModelBlock> vanillaModelOptional = getVanillaModel(inventoryIdentifier);
            if (!vanillaModelOptional.isPresent())
            {
                LOGGER.debug("No AA transforms found for {} or any of its parents, falling back to defaults.", inventoryIdentifier);
                return AATransforms.NONE;
            }
            ResourceLocation parentLocation = vanillaModelOptional.get().getParentLocation();
            IResource resource = getModelResource(resourceManager, parentLocation);
            while (!resource.hasMetadata())
            {
                vanillaModelOptional = getVanillaModel(parentLocation);
                if (!vanillaModelOptional.isPresent())
                {
                    LOGGER.debug("No AA transforms found for {} or any of its parents, falling back to defaults.", inventoryIdentifier);
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
        Builder<ResourceLocation> builder = ImmutableSet.builder();
        builder.add(inventoryIdentifier);
        if (QuiverbowConfig.use3dModels)
            builder.add(threedIdentifier);
        return builder.build();
    }

    @Override
    public Optional<ModelBlock> asVanillaModel()
    {
        return getInventoryModel().asVanillaModel();
    }

    public class Baked implements IBakedModel
    {
        private final IBakedModel threed, inventory;
        private final AATransforms aaTransforms;

        public Baked(IBakedModel threed, IBakedModel inventory, AATransforms aaTransforms)
        {
            this.threed = threed;
            this.inventory = inventory;
            this.aaTransforms = aaTransforms;
        }

        public AATransforms getAATransforms()
        {
            return aaTransforms;
        }

        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
        {
            return inventory.getQuads(state, side, rand);
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return true;//inventory.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d()
        {
            return true;//inventory.isGui3d();
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return inventory.getParticleTexture();
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return inventory.getOverrides();
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransform)
        {
            if (cameraTransform == TransformType.GUI)
                return inventory.handlePerspective(cameraTransform);
            return threed.handlePerspective(cameraTransform);
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
                && modelLocation.getResourcePath().endsWith("_internal")
                && PORTED.contains(modelLocation);
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
