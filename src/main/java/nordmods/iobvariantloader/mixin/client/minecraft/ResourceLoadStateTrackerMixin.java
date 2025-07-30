package nordmods.iobvariantloader.mixin.client.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ResourceLoadStateTracker;
import net.minecraft.server.packs.PackResources;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.ducks.DragonModelCacheHelper;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ResourceLoadStateTracker.class)
public abstract class ResourceLoadStateTrackerMixin {
    @Shadow @Nullable private ResourceLoadStateTracker.ReloadState reloadState;

    @Inject(method = "startReload(Lnet/minecraft/client/ResourceLoadStateTracker$ReloadReason;Ljava/util/List;)V", at = @At("TAIL"))
    private void updateStatusOnStart(ResourceLoadStateTracker.ReloadReason pReloadReason, List<PackResources> pPacks, CallbackInfo ci) {
        if (reloadState != null) ResourceUtil.isResourceReloadFinished = reloadState.finished;
        if (Minecraft.getInstance().level != null) {
            Minecraft.getInstance().level.entitiesForRendering().forEach(entity -> {
                if (entity instanceof DragonModelCacheHelper cache) cache.resetCache();
            });
        }
        SoundRedirectUtil.clearCahce();
    }

    @Inject(method = "finishReload()V", at = @At("TAIL"))
    private void updateStatusOnFinish(CallbackInfo ci) {
        if (reloadState != null) ResourceUtil.isResourceReloadFinished = reloadState.finished;
    }

}
