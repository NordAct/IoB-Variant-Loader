package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.eggs.NightLightEgg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.DeadlyNadderModelCacheHelper;
import nordmods.iobvariantloader.util.ModelCacheHelper;
import nordmods.iobvariantloader.util.VariantNameHelper;
import nordmods.iobvariantloader.util.dragon_variant.DragonVariant;
import nordmods.iobvariantloader.util.dragon_variant.DragonVariantUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ADragonBase.class)
public abstract class ADragonBaseMixin extends TamableAnimal implements VariantNameHelper, ModelCacheHelper {
    @Unique private ResourceLocation modelLocationCache;
    @Unique private ResourceLocation textureLocationCache;
    @Unique private ResourceLocation animationLocationCache;
    @Unique private ResourceLocation saddleTextureLocationCache;
    @Unique private ResourceLocation glowLayerLocationCache;
    protected ADragonBaseMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @SuppressWarnings("WrongEntityDataParameterClass")
    @Unique
    private static final EntityDataAccessor<String> VARIANT_NAME = SynchedEntityData.defineId(ADragonBase.class, EntityDataSerializers.STRING);

    @Unique
    public String getVariantName() {
        return entityData.get(VARIANT_NAME);
    }

    @Unique
    public void setVariantName(String variantName) {
        entityData.set(VARIANT_NAME, variantName);
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void saveVariantName(CompoundTag nbt, CallbackInfo ci) {
        nbt.putString("VariantName", getVariantName());
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void readVariantName(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("VariantName")) setVariantName(nbt.getString("VariantName"));
    }

    @Inject(method = "defineSynchedData()V", at = @At("TAIL"))
    private void defineVariantName(CallbackInfo ci) {
        entityData.define(VARIANT_NAME, "");
    }

    @Inject(method = "finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;", at = @At("HEAD"))
    private void assignVariantName(ServerLevelAccessor world, DifficultyInstance p_146747_, MobSpawnType p_146748_, SpawnGroupData p_146749_, CompoundTag p_146750_, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (getVariantName().isEmpty()) DragonVariantUtil.assignVariant(world, this, true);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_CUSTOM_NAME.equals(key) || VARIANT_NAME.equals(key)) {
            setTextureLocationCache(null);
            setAnimationLocationCache(null);
            setModelLocationCache(null);
            setSaddleTextureLocationCache(null);
            setGlowLayerLocationCache(null);
            if (this instanceof DeadlyNadderModelCacheHelper helper) {
                helper.setWingGlowLayerLocationCache(null);
                helper.setWingLayerLocationCache(null);
            }
        }
    }

    //All cache stuff should be called only from clientside. Would be glad to write it on model class, but it doesn't work well
    public ResourceLocation getModelLocationCache() {
        return modelLocationCache;
    }
    public ResourceLocation getAnimationLocationCache() {
        return animationLocationCache;
    }
    public ResourceLocation getTextureLocationCache() {
        return textureLocationCache;
    }
    public ResourceLocation getSaddleTextureLocationCache() {
        return saddleTextureLocationCache;
    }
    public ResourceLocation getGlowLayerLocationCache() {
        return glowLayerLocationCache;
    }
    public void setModelLocationCache(ResourceLocation state) {
        modelLocationCache = state;
    }
    public void setAnimationLocationCache(ResourceLocation state) {
        animationLocationCache = state;
    }
    public void setTextureLocationCache(ResourceLocation state) {
        textureLocationCache = state;
    }
    public void setSaddleTextureLocationCache(ResourceLocation state) {
        saddleTextureLocationCache = state;
    }
    public void setGlowLayerLocationCache(ResourceLocation state) {
        glowLayerLocationCache = state;
    }

    @Redirect(method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V",
            at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/base/dragon/ADragonBase;getBreedEggResult(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lcom/GACMD/isleofberk/entity/eggs/entity/base/ADragonEggBase;"),
            remap = false)
    private ADragonEggBase assignVariant(ADragonBase instance, ServerLevel world, AgeableMob parent) {
        if (parent instanceof ADragonBase dragonPartner) {
            ADragonEggBase egg = instance.getBreedEggResult(world, dragonPartner);
            if (!IoBVariantLoader.config.assignEggVariantOnBreeding.get()) return egg;

            if (egg instanceof VariantNameHelper helper) {
                if (instance instanceof VariantNameHelper parent1 && dragonPartner instanceof VariantNameHelper parent2) {
                    String parent1Variant = parent1.getVariantName();
                    String parent2Variant = parent2.getVariantName();

                    if (instance.getRandom().nextDouble() < IoBVariantLoader.config.inheritanceChance.get()) {
                        DragonVariant variant1 = DragonVariantUtil.getVariantByName(parent1, parent1Variant);
                        DragonVariant variant2 = DragonVariantUtil.getVariantByName(parent2, parent2Variant);

                        if (variant1 != null && variant2 != null) {
                            int weight1 = variant1.breedingWeight();
                            int weight2 = variant2.breedingWeight();
                            if (weight1 > 0 || weight2 > 0) {
                                if (weight1 <= 0) helper.setVariantName(parent2Variant);
                                else if (weight2 <= 0) helper.setVariantName(parent1Variant);
                                else if (getRandom().nextInt(weight1 + weight2) < weight1) helper.setVariantName(parent1Variant);
                                else helper.setVariantName(parent2Variant);
                            }
                        }

                        if (variant1 == null && variant2 != null) helper.setVariantName(parent2Variant);
                        else if (variant2 == null && variant1 != null) helper.setVariantName(parent1Variant);
                    }
                    if (helper.getVariantName().isEmpty()) DragonVariantUtil.assignVariant(world, egg, false, parent1);
                    return egg;
                }
            }
        }
        return null;
    }

    @ModifyArg(method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private Entity assignNightLightVariant(Entity egg) {
        if (!IoBVariantLoader.config.assignEggVariantOnBreeding.get()) return egg;

        if (egg instanceof VariantNameHelper helper && helper.getVariantName().isEmpty() && egg instanceof NightLightEgg && level instanceof ServerLevelAccessor serverLevelAccessor) {
            List<DragonVariant> variants = DragonVariantUtil.getVariantsFor("night_light");
            DragonVariantUtil.assignVariantFromList(serverLevelAccessor, egg, false, variants);
        }
        return egg;
    }
}
