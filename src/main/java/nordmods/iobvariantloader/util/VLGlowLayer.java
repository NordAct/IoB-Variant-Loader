package nordmods.iobvariantloader.util;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.base.render.render.BaseRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class VLGlowLayer<T extends ADragonBase & IAnimatable> extends GeoLayerRenderer<T> {

    public VLGlowLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation id = getGlowLayerLocation(dragon);
        if (!ResourceUtil.isValid(id)) return;

        RenderType cameo =  RenderType.eyes(id);
        getRenderer().render(getModel(dragon), dragon, partialTicks, cameo, matrixStackIn, bufferIn,
                bufferIn.getBuffer(cameo), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private ResourceLocation getGlowLayerLocation(T dragon) {
        BaseRenderer<T> baseRenderer = (BaseRenderer<T>) getRenderer();
        String namespace = baseRenderer.getTextureLocation(dragon).getNamespace();
        String path = baseRenderer.getTextureLocation(dragon).getPath().replace(".png", "_glowing.png");
        return new ResourceLocation(namespace, path);
    }

    private GeoModel getModel(T dragon) {
        return getEntityModel().getModel(getEntityModel().getModelLocation(dragon));
    }
}
