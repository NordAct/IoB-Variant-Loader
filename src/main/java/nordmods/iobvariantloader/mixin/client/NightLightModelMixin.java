package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.nightlight.NightLight;
import com.GACMD.isleofberk.entity.dragons.nightlight.NightLightModel;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.VariantNameHelper;
import nordmods.iobvariantloader.util.modelRedirect.ModelRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NightLightModel.class)
public abstract class NightLightModelMixin {
    @Unique
    private final String ID = "night_light";

    @Inject(method = "getTextureLocation*", at = @At("RETURN"), cancellable = true, remap = false)
    private void setVariantFromName(NightLight entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (!ResourceUtil.isResourceReloadFinished) return;

        ResourceLocation id = ResourceUtil.getCustomTexturePath(entity, ID);
        if (ResourceUtil.isValid(id)) {
            cir.setReturnValue(id);
            return;
        }

        if (entity instanceof VariantNameHelper helper) {
            id = ResourceUtil.getVariantTexturePath(helper.getVariantName(), ID);
            if (ResourceUtil.isValid(id)) cir.setReturnValue(id);
        }
    }

    @Inject(method = "getModelLocation*", at = @At("RETURN"), cancellable = true, remap = false)
    private void setModelFromName(NightLight entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (!ResourceUtil.isResourceReloadFinished) return;

        ResourceLocation id = ModelRedirectUtil.getCustomModelPath(entity, ID);
        if (ResourceUtil.isValid(id)) {
            cir.setReturnValue(id);
            return;
        }

        id = ModelRedirectUtil.getVariantModelPath(entity, ID);
        if (ResourceUtil.isValid(id)) cir.setReturnValue(id);
    }

    @Inject(method = "getAnimationFileLocation*", at = @At("RETURN"), cancellable = true, remap = false)
    private void setAnimationFromName(NightLight entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (!ResourceUtil.isResourceReloadFinished) return;

        ResourceLocation id = ModelRedirectUtil.getCustomAnimationPath(entity, ID);
        if (ResourceUtil.isValid(id)) {
            cir.setReturnValue(id);
            return;
        }

        id = ModelRedirectUtil.getVariantAnimationPath(entity, ID);
        if (ResourceUtil.isValid(id)) cir.setReturnValue(id);
    }
}
