package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import com.GACMD.isleofberk.items.DragonEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.VariantNameHelper;
import nordmods.iobvariantloader.util.dragon_variant.DragonVariant;
import nordmods.iobvariantloader.util.dragon_variant.DragonVariantUtil;
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
public abstract class ADragonEggBaseMixin extends AgeableMob implements VariantNameHelper {
    @Shadow protected abstract DragonEggItem getItemVersion();

    @Shadow public abstract ResourceLocation getTextureLocation(ADragonEggBase dragonBase);

    @Shadow public abstract Block getBlockParticle();

    @Shadow protected abstract int getHatchTime();

    @Shadow public abstract void setCanHatch(boolean canHatch);

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
            if (getVariantName().isEmpty()) DragonVariantUtil.assignVariant(serverLevelAccessor, entity, false);
            else ((VariantNameHelper)entity).setVariantName(getVariantName());
        }
        return world.addFreshEntity(entity);
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        if (!this.isRemoved() && !level.isClientSide()) {
            DragonEggItem item = getItemVersion();
            ItemStack itemStack = new ItemStack(item);
            if (!getVariantName().isEmpty()) itemStack.addTagElement("VariantName", StringTag.valueOf(getVariantName()));
            ItemEntity itemEntity = new ItemEntity(level, getX(), getY(), getZ(), itemStack);
            level.addFreshEntity(itemEntity);
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
            List<DragonVariant> variants = DragonVariantUtil.getVariantsFor(getSpecies());
            DragonVariantUtil.assignVariantFromList(pLevel, this, false, variants);
        }

        return pSpawnData;
    }

    @Unique
    private String getSpecies() {
        ResourceLocation resourcelocation = EntityType.getKey(getType());
        String dragonID = resourcelocation.getPath().replace("_egg", "");
        //this inconsistency in names just kills me
        return switch (dragonID) {
            default -> dragonID;
            case "m_nightmare" -> "monstrous_nightmare";
            case "nadder" -> "deadly_nadder";
        };
    }

    @Override
    public ItemStack getPickResult() {
        ItemStack itemStack = new ItemStack(getItemVersion());
        if (!getVariantName().isEmpty()) itemStack.addTagElement("VariantName", StringTag.valueOf(getVariantName()));
        return itemStack;
    }
}
