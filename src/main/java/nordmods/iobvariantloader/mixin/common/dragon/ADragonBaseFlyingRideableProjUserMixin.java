package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBaseFlyingRideableProjUser;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ADragonBaseFlyingRideableProjUser.class)
public abstract class ADragonBaseFlyingRideableProjUserMixin extends ADragonBaseFlyingRideableMixin {
    protected ADragonBaseFlyingRideableProjUserMixin(EntityType<? extends ADragonBase> animal, Level world) {
        super(animal, world);
    }

    //at this point my ass is hardcoded to be new version of Oreshnik
    @WrapOperation(
            method = "playerFireProjectile",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/GACMD/isleofberk/entity/base/dragon/ADragonBaseFlyingRideableProjUser;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"
            )
    )
    private void swapWeakFireSound(ADragonBaseFlyingRideableProjUser instance, SoundEvent soundEvent, float a, float b, Operation<Void> original) {
        if (!SoundRedirectUtil.playSound(this, SoundRedirectUtil.FIRE_WEAK))
            original.call(instance, soundEvent, a, b);
    }
}
