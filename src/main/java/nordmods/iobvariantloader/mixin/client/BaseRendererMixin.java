package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.base.render.render.BaseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import nordmods.iobvariantloader.util.VLGlowLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

@Mixin(BaseRenderer.class)
public abstract class BaseRendererMixin<T extends ADragonBase & IAnimatable> extends GeoEntityRenderer<T> {
    public BaseRendererMixin(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider) {
        super(renderManager, modelProvider);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lsoftware/bernie/geckolib3/model/AnimatedGeoModel;)V", at = @At("TAIL"))
    private void addGlowLayer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider, CallbackInfo ci) {
        addLayer(new VLGlowLayer<>(this));
    }
}
