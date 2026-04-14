package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.dragons.gronckle.Gronckle;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nordmods.iobvariantloader.util.extras.ExtrasUtil;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Mixin(Gronckle.class)
public abstract class GronckleMixin extends ADragonRideableUtilityMixin {
    protected GronckleMixin(EntityType<? extends ADragonBase> animal, Level world) {
        super(animal, world);
    }

    @ModifyVariable(method = "tickPart", at = @At("HEAD"), ordinal = 0, argsOnly = true, remap = false)
    private double adjustBox1(double value) {
        return getAttackBoxPos().x * Mth.sin(-getYRot() * Mth.DEG_TO_RAD);
    }

    @ModifyVariable(method = "tickPart", at = @At("HEAD"), ordinal = 1, argsOnly = true, remap = false)
    private double adjustBox2(double value) {
        return getAttackBoxPos().y;
    }

    @ModifyVariable(method = "tickPart", at = @At("HEAD"), ordinal = 2, argsOnly = true, remap = false)
    private double adjustBox3(double value) {
        return getAttackBoxPos().z * Mth.cos(getYRot() * Mth.DEG_TO_RAD);
    }


    @Override
    public String getFromBaseVariant() {
        return switch (getDragonVariant()) {
            case 1 -> "hjarta";
            case 2 -> "junior_tuffnut_junior";
            case 3 -> "gronckle";
            case 4 -> "cheesemonger";
            case 5 -> "exiled";
            case 6 -> "rubblegrubber";
            case 7 -> "barn";
            case 8 -> "crubble";
            case 9 -> "yawnckle";
            default -> "meatlug";
        };
    }

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    public void swapDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.DEATH)) cir.setReturnValue(null);
    }

    @Inject(method = "getTameSound", at = @At("HEAD"), cancellable = true, remap = false)
    public void swapTameSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.TAME)) cir.setReturnValue(null);
    }

    @Inject(method = "get1stAttackSound", at = @At("HEAD"), cancellable = true, remap = false)
    public void swapBiteSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.BITE)) cir.setReturnValue(null);
    }

    @Inject(method = "getProjectileSound", at = @At("HEAD"), cancellable = true, remap = false)
    public void swapFireSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.FIRE)) cir.setReturnValue(null);
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

    @Inject(method = "isBreedingFood", at = @At("HEAD"), cancellable = true, remap = false)
    private void getBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Boolean isCorrect = ExtrasUtil.isBreedingItem(getSpecies(false), getVariantName(), stack);
        if (isCorrect != null) cir.setReturnValue(isCorrect);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void checkCustomItemInteraction(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> cir) {
        checkCustomItemInteractions(pPlayer, pHand, cir);
    }
}
