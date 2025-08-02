package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStryke;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.core.manager.AnimationData;

@Mixin(TripleStryke.class)
public abstract class TripleStrykeMixin extends ADragonBaseMixin {

    protected TripleStrykeMixin(EntityType<? extends ADragonBase> animal, Level world) {
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
            case 1 -> "sandr";
            case 2 -> "fart_n_sniff";
            case 3 -> "hodd";
            case 4 -> "hjarta";
            case 5 -> "exiled";
            case 6 -> "whip_n_lash";
            case 7 -> "leaf_n_bark";
            case 8 -> "purple_n_nurple";
            case 9 -> "hamfeist";
            case 10 -> "kandy_n_kane";
            default -> "pistill";
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

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/dragons/triple_stryke/TripleStryke;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private void swapStingSound(TripleStryke instance, SoundEvent soundEvent, float a, float b, Operation<Void> original) {
        if (!SoundRedirectUtil.playSound(this, SoundRedirectUtil.STING))
            original.call(instance, soundEvent, a, b);
    }

    @WrapOperation(method = "playAttackSound", at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/dragons/triple_stryke/TripleStryke;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private void swapMeleeAttackSound(TripleStryke instance, SoundEvent soundEvent, float a, float b, Operation<Void> original) {
        if (soundEvent != SoundEvents.SHEEP_SHEAR || !SoundRedirectUtil.playSound(this, SoundRedirectUtil.MELEE_ATTACK))
            original.call(instance, soundEvent, a, b);
    }

    @Inject(method = "registerControllers", at = @At("TAIL"), remap = false)
    private void registerSoundController(AnimationData data, CallbackInfo ci) {
        data.getAnimationControllers().forEach((name, contr) -> contr.registerSoundListener(
                event -> SoundRedirectUtil.playSound(this, event.sound)));
    }
}
