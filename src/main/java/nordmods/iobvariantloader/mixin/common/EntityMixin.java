package nordmods.iobvariantloader.mixin.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import nordmods.iobvariantloader.util.ModelCacheHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "setCustomName(Lnet/minecraft/network/chat/Component;)V", at = @At("TAIL"))
    private void updateCache(Component pName, CallbackInfo ci) {
        if (this instanceof ModelCacheHelper helper) {
            helper.setTextureLocationCache(null);
            helper.setAnimationLocationCache(null);
            helper.setModelLocationCache(null);
        }
    }
}
