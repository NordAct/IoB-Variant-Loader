package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.skrill.Skrill;
import com.GACMD.isleofberk.entity.dragons.skrill.SkrillModel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.VariantNameHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkrillModel.class)
public abstract class SkrillModelMixin {
    @Inject(method = "getTextureLocation*", at = @At("RETURN"), cancellable = true, remap = false)
    private void setVariantFromName(Skrill entity, CallbackInfoReturnable<ResourceLocation> cir) {
        ResourceLocation id = ResourceUtil.getCustomTexturePath(entity, "skrill");
        if (Minecraft.getInstance().getResourceManager().hasResource(id)) {
            cir.setReturnValue(id);
            return;
        }

        if (entity instanceof VariantNameHelper helper) {
            ResourceLocation variant = ResourceUtil.getVariantTexturePath(helper.getVariantName(), "skrill");
            if (Minecraft.getInstance().getResourceManager().hasResource(variant)) cir.setReturnValue(variant);
        }
    }
}
