package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.eggs.NightLightEgg;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ModelCacheHelper;
import nordmods.iobvariantloader.util.VariantNameHelper;
import nordmods.iobvariantloader.util.dragonVariant.DragonVariant;
import nordmods.iobvariantloader.util.dragonVariant.DragonVariantUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ADragonBase.class)
public abstract class ADragonBaseMixin extends LivingEntity implements VariantNameHelper, ModelCacheHelper {
    @Unique private ResourceLocation modelLocationCache;
    @Unique private ResourceLocation textureLocationCache;
    @Unique private ResourceLocation animationLocationCache;
    protected ADragonBaseMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Unique
    private static final EntityDataAccessor<String> VARIANT_NAME = SynchedEntityData.defineId(ADragonBase.class, EntityDataSerializers.STRING);

    @Unique
    public String getVariantName() {
        return entityData.get(VARIANT_NAME);
    }

    @Unique
    public void setVariantName(String variantName) {
        entityData.set(VARIANT_NAME, variantName);
        setTextureLocationCache(null);
        setAnimationLocationCache(null);
        setModelLocationCache(null);
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
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        setTextureLocationCache(null);
        setAnimationLocationCache(null);
        setModelLocationCache(null);
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
    public void setModelLocationCache(ResourceLocation state) {
        modelLocationCache = state;
    }
    public void setAnimationLocationCache(ResourceLocation state) {
        animationLocationCache = state;
    }
    public void setTextureLocationCache(ResourceLocation state) {
        textureLocationCache = state;
    }

    @Redirect(method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V",
            at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/base/dragon/ADragonBase;getBreedEggResult(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lcom/GACMD/isleofberk/entity/eggs/entity/base/ADragonEggBase;"))
    private ADragonEggBase assignVariant(ADragonBase instance, ServerLevel world, AgeableMob parent) {
        if (parent instanceof ADragonBase dragonPartner) {
            ADragonEggBase egg = instance.getBreedEggResult(world, dragonPartner);
            if (egg instanceof VariantNameHelper helper) {
                if (instance instanceof VariantNameHelper parent1 && dragonPartner instanceof VariantNameHelper parent2) {
                    String parent1Variant = parent1.getVariantName();
                    String parent2Variant = parent2.getVariantName();

                    if (instance.getRandom().nextDouble() < IoBVariantLoader.config.inheritanceChance.get()) {
                        if (instance.getRandom().nextBoolean()) helper.setVariantName(parent1Variant);
                        else helper.setVariantName(parent2Variant);
                    }
                    else DragonVariantUtil.assignVariant(world, egg, false, parent1);
                    return egg;
                }
            }
        }
        return null;
    }

    @Inject(method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V",
            at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/eggs/entity/base/ADragonEggBase;setBaby(Z)V"))
    private void assignNightLightVariant(ServerLevel world, Animal partner, CallbackInfo ci, @Local LocalRef<ADragonEggBase> localRef){
        if (localRef.get() instanceof NightLightEgg egg
                && (getType().getRegistryName().equals(new ResourceLocation("isleofberk", "night_fury")) && partner.getType().getRegistryName().equals(new ResourceLocation("isleofberk", "light_fury"))
                    || getType().getRegistryName().equals(new ResourceLocation("isleofberk", "light_fury")) && partner.getType().getRegistryName().equals(new ResourceLocation("isleofberk", "night_fury")))) {

            List<DragonVariant> variants = DragonVariantUtil.getVariantsFor("night_light");
            if (variants != null) {
                int totalWeight = 0;
                for (DragonVariant variant : variants) {
                    //banned biomes check (blacklist)
                    if (variant.hasBannedBiomes() && DragonVariantUtil.isVariantIn(variant.bannedBiomes(), world, partner.blockPosition())) continue;
                    if (variant.altitudeRestriction().min() > partner.blockPosition().getY() || partner.blockPosition().getY() > variant.altitudeRestriction().max()) continue;

                    //allowed biomes check (whitelist)
                    if (variant.hasAllowedBiomes()) {
                        if (DragonVariantUtil.isVariantIn(variant.allowedBiomes(), world, partner.blockPosition())) totalWeight += variant.breedingWeight();
                    } else totalWeight += variant.breedingWeight();
                }

                if (totalWeight <= 0)
                    throw new RuntimeException("Failed to assign dragon variant due impossible total weight of all variants for " + egg);

                int roll = partner.getRandom().nextInt(totalWeight);
                int previousBound = 0;

                for (DragonVariant variant : variants) {
                    //banned biomes check (blacklist)
                    if (variant.hasBannedBiomes() && DragonVariantUtil.isVariantIn(variant.bannedBiomes(), world, partner.blockPosition())) continue;
                    if (variant.altitudeRestriction().min() > partner.blockPosition().getY() || partner.blockPosition().getY() > variant.altitudeRestriction().max()) continue;

                    //allowed biomes check (whitelist)
                    if (variant.hasAllowedBiomes()) {
                        if (DragonVariantUtil.isVariantIn(variant.allowedBiomes(), world, partner.blockPosition())) {
                            if (roll >= previousBound && roll < previousBound + variant.breedingWeight()) {
                                ((VariantNameHelper)egg).setVariantName(variant.name());
                                break;
                            }
                            previousBound += variant.breedingWeight();
                        }
                    } else {
                        if (roll >= previousBound && roll < previousBound + variant.breedingWeight()) {
                            ((VariantNameHelper)egg).setVariantName(variant.name());
                            break;
                        }
                        previousBound += variant.breedingWeight();
                    }
                }
            }
            localRef.set(egg);
        }
    }

}
