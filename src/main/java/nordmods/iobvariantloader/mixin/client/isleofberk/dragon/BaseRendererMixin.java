package nordmods.iobvariantloader.mixin.client.isleofberk.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.base.render.render.BaseRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import nordmods.iobvariantloader.IoBVariantLoader;
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
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.List;

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

    @Override
    public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn,
                                  int packedOverlayIn, float red, float green, float blue, float alpha) {
        List<Entity> passengers = animatable.getPassengers();
        for (int i = 0; i < passengers.size(); i++) {
            Entity passenger = passengers.get(i);
            String name = "passenger" + i;
            if (modelProvider.getAnimationProcessor().getBone(name) == null) {
                IoBVariantLoader.PASSENGERS.remove(passenger.getUUID());
                continue;
            }
            if (bone.name.equals(name)) {
                IoBVariantLoader.PASSENGERS.remove(passenger.getUUID());
                float deltaFrameTime = Minecraft.getInstance().getDeltaFrameTime();
                stack.pushPose();
                float scale = 1/animatable.getScale() * 0.8f;

                stack.translate(0, (passenger.getMyRidingOffset() * 2) * scale, 0);
                RenderUtils.moveToPivot(bone, stack);
                stack.mulPose(Vector3f.YN.rotationDegrees(180f - passenger.getViewYRot(deltaFrameTime)));
                stack.scale(scale, scale, scale);
                renderPassenger(passenger, deltaFrameTime, stack, getCurrentRTB(), packedLightIn);
                bufferIn = getCurrentRTB().getBuffer(getRenderType(animatable, deltaFrameTime, stack, getCurrentRTB(), bufferIn, packedLightIn, getTextureLocation(animatable)));
                stack.popPose();
                IoBVariantLoader.PASSENGERS.add(passenger.getUUID());
            }
        }
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    private <E extends Entity> void renderPassenger(E entityIn, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
        boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (isFirstPerson && entityIn == clientPlayer) return;

        EntityRenderer<? super E> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entityIn);
        try {
            render.render(entityIn, 0, partialTicks, matrixStack, bufferIn, packedLight);
        } catch (Throwable throwable1) {
            throw new ReportedException(CrashReport.forThrowable(throwable1, "Rendering entity in world"));
        }
    }
}
