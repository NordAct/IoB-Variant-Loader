package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.dragons.nightlight.NightLight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.core.manager.AnimationData;

@Mixin(NightLight.class)
public abstract class NightLightMixin extends ADragonBaseMixin{

    protected NightLightMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public String getFromBaseVariant() {
        return switch (getDragonVariant()) {
            case 1 -> "pouncer";
            case 2 -> "ruffrunner";
            case 3 -> "leopard";
            case 4 -> "dusk";
            case 5 -> "appaloosa";
            case 6 -> "skydancer";
            case 7 -> "nightdancer";
            case 8 -> "iridescence";
            case 9 -> "ashwing";
            case 11 -> "harlequeen";
            default -> "dart";
        };
    }

    @Inject(method = "registerControllers", at = @At("TAIL"), remap = false)
    private void registerSoundController(AnimationData data, CallbackInfo ci) {
        data.getAnimationControllers().forEach((name, contr) -> contr.registerSoundListener(event -> SoundRedirectUtil.playSound(this, event.sound)));
    }
}
