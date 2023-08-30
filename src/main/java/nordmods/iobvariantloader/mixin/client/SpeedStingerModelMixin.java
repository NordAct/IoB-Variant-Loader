package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStinger;
import com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStingerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.VariantNameHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpeedStingerModel.class)
public abstract class SpeedStingerModelMixin {
    @Inject(method = "getTextureLocation*", at = @At("RETURN"), cancellable = true, remap = false)
    private void setVariantFromName(SpeedStinger entity, CallbackInfoReturnable<ResourceLocation> cir) {
        ResourceLocation id = ResourceUtil.getCustomTexturePath(entity, "speed_stinger");
        if (Minecraft.getInstance().getResourceManager().hasResource(id)) {
            cir.setReturnValue(id);
            return;
        }

        if (entity instanceof VariantNameHelper helper) {
            ResourceLocation variant = ResourceUtil.getVariantTexturePath(helper.getVariantName(), "speed_stinger");
            if (Minecraft.getInstance().getResourceManager().hasResource(variant)) cir.setReturnValue(variant);
        }
    }
}
