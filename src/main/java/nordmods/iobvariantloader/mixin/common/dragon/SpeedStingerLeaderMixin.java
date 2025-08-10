package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.dragons.speedstingerleader.SpeedStingerLeader;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nordmods.iobvariantloader.util.extras.ExtrasUtil;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Mixin(SpeedStingerLeader.class)
public abstract class SpeedStingerLeaderMixin extends ADragonBaseMixin{
    protected SpeedStingerLeaderMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public String getFromBaseVariant() {
        return switch (getDragonVariant()) {
            case 1 -> "floutscout";
            case 2 -> "ice_breaker";
            case 3 -> "sweet_sting";
            default -> "speed_stinger";
        };
    }

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    public void swapDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.DEATH)) cir.setReturnValue(null);
    }

    @Inject(method = "registerControllers", at = @At("TAIL"), remap = false)
    private void registerSoundController(AnimationData data, CallbackInfo ci){
        //I have no idea how, I don't know why and don't wish to know how
        //but SOMEHOW IT CANNOT FIND SAME METHOD IN THE SAME CLASS in different versions of Geckolib
        //unless I specifically compile against it
        try {
            Class<?> clazz = data.getClass();
            String methodName = "getAnimationControllers";
            Method method = clazz.getMethod(methodName);
            Map<String, AnimationController> result = (Map<String, AnimationController>) method.invoke(data);
            result.forEach((name, contr) -> contr.registerSoundListener(event -> SoundRedirectUtil.playSound(this, event.sound)));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(method = "isItemStackForTaming", at = @At("HEAD"), cancellable = true, remap = false)
    private void getTamingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Boolean isCorrect = ExtrasUtil.isTamingItem(getSpecies(false), getVariantName(), stack);
        if (isCorrect != null) cir.setReturnValue(isCorrect);
    }
}
