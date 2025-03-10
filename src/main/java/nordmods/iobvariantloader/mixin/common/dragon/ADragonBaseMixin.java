package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.dragons.gronckle.Gronckle;
import com.GACMD.isleofberk.entity.dragons.stinger.Stinger;
import com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStryke;
import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.eggs.NightLightEgg;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawner;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawnerUtil;
import nordmods.iobvariantloader.util.ducks.*;
import nordmods.iobvariantloader.util.hitbox_redirect.HitboxRedirectUtil;
import nordmods.iobvariantloader.util.model_redirect.ModelRedirectUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ADragonBase.class)
public abstract class ADragonBaseMixin extends TamableAnimal implements VariantNameHelper, DragonModelCacheHelper, DragonSpeciesHelper, HitboxRedirectHelper, ModelSizeProvider, DefaultVariantNameHelper {
    @Shadow public abstract int getDragonVariant();

    @Shadow public abstract boolean isTitanWing();

    @Unique private ResourceLocation modelLocationCache;
    @Unique private ResourceLocation textureLocationCache;
    @Unique private ResourceLocation animationLocationCache;
    @Unique private ResourceLocation saddleTextureLocationCache;
    @Unique private ResourceLocation glowLayerLocationCache;
    @Unique private boolean preventGlowLayer = false;
    @Unique private Component translationName;
    @Unique private EntityDimensions boxOverride;
    @Unique private EntityDimensions attackBoxOverride;
    @Unique private Vec3 attackBoxPos;
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
        else setVariantName(getFromBaseVariant());
    }

    @Inject(method = "defineSynchedData()V", at = @At("TAIL"))
    private void defineVariantName(CallbackInfo ci) {
        entityData.define(VARIANT_NAME, "");
    }

    @Inject(method = "finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;", at = @At("HEAD"))
    private void assignVariantName(ServerLevelAccessor world, DifficultyInstance p_146747_, MobSpawnType p_146748_, SpawnGroupData p_146749_, CompoundTag p_146750_, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (getVariantName().isEmpty()) DragonVariantSpawnerUtil.assignVariant(world, this, true);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_CUSTOM_NAME.equals(key) || VARIANT_NAME.equals(key) || DATA_BABY_ID.equals(key)) {
            if (level.isClientSide()) resetCache();
            resetHitboxData();
        }
    }

    @Override
    public void resetTranslationName() {
        translationName = null;
    }

    //All cache stuff should be called only from clientside... But I'm quite lazy to separate this mess
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

    public boolean shouldPreventGlowLayerRenderer() {
        return preventGlowLayer;
    }
    public void setPreventGlowLayer(boolean state) {
        preventGlowLayer = state;
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
                        DragonVariantSpawner variant1 = DragonVariantSpawnerUtil.getVariantByName(parent1, parent1Variant);
                        DragonVariantSpawner variant2 = DragonVariantSpawnerUtil.getVariantByName(parent2, parent2Variant);

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
                    if (helper.getVariantName().isEmpty()) DragonVariantSpawnerUtil.assignVariant(world, egg, false, parent1);
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
            List<DragonVariantSpawner> variants = DragonVariantSpawnerUtil.getVariantsFor("night_light");
            DragonVariantSpawnerUtil.assignVariantFromList(serverLevelAccessor, egg, false, variants);
        }
        return egg;
    }

    @Override
    protected Component getTypeName() {
        if (level.isClientSide() && !ResourceUtil.isResourceReloadFinished) return super.getTypeName();
        if (translationName == null) {
            if (ModelRedirectUtil.dragonModelRedirects.containsKey(getSpecies(true)) && ModelRedirectUtil.dragonModelRedirects.get(getSpecies(true)).containsKey(getVariantName()))
                translationName = ModelRedirectUtil.dragonModelRedirects.get(getSpecies(true)).get(getVariantName()).dragonName();
            if (translationName == null) translationName = super.getTypeName();
        }
        return translationName;
    }

    @Unique
    public String getSpecies(boolean isClient) {
        String dragonID = EntityType.getKey(getType()).getPath();
        if (isClient) if (dragonID.equals("monstrous_nightmare")) return "nightmare";
        return dragonID;
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        if (boxOverride == null) {
            EntityDimensions original = super.getDimensions(pPose);
            EntityDimensions override = HitboxRedirectUtil.getHitboxOverride((ADragonBase) (Object)this);
            boxOverride = override == null ? original : override.scale(getScale());
        }
        if (getParts() != null && getParts()[0] instanceof AttackBoxRedirectHelper helper) {
            EntityDimensions newBox = getAttackBox();
            if (newBox != getParts()[0].getDimensions(pPose)) {
                helper.setAttackBoxOverride(newBox);
                getParts()[0].refreshDimensions();
            }
        }
        return boxOverride;
    }

    @Override
    public void resetHitboxData() {
        boxOverride = null;
        attackBoxPos = null;
        attackBoxOverride = null;
        refreshDimensions();
    }

    @Override
    public EntityDimensions getAttackBox() {
        if (getVariantName().isEmpty()) return getDefaultAttackBox((ADragonBase) (Object) this);
        if (attackBoxOverride == null) {
            attackBoxOverride = HitboxRedirectUtil.getAttackBoxOverride((ADragonBase) (Object) this);
            if (attackBoxOverride == null) attackBoxOverride = getDefaultAttackBox((ADragonBase) (Object) this);
        }
        return attackBoxOverride;
    }

    @Override
    public Vec3 getAttackBoxPos() {
        if (getVariantName().isEmpty()) return getDefaultAttackBoxPos((ADragonBase) (Object) this);
        if (attackBoxPos == null) {
            attackBoxPos = HitboxRedirectUtil.getAttackBoxPos((ADragonBase) (Object) this);
            if (attackBoxPos == null) attackBoxPos = getDefaultAttackBoxPos((ADragonBase) (Object) this);
        }
        return attackBoxPos;
    }

    private Vec3 getDefaultAttackBoxPos(ADragonBase dragon) {
        if (dragon instanceof Gronckle) return new Vec3(2.2, 0.4, 2.2);
        if (dragon instanceof TripleStryke) return new Vec3(3, 0.4, 3);
        if (dragon instanceof Stinger stinger) return new Vec3(3, stinger.isUsingAbility() ? 0.4 : 2.0, 3);
        return Vec3.ZERO;
    }

    private EntityDimensions getDefaultAttackBox(ADragonBase dragon) {
        if (dragon instanceof Gronckle) return EntityDimensions.scalable(1.6f, 1.6f);
        if (dragon instanceof TripleStryke) return EntityDimensions.scalable(1.8f, 1.8f);
        if (dragon instanceof Stinger) return EntityDimensions.scalable(1.5f, 1.5f);
        return EntityDimensions.scalable(1f, 1f);
    }

    @Override
    public float getModelSize() {
        String species = getSpecies(true);
        if (isBaby()) {
            return switch (species) {
                case "terrible_terror" -> 0.3f;
                default -> 0.4f;
            };
        }
        if (isTitanWing()) {
            return switch (species) {
                case "terrible_terror" -> 1;
                default -> 1.4f;
            };
        }

        return switch (species) {
            case "night_fury" -> 1.1f;
            case "skrill" -> 1.4f;
            case "terrible_terror" -> 0.7f;
            case "triple_stryke" -> 1.3f;
            case "speed_stinger_leader" -> 1.4f;
            default -> 1;
        };
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader levelReader) {
        return 0;
    }
}
