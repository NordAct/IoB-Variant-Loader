package nordmods.iobvariantloader.mixin.client.isleofberk.egg;

import com.GACMD.isleofberk.entity.eggs.entity.base.large.ADragonLargeEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.base.large.ADragonLargeEggRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.VLGlowLayerHelper;
import nordmods.iobvariantloader.util.layer.VLGlowLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

import javax.annotation.Nullable;

@Mixin(ADragonLargeEggRenderer.class)
public abstract class ADragonLargeEggRendererMixin extends GeoProjectilesRenderer<ADragonLargeEggBase> implements VLGlowLayerHelper<ADragonLargeEggBase> {
    @Unique
    private final VLGlowLayer<ADragonLargeEggBase> glowLayer = new VLGlowLayer<>(this);

    private ADragonLargeEggRendererMixin(EntityRendererProvider.Context renderManager, AnimatedGeoModel<ADragonLargeEggBase> modelProvider) {
        super(renderManager, modelProvider);
    }


    @Override
    public void render(GeoModel model, ADragonLargeEggBase animatable, float partialTicks, RenderType type, PoseStack matrixStackIn,
                       @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn,
                       int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        glowLayer.render(matrixStackIn, renderTypeBuffer, packedLightIn, animatable, 0, 0, partialTicks, animatable.getAge(), 0, 0);
    }

    @Override
    public void reRender(GeoModel model, ADragonLargeEggBase animatable, float partialTicks, RenderType type, PoseStack matrixStackIn,
                  @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn,
                  int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    //*screams*
    @Override
    public ResourceLocation getTextureLocation(ADragonLargeEggBase instance) {
        return super.getTextureLocation(instance);
    }
}
