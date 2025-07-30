package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBaseFlyingRideable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ADragonBaseFlyingRideable.class)
public abstract class ADragonBaseFlyingRideableMixin extends ADragonRideableUtilityMixin {
    @Shadow public abstract boolean isFlying();

    protected ADragonBaseFlyingRideableMixin(EntityType<? extends ADragonBase> animal, Level world) {
        super(animal, world);
    }

    //*звуки тихого ахуя*
    @Override
    public void playAmbientSound() {
        if (!isFlying()) super.playAmbientSound();
    }

    @Inject(method = "onFlap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V", shift = At.Shift.BEFORE), cancellable = true)
    private void cancelFlapping(CallbackInfo ci) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.FLAP)) ci.cancel();
    }
}
