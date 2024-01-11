package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import nordmods.iobvariantloader.util.ModelCacheHelper;
import nordmods.iobvariantloader.util.VariantNameHelper;
import nordmods.iobvariantloader.util.dragonVariant.DragonVariantUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

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
        DragonVariantUtil.assignVariant(world, this);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        setTextureLocationCache(null);
        setAnimationLocationCache(null);
        setModelLocationCache(null);
    }

    //All below should be called only from clientside. Would be glad to write it on model class, but it doesn't work well
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
}
