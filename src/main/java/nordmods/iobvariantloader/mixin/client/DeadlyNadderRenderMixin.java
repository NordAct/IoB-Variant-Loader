package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.base.render.render.BaseRenderer;
import com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadder;
import com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadderRender;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import nordmods.iobvariantloader.util.DeadlyNadderWingGlowLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@Mixin(DeadlyNadderRender.class)
public abstract class DeadlyNadderRenderMixin extends BaseRenderer<DeadlyNadder> {
    protected DeadlyNadderRenderMixin(EntityRendererProvider.Context renderManager, AnimatedGeoModel<DeadlyNadder> modelProvider) {
        super(renderManager, modelProvider);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)V", at = @At("TAIL"))
    private void addWingGlowLayer(EntityRendererProvider.Context renderManager, CallbackInfo ci) {
        addLayer(new DeadlyNadderWingGlowLayer(this));
    }
}
