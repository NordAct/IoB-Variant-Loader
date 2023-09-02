package nordmods.iobvariantloader.util;

import com.GACMD.isleofberk.IsleofBerk;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.LayerGlowingAreasGeo;

import java.util.function.Function;

public class OptimisedGlowingLayer<T extends ADragonBase & IAnimatable> extends LayerGlowingAreasGeo<T> {

    public OptimisedGlowingLayer(GeoEntityRenderer<T> renderer, Function<T, ResourceLocation> funcGetCurrentTexture, Function<T, ResourceLocation> funcGetCurrentModel, Function<ResourceLocation, RenderType> funcGetEmissiveRenderType) {
        super(renderer, funcGetCurrentTexture, funcGetCurrentModel, funcGetEmissiveRenderType);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityLivingBaseIn,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                       float headPitch) {
        ResourceLocation id = getRenderer().getGeoModelProvider().getTextureLocation(entityLivingBaseIn);
        id = new ResourceLocation(IsleofBerk.MOD_ID, id.getPath() + ".mcmeta");
        if (Minecraft.getInstance().getResourceManager().hasResource(id))
            super.render(matrixStackIn, bufferIn, packedLightIn, entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }
}
