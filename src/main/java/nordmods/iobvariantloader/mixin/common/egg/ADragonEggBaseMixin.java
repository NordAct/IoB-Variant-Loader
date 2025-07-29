package nordmods.iobvariantloader.mixin.common.egg;

import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import com.GACMD.isleofberk.items.DragonEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.ducks.ModelCacheHelper;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawner;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawnerUtil;
import nordmods.iobvariantloader.util.model_redirect.ModelRedirectUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ADragonEggBase.class)
public abstract class ADragonEggBaseMixin extends AgeableMob implements VariantNameHelper, ModelCacheHelper, DragonSpeciesHelper {
    @Shadow protected abstract DragonEggItem getItemVersion();

    @Shadow public abstract ResourceLocation getTextureLocation(ADragonEggBase dragonBase);

    @Shadow public abstract Block getBlockParticle();

    @Shadow protected abstract int getHatchTime();

    @Shadow public abstract void setCanHatch(boolean canHatch);

    @Unique private ResourceLocation modelLocationCache;
    @Unique private ResourceLocation textureLocationCache;
    @Unique private ResourceLocation glowLayerLocationCache;
    @Unique private boolean preventGlowLayer = false;
    @Unique private Component translationName;

    @SuppressWarnings("WrongEntityDataParameterClass")
    @Unique
    private static final EntityDataAccessor<String> VARIANT_NAME = SynchedEntityData.defineId(ADragonEggBase.class, EntityDataSerializers.STRING);

    protected ADragonEggBaseMixin(EntityType<? extends  AgeableMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

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

    @Redirect(method = "hatch()V", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean assignEggVariant(Level world, Entity entity) {
        if (world instanceof ServerLevelAccessor serverLevelAccessor) {
            if (getVariantName().isEmpty()) DragonVariantSpawnerUtil.assignVariant(serverLevelAccessor, entity, false);
            else ((VariantNameHelper)entity).setVariantName(getVariantName());
        }
        return world.addFreshEntity(entity);
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        if (!this.isRemoved() && !level.isClientSide()) {
            if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                DragonEggItem item = getItemVersion();
                ItemStack itemStack = new ItemStack(item);
                if (!getVariantName().isEmpty())
                    itemStack.addTagElement("VariantName", StringTag.valueOf(getVariantName()));
                ItemEntity itemEntity = new ItemEntity(level, getX(), getY(), getZ(), itemStack);
                level.addFreshEntity(itemEntity);
            }
            this.discard();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        setCanHatch(pReason != MobSpawnType.STRUCTURE);
        if (getVariantName().isEmpty() && IoBVariantLoader.config.assignEggVariantOnPlaced.get()) {
            List<DragonVariantSpawner> variants = DragonVariantSpawnerUtil.getVariantsFor(getSpecies(false));
            DragonVariantSpawnerUtil.assignVariantFromList(pLevel, this, false, variants);
        }

        return pSpawnData;
    }

    @Unique
    public String getSpecies(boolean isClient) {
        ResourceLocation resourcelocation = EntityType.getKey(getType());
        String dragonID = resourcelocation.getPath().replace("_egg", "");
        //this inconsistency in names just kills me
        return switch (dragonID) {
            default -> dragonID;
            case "m_nightmare" -> isClient ? "nightmare" : "monstrous_nightmare";
            case "nadder" -> "deadly_nadder";
        };
    }

    @Override
    public ItemStack getPickResult() {
        ItemStack itemStack = new ItemStack(getItemVersion());
        if (!getVariantName().isEmpty()) itemStack.addTagElement("VariantName", StringTag.valueOf(getVariantName()));
        return itemStack;
    }

    public ResourceLocation getModelLocationCache() {
        return modelLocationCache;
    }
    public ResourceLocation getTextureLocationCache() {
        return textureLocationCache;
    }
    public ResourceLocation getGlowLayerLocationCache() {
        return glowLayerLocationCache;
    }
    public void setModelLocationCache(ResourceLocation state) {
        modelLocationCache = state;
    }
    public void setTextureLocationCache(ResourceLocation state) {
        textureLocationCache = state;
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

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level.isClientSide() && VARIANT_NAME.equals(key)) resetCache();
    }

    @Override
    public void resetTranslationName() {
        translationName = null;
    }

    @Override
    protected Component getTypeName() {
        if (translationName == null) {
            String key = null;
            if (ModelRedirectUtil.dragonModelRedirects.containsKey(getSpecies(true)) && ModelRedirectUtil.dragonModelRedirects.get(getSpecies(true)).containsKey(getVariantName()))
                key = ModelRedirectUtil.dragonModelRedirects.get(getSpecies(true)).get(getVariantName()).eggName().orElse(null);
            if (key == null) translationName = getDefaultTypeName();
            else translationName = new TranslatableComponent(key);
        }
        return translationName;
    }

    //because apparently whoever coded this bs was setting item translatable component via custom name because there's no fucking translations for actual entity names...
    //if it was RPG, this guy deserved to be banished from modded MC community twice as more
    protected abstract Component getDefaultTypeName();
}
