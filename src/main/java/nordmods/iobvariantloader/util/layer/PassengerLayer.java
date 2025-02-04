package nordmods.iobvariantloader.util.layer;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import nordmods.iobvariantloader.IoBVariantLoaderClient;
import nordmods.iobvariantloader.util.ducks.ModelSizeProvider;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.List;

public class PassengerLayer {
    public static <T extends ADragonBase & IAnimatable> void renderPassenger(T animatable, AnimatedGeoModel<T> modelProvider, GeoBone bone, PoseStack stack, VertexConsumer bufferIn, GeoEntityRenderer<T> renderer, int packedLightIn) {
        List<Entity> passengers = animatable.getPassengers();
        for (int i = 0; i < passengers.size(); i++) {
            Entity passenger = passengers.get(i);
            String name = "passenger" + i;
            if (modelProvider.getAnimationProcessor().getBone(name) == null) {
                IoBVariantLoaderClient.PASSENGERS.remove(passenger.getUUID());
                continue;
            }
            if (bone.name.equals(name)) {
                IoBVariantLoaderClient.PASSENGERS.remove(passenger.getUUID());
                float deltaFrameTime = Minecraft.getInstance().getDeltaFrameTime();
                stack.pushPose();
                float scale = 1/animatable.getScale() / ((ModelSizeProvider)animatable).getModelSize();

                stack.translate(0, (passenger.getMyRidingOffset() * 2) * scale, 0);
                RenderUtils.moveToPivot(bone, stack);
                stack.mulPose(Vector3f.YN.rotationDegrees(180f - passenger.getViewYRot(deltaFrameTime)));
                stack.scale(scale, scale, scale);
                renderPassenger(passenger, deltaFrameTime, stack, renderer.getCurrentRTB(), packedLightIn);
                bufferIn = renderer.getCurrentRTB().getBuffer(renderer.getRenderType(animatable, deltaFrameTime, stack, renderer.getCurrentRTB(), bufferIn, packedLightIn, renderer.getTextureLocation(animatable)));
                stack.popPose();
                IoBVariantLoaderClient.PASSENGERS.add(passenger.getUUID());
            }
        }
    }


    private static  <E extends Entity> void renderPassenger(E entityIn, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
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
