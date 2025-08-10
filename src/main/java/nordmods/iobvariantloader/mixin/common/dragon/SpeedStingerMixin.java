package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStinger;
import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawnerUtil;
import nordmods.iobvariantloader.util.extras.ExtrasUtil;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Mixin(SpeedStinger.class)
public abstract class SpeedStingerMixin extends ADragonBaseMixin{
    protected SpeedStingerMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Redirect(method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V",
            at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/dragons/speedstinger/SpeedStinger;getBreedEggResult(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lcom/GACMD/isleofberk/entity/eggs/entity/base/ADragonEggBase;"))
    private ADragonEggBase assignVariant(SpeedStinger instance, ServerLevel world, AgeableMob parent) {
        if (parent instanceof ADragonBase dragonPartner) {
            ADragonEggBase egg = instance.getBreedEggResult(world, dragonPartner);
            if (!IoBVariantLoader.config.assignEggVariantOnBreeding.get()) return egg;

            if (egg instanceof VariantNameHelper helper) {
                if (instance instanceof VariantNameHelper parent1 && dragonPartner instanceof VariantNameHelper parent2) {
                    String parent1Variant = parent1.getVariantName();
                    String parent2Variant = parent2.getVariantName();

                    if (instance.getRandom().nextDouble() < IoBVariantLoader.config.inheritanceChance.get()) {
                        if (instance.getRandom().nextBoolean()) helper.setVariantName(parent1Variant);
                        else helper.setVariantName(parent2Variant);
                    }
                    else DragonVariantSpawnerUtil.assignVariant(world, egg, false, parent1);
                    return egg;
                }
            }
        }
        return null;
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

    @Inject(method = "get1stAttackSound", at = @At("HEAD"), cancellable = true, remap = false)
    public void swapBiteSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.BITE)) cir.setReturnValue(null);
    }

    @Inject(method = "get2ndAttackSound", at = @At("HEAD"), cancellable = true, remap = false)
    public void swapStingSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.STING)) cir.setReturnValue(null);
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
