package nordmods.iobvariantloader.mixin.client.isleofberk.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.base.render.render.BaseRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3d;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import nordmods.iobvariantloader.util.VLGlowLayerHelper;
import nordmods.iobvariantloader.util.layer.VLGlowLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

@Mixin(BaseRenderer.class)
public abstract class BaseRendererMixin<T extends ADragonBase & IAnimatable> extends GeoEntityRenderer<T> implements VLGlowLayerHelper<T> {
    public BaseRendererMixin(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider) {
        super(renderManager, modelProvider);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lsoftware/bernie/geckolib3/model/AnimatedGeoModel;)V",
            at = @At("TAIL"),
            remap = false)
    private void addGlowLayer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider, CallbackInfo ci) {
        addLayer(new VLGlowLayer<>(this));
    }

    @Override
    public void reRender(GeoModel model, T animatable, float partialTicks, RenderType type, PoseStack matrixStackIn,
                         @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn,
                         int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    //@Override
    //public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn,
    //                              int packedOverlayIn, float red, float green, float blue, float alpha) {
    //    if (bone.name.equals("leftWingClaw") || bone.name.equals("rightWingClaw")) {
    //        stack.pushPose();
    //        preparePositionRotationScale(bone, stack);
    //        Vector3d pos = bone.getWorldPosition();
    //        animatable.getLevel().addParticle(ParticleTypes.DRAGON_BREATH, pos.x, pos.y, pos.z, 0, 0, 0);
    //        stack.popPose();
    //    }
    //    super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    //}
}
