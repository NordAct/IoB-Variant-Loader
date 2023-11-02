package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.terrible_terror.TerribleTerror;
import com.GACMD.isleofberk.entity.dragons.terrible_terror.TerribleTerrorModel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.VariantNameHelper;
import nordmods.iobvariantloader.util.modelRedirect.ModelRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TerribleTerrorModel.class)
public abstract class TerribleTerrorModelMixin {
    @Unique private final String ID = "terrible_terror";

    @Inject(method = "getTextureLocation*", at = @At("RETURN"), cancellable = true, remap = false)
    private void setVariantFromName(TerribleTerror entity, CallbackInfoReturnable<ResourceLocation> cir) {
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
    private void setModelFromName(TerribleTerror entity, CallbackInfoReturnable<ResourceLocation> cir) {
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
    private void setAnimationFromName(TerribleTerror entity, CallbackInfoReturnable<ResourceLocation> cir) {
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
