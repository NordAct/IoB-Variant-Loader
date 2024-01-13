package nordmods.iobvariantloader.util.layer;

import com.GACMD.isleofberk.entity.base.render.render.BaseRenderer;
import com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.DeadlyNadderModelCacheHelper;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class VLDeadlyNadderWingLayer extends GeoLayerRenderer<DeadlyNadder> {
    public VLDeadlyNadderWingLayer(IGeoRenderer<DeadlyNadder> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, DeadlyNadder dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderType cameo = getRenderType(getTexture(dragon));

        getRenderer().render(getModel(dragon), dragon, partialTicks, cameo, matrixStackIn, bufferIn,
                bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private GeoModel getModel(DeadlyNadder dragon) {
        return getEntityModel().getModel(getEntityModel().getModelLocation(dragon));
    }

    protected ResourceLocation getTexture(DeadlyNadder dragon) {
        if (((DeadlyNadderModelCacheHelper)dragon).getWingLayerLocationCache() != null) return ((DeadlyNadderModelCacheHelper)dragon).getWingLayerLocationCache();

        BaseRenderer<DeadlyNadder> baseRenderer = (BaseRenderer<DeadlyNadder>) getRenderer();
        String namespace = baseRenderer.getTextureLocation(dragon).getNamespace();
        String path = baseRenderer.getTextureLocation(dragon).getPath().replace(".png", "_membranes.png");
        ResourceLocation id = new ResourceLocation(namespace, path);
        ((DeadlyNadderModelCacheHelper)dragon).setWingLayerLocationCache(id);

        return id;
    }
}
