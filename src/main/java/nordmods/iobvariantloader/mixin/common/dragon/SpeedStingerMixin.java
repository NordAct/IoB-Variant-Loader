package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.AI.breed.DragonBreedGoal;
import com.GACMD.isleofberk.entity.AI.goal.FollowOwnerNoTPGoal;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStinger;
import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.eggs.*;
import com.GACMD.isleofberk.registery.ModEntities;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.VLDragonBreedGoal;
import nordmods.iobvariantloader.util.alt_navigation.AltFollowGoal;
import nordmods.iobvariantloader.util.breeding_list.BreedingListUtil;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawnerUtil;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;
import nordmods.iobvariantloader.util.extras.ExtrasUtil;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Mixin(SpeedStinger.class)
public abstract class SpeedStingerMixin extends ADragonRideableUtilityMixin{


    protected SpeedStingerMixin(EntityType<? extends ADragonBase> animal, Level world) {
        super(animal, world);
    }

    @Redirect(method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V",
            at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/dragons/speedstinger/SpeedStinger;getBreedEggResult(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lcom/GACMD/isleofberk/entity/eggs/entity/base/ADragonEggBase;"))
    private ADragonEggBase assignVariant(SpeedStinger instance, ServerLevel world, AgeableMob parent) {
        if (parent instanceof ADragonBase dragonPartner) {

            ADragonEggBase egg = instance.getBreedEggResult(world, dragonPartner);
            if (!IoBVariantLoader.config.assignEggVariantOnBreeding.get()) return egg;

            if (IoBVariantLoader.config.breedingListsUse.get().canUseBreedingLists()) {
                Pair<String, String> dragonAndVariant = BreedingListUtil.getRandomDragonAndVariant(instance, dragonPartner);
                if (dragonAndVariant != null) {
                    ADragonEggBase egg1 = switch (dragonAndVariant.getFirst()) {
                        case "deadly_nadder" -> new DeadlyNadderEgg(ModEntities.NADDER_EGG.get(), world);
                        case "gronckle" -> new GronkleEgg(ModEntities.GRONCKLE_EGG.get(), world);
                        case "light_fury" -> new LightFuryEgg(ModEntities.LIGHT_FURY_EGG.get(), world);
                        case "monstrous_nightmare" -> new MonstrousNightmareEgg(ModEntities.M_NIGHTMARE_EGG.get(), world);
                        case "night_fury" -> new NightFuryEgg(ModEntities.NIGHT_FURY_EGG.get(), world);
                        case "night_light" -> new NightLightEgg(ModEntities.NIGHT_LIGHT_EGG.get(), world);
                        case "skrill" -> new SkrillEgg(ModEntities.SKRILL_EGG.get(), world);
                        case "speed_stinger", "speed_stinger_leader" -> new SpeedStingerEgg(ModEntities.SPEED_STINGER_EGG.get(), world);
                        case "stinger" -> new StingerEgg(ModEntities.STINGER_EGG.get(), world);
                        case "terrible_terror" -> new TerribleTerrorEgg(ModEntities.TERRIBLE_TERROR_EGG.get(), world);
                        case "triple_stryke" -> new TripleStrykeEgg(ModEntities.TRIPLE_STRYKE_EGG.get(), world);
                        case "zippleback" -> new ZippleBackEgg(ModEntities.ZIPPLEBACK_EGG.get(), world);
                        default -> null;
                    };
                    if (egg1 instanceof VariantNameHelper helper) {
                        helper.setVariantName(dragonAndVariant.getSecond());
                        return egg1;
                    } else if (!IoBVariantLoader.config.breedingListsUse.get().canUseFallback()) return null;
                } else if (!IoBVariantLoader.config.breedingListsUse.get().canUseFallback()) return null;
            }

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

    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V"), index = 1)
    private Goal replaceBreedingGoal(Goal pGoal) {
        if (IoBVariantLoader.config.breedingListsUse.get().canUseBreedingLists() && pGoal instanceof DragonBreedGoal)
            return new VLDragonBreedGoal((ADragonBase)(Object)this, 1);
        return pGoal;
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void checkCustomItemInteraction(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> cir) {
        checkCustomItemInteractions(pPlayer, pHand, cir);
    }

    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V"), index = 1)
    private Goal replaceFollowGoal(Goal pGoal) {
        if (IoBVariantLoader.config.alternativeLandNavigation.get() && pGoal instanceof FollowOwnerNoTPGoal)
            return new AltFollowGoal((ADragonBase)(Object)this, 1.1, 3.0F, 3.0F, false);
        return pGoal;
    }
}
