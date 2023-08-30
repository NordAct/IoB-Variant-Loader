package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import nordmods.iobvariantloader.util.DragonVariant;
import nordmods.iobvariantloader.util.DragonVariantUtil;
import nordmods.iobvariantloader.util.VariantNameHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ADragonBase.class)
public abstract class ADragonBaseMixin extends Entity implements VariantNameHelper {
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

    protected ADragonBaseMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
        ResourceLocation resourcelocation = EntityType.getKey(getType());
        List<DragonVariant> variants = DragonVariantUtil.getVariants(resourcelocation.getPath());
        if (variants == null) return;

        int totalWeight = 0;
        for (DragonVariant variant : variants) {
            //banned biomes check (blacklist)
            if (variant.hasBannedBiomes() && isVariantIn(variant.bannedBiomes(), world)) continue;
            if (variant.altitudeRestriction().min() > blockPosition().getY() || blockPosition().getY() > variant.altitudeRestriction().max()) continue;
            //allowed biomes check (whitelist)
            if (variant.hasAllowedBiomes()) {
                if (isVariantIn(variant.allowedBiomes(), world)) totalWeight += variant.weight();
            } else totalWeight += variant.weight();
        }

        if (totalWeight <= 0) throw new RuntimeException("Failed to assign dragon variant due impossible total weight of all variants for " + this);

        int roll = random.nextInt(totalWeight);
        int previousBound = 0;

        for (DragonVariant variant : variants) {
            //banned biomes check (blacklist)
            if (variant.hasBannedBiomes() && isVariantIn(variant.bannedBiomes(), world)) continue;
            if (variant.altitudeRestriction().min() > blockPosition().getY() || blockPosition().getY() > variant.altitudeRestriction().max()) continue;
            //allowed biomes check (whitelist)
            if (variant.hasAllowedBiomes()) {
                if (isVariantIn(variant.allowedBiomes(), world)) {
                    if (roll >= previousBound && roll < previousBound + variant.weight()) {
                        setVariantName(variant.name());
                        break;
                    }
                    previousBound += variant.weight();
                }
            } else {
                if (roll >= previousBound && roll < previousBound + variant.weight()) {
                    setVariantName(variant.name());
                    break;
                }
                previousBound += variant.weight();
            }
        }
    }

    @Unique
    private boolean isVariantIn(DragonVariant.BiomeRestrictions restrictions, ServerLevelAccessor world) {
        Holder<Biome> biome = world.getBiome(blockPosition());
        List<String> id = restrictions.hasBiomesByIdList() ? restrictions.biomesById() : List.of();
        List<String> tags = restrictions.hasBiomesByTagList() ?restrictions.biomesByTag() : List.of();

        boolean isIn = false;
        for (String s : id) {
            ResourceLocation name = new ResourceLocation(s);
            if (biome.is(name)) {
                isIn = true;
                break;
            }
        }

        if (!isIn) for (String tag : tags) {
            ResourceLocation name = new ResourceLocation(tag);
            if (biome.is(TagKey.create(Registry.BIOME_REGISTRY, name))) {
                isIn = true;
                break;
            }
        }

        return isIn;
    }
}
