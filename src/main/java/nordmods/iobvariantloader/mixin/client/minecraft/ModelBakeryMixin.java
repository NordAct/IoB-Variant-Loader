package nordmods.iobvariantloader.mixin.client.minecraft;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.iobvariantloader.util.model_redirect.ModelRedirectUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow protected abstract void loadTopLevel(ModelResourceLocation pLocation);

    @Shadow @Final protected ResourceManager resourceManager;

    @ModifyArg(method = "processLoading", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"))
    private String isARightTimeToAdd(String s) {
        if (s.equals("special")) {
            Set<String> eggModels = new HashSet<>();
            ModelRedirectUtil.EGG_ITEM_MODEL_REDIRECTS.forEach((dragon, redirect) ->
                    redirect.forEach((variant, modelRedirect) -> {
                        if (modelRedirect != null) eggModels.add(modelRedirect);
                    }));
            eggModels.forEach(entry -> loadTopLevel(new ModelResourceLocation(entry, "inventory")));
        }
        return s;
    }

    @Inject(method = "processLoading", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V"))
    private void registerEggItemModelRedirectsEarly(ProfilerFiller pProfiler, int pMaxMipmapLevel, CallbackInfo ci) {
        ModelRedirectUtil.registerEggItemModelRedirects(resourceManager);
    }
}
