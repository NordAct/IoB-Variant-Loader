package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nordmods.iobvariantloader.util.ducks.DeadlyNadderModelCacheHelper;
import nordmods.iobvariantloader.util.extras.ExtrasUtil;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Mixin(DeadlyNadder.class)
public abstract class DeadlyNadderMixin extends ADragonBaseMixin implements DeadlyNadderModelCacheHelper {
    @Unique
    private ResourceLocation wingGlowLayerLocationCache;
    @Unique private ResourceLocation wingLayerLocationCache;
    @Unique private boolean preventWingGlowLayer = false;

    protected DeadlyNadderMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public ResourceLocation getWingGlowLayerLocationCache() {
        return wingGlowLayerLocationCache;
    }

    @Override
    public ResourceLocation getWingLayerLocationCache() {
        return wingLayerLocationCache;
    }

    @Override
    public void setWingGlowLayerLocationCache(ResourceLocation state) {
        wingGlowLayerLocationCache = state;
    }

    @Override
    public void setWingLayerLocationCache(ResourceLocation state) {
        wingLayerLocationCache = state;
    }

    @Override
    public boolean shouldPreventWingGlowLayerRenderer() {
        return preventWingGlowLayer;
    }

    @Override
    public void setPreventWingGlowLayer(boolean state) {
        preventWingGlowLayer = state;
    }

    @Override
    public String getFromBaseVariant() {
        return switch (getDragonVariant()) {
            case 1 -> "deadly_nadder";
            case 2 -> "kingstail";
            case 3 -> "scardian";
            case 4 -> "springshedder";
            case 5 -> "hjarta";
            case 6 -> "bork_week";
            case 7 -> "flystorm";
            case 8 -> "hjaldr";
            case 9 -> "barklethorn";
            case 10 -> "lethal_lancebeak";
            case 11 -> "seedling_stormpest";
            default -> "stormfly";
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

    @WrapOperation(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/dragons/deadlynadder/DeadlyNadder;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private void swapStingSound(DeadlyNadder instance, SoundEvent soundEvent, float a, float b, Operation<Void> original) {
        if (!SoundRedirectUtil.playSound(this, SoundRedirectUtil.STING))
            original.call(instance, soundEvent, a, b);
    }

    @WrapOperation(method = "performRangedAttackAI", at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/dragons/deadlynadder/DeadlyNadder;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private void swapStingSoundAI(DeadlyNadder instance, SoundEvent soundEvent, float a, float b, Operation<Void> original) {
        if (!SoundRedirectUtil.playSound(this, SoundRedirectUtil.STING))
            original.call(instance, soundEvent, a, b);
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
}
