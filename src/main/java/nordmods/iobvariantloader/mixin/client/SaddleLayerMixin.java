package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.base.dragon.ADragonRideableUtility;
import com.GACMD.isleofberk.entity.base.render.layer.BaseSaddleAndChestsLayer;
import com.GACMD.isleofberk.entity.base.render.render.BaseRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ModelCacheHelper;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.model_redirect.ModelRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@Mixin(BaseSaddleAndChestsLayer.class)
public abstract class SaddleLayerMixin <T extends ADragonBase & IAnimatable> extends GeoLayerRenderer<T> {
    @Shadow private BaseRenderer<T> baseRenderer;

    public SaddleLayerMixin(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILcom/GACMD/isleofberk/entity/base/dragon/ADragonBase;FFFFFF)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private void getCustomSaddleTexture(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!ResourceUtil.isResourceReloadFinished) return;

        if (!(dragon instanceof ADragonRideableUtility dragonRideableUtility) || !dragonRideableUtility.isSaddled() && !dragonRideableUtility.hasChest()) return;
        if (((ModelCacheHelper)dragon).getSaddleTextureLocationCache() != null) return;

        ResourceLocation id;
        if (!IoBVariantLoader.clientConfig.disableNamedVariants.get()) {
            id = ModelRedirectUtil.getCustomSaddlePath(dragon, baseRenderer.getDragonFolder());
            if (ResourceUtil.isValid(id)) {
                renderSaddle(id, matrixStackIn, bufferIn, packedLightIn, dragon, partialTicks);
                ci.cancel();
            }
        }

        id = ModelRedirectUtil.getVariantSaddlePath(dragon, baseRenderer.getDragonFolder());
        if (ResourceUtil.isValid(id)) {
            renderSaddle(id, matrixStackIn, bufferIn, packedLightIn, dragon, partialTicks);
            ci.cancel();
        }
    }

    private void renderSaddle(ResourceLocation saddle, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T dragon, float partialTicks) {
        RenderType cameo = RenderType.entityCutout(saddle);
        GeoModel model = getEntityModel().getModel(this.baseRenderer.getGeoModelProvider().getModelLocation(dragon));
        getRenderer().render(model, dragon, partialTicks, cameo, matrixStackIn, bufferIn, bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
