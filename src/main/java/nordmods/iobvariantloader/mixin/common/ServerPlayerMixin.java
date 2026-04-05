package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import nordmods.iobvariantloader.IoBVariantLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "awardKillScore", at = @At("TAIL"))
    private void triggerKilledVariant(Entity pKilled, int pScoreValue, DamageSource pDamageSource, CallbackInfo ci) {
        if (pKilled instanceof ADragonBase dragon)
            IoBVariantLoader.KILL_VARIANT_FROM_GROUP_TRIGGER.trigger((ServerPlayer) (Object)this, dragon, pDamageSource);
    }
}
