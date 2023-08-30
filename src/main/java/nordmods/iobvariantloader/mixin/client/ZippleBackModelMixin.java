package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.zippleback.ZippleBack;
import com.GACMD.isleofberk.entity.dragons.zippleback.ZippleBackModel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.VariantNameHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZippleBackModel.class)
public abstract class ZippleBackModelMixin {
    @Inject(method = "getTextureLocation*", at = @At("RETURN"), cancellable = true, remap = false)
    private void setVariantFromName(ZippleBack entity, CallbackInfoReturnable<ResourceLocation> cir) {
        ResourceLocation id = ResourceUtil.getCustomTexturePath(entity, "zippleback");
        if (Minecraft.getInstance().getResourceManager().hasResource(id)) {
            cir.setReturnValue(id);
            return;
        }
        if (entity instanceof VariantNameHelper helper) {
            ResourceLocation variant = ResourceUtil.getVariantTexturePath(helper.getVariantName(), "zippleback");
            if (Minecraft.getInstance().getResourceManager().hasResource(variant)) cir.setReturnValue(variant);
        }
    }
}
