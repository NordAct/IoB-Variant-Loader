package nordmods.iobvariantloader.mixin.client;

import com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadder;
import com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadderWingLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.VariantNameHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DeadlyNadderWingLayer.class)
public abstract class DeadlyNadderWingLayerMixin {
    @Unique
    private final String ID = "deadly_nadder";

    @Inject(method = "getNadderEntityTexture", at = @At("RETURN"), cancellable = true, remap = false)
    private void setVariantFromName(DeadlyNadder entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (!ResourceUtil.isResourceReloadFinished) return;

        ResourceLocation id = ResourceUtil.getCustomTexturePath(entity, ID,"_membranes");
        if (ResourceUtil.isValid(id)) {
            cir.setReturnValue(id);
            return;
        }

        if (entity instanceof VariantNameHelper helper) {
            id = ResourceUtil.getVariantTexturePath(helper.getVariantName(), ID,"_membranes");
            if (Minecraft.getInstance().getResourceManager().hasResource(id)) cir.setReturnValue(id);
        }
    }
}
