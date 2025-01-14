package nordmods.iobvariantloader.util.dragon_variant_spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ducks.DefaultVariantNameHelper;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DragonVariantSpawnerUtil {
    public static final Map<String, List<DragonVariantSpawner>> dragonVariants = new HashMap<>();

    public static List<DragonVariantSpawner> getVariantsFor(String name) {
        return dragonVariants.get(name);
    }

    public static synchronized void add(String name, List<DragonVariantSpawner> variants) {
        List<DragonVariantSpawner> content = dragonVariants.get(name);
        if (content != null) {
            content.addAll(variants);
            dragonVariants.put(name, content);
        } else dragonVariants.put(name, variants);
    }

    public static void debugPrint() {
        for (Map.Entry<String, List<DragonVariantSpawner>> entry : dragonVariants.entrySet()) {
            for (DragonVariantSpawner variant : entry.getValue()) {
                IoBVariantLoader.LOGGER.debug("{}: variant {} was loaded", entry.getKey(), variant);
            }
        }
    }

    public static boolean isVariantIn(DragonVariantSpawner.BiomeRestrictions restrictions, ServerLevelAccessor world, BlockPos blockPos) {
        Holder<Biome> biome = world.getBiome(blockPos);
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

    public static void assignVariant(ServerLevelAccessor world, Entity entity, boolean naturalSpawn) {
        assignVariant(world, entity, naturalSpawn, null);
    }

    public static void assignVariant(ServerLevelAccessor world, Entity entity, boolean naturalSpawn, @Nullable VariantNameHelper sourceEntity) {
        List<DragonVariantSpawner> variants = sourceEntity != null ? DragonVariantSpawnerUtil.getVariantsFor(sourceEntity) : DragonVariantSpawnerUtil.getVariantsFor((VariantNameHelper)entity);
        assignVariantFromList(world, entity, naturalSpawn, variants);
    }

    @SuppressWarnings("DataFlowIssue")
    public static void assignVariantFromList(ServerLevelAccessor world, Entity entity, boolean naturalSpawn, List<DragonVariantSpawner> variants) {
        if (!(entity instanceof VariantNameHelper helper)) return;

        if (variants != null) {
            BlockPos pos = entity.blockPosition();
            long totalWeight = 0;
            for (DragonVariantSpawner variant : variants) {
                //surface check
                if (!(variant.surfaceRestriction().canSpawn(pos, world))) continue;
                //banned biomes check (blacklist)
                if (variant.hasBannedBiomes() && DragonVariantSpawnerUtil.isVariantIn(variant.bannedBiomes(), world, pos)) continue;
                if (variant.altitudeRestriction().min() > pos.getY() || pos.getY() > variant.altitudeRestriction().max()) continue;

                //allowed biomes check (whitelist)
                if (variant.hasAllowedBiomes()) {
                    if (DragonVariantSpawnerUtil.isVariantIn(variant.allowedBiomes(), world, pos)) {
                        if (naturalSpawn) totalWeight += variant.weight();
                        else totalWeight += variant.breedingWeight();
                    }
                } else {
                    if (naturalSpawn) totalWeight += variant.weight();
                    else totalWeight += variant.breedingWeight();
                }
            }

            if (totalWeight <= 0) {
                if (entity instanceof DefaultVariantNameHelper defaultVariantNameHelper) helper.setVariantName(defaultVariantNameHelper.getFromBaseVariant());
                else helper.setVariantName("");

                IoBVariantLoader.LOGGER.warn("Failed to get variant for {} ({}), setting default", entity.getName().getString(), entity.getType().getRegistryName().getPath());
                return;
            }

            long roll = ((LivingEntity) entity).getRandom().nextLong(totalWeight);
            long previousBound = 0;

            for (DragonVariantSpawner variant : variants) {
                //surface check
                if (!(variant.surfaceRestriction().canSpawn(pos, world))) continue;
                //banned biomes check (blacklist)
                if (variant.hasBannedBiomes() && DragonVariantSpawnerUtil.isVariantIn(variant.bannedBiomes(), world, pos)) continue;
                if (variant.altitudeRestriction().min() > pos.getY() || pos.getY() > variant.altitudeRestriction().max()) continue;
                //allowed biomes check (whitelist)
                if (variant.hasAllowedBiomes()) {
                    if (DragonVariantSpawnerUtil.isVariantIn(variant.allowedBiomes(), world, pos)) {
                        if (roll >= previousBound && roll < previousBound + (naturalSpawn ? variant.weight() : variant.breedingWeight())) {
                            helper.setVariantName(variant.name());
                            break;
                        }
                        if (naturalSpawn) previousBound += variant.weight();
                        else previousBound += variant.breedingWeight();
                    }
                } else {
                    if (roll >= previousBound && roll < previousBound + (naturalSpawn ? variant.weight() : variant.breedingWeight())) {
                        helper.setVariantName(variant.name());
                        break;
                    }
                    if (naturalSpawn) previousBound += variant.weight();
                    else previousBound += variant.breedingWeight();
                }
            }
        }
    }

    public static boolean hasNaturalVariantsForSpot(String entity, ServerLevelAccessor world, BlockPos pos) {
        List<DragonVariantSpawner> variants = getVariantsFor(entity);
        for (DragonVariantSpawner variant : variants) {
            //weight check
            if (variant.weight() <= 0) continue;
            //surface check
            if (!(variant.surfaceRestriction().canSpawn(pos, world))) continue;
            //banned biomes check (blacklist)
            if (variant.hasBannedBiomes() && DragonVariantSpawnerUtil.isVariantIn(variant.bannedBiomes(), world, pos)) continue;
            if (variant.altitudeRestriction().min() > pos.getY() || pos.getY() > variant.altitudeRestriction().max()) continue;

            //allowed biomes check (whitelist)
            if (variant.hasAllowedBiomes()) {
                if (!DragonVariantSpawnerUtil.isVariantIn(variant.allowedBiomes(), world, pos)) continue;
            }

            return true;
        }
        return false;
    }

    public static List<DragonVariantSpawner> getVariantsFor(VariantNameHelper entity) {
        ResourceLocation resourcelocation = EntityType.getKey(((Entity) entity).getType());
        return DragonVariantSpawnerUtil.getVariantsFor(resourcelocation.getPath());
    }

    @Nullable
    public static DragonVariantSpawner getVariantByName(VariantNameHelper entity, String name) {
        List<DragonVariantSpawner> list = getVariantsFor(entity);
        for (DragonVariantSpawner variant : list) if (variant.name().equals(name)) return variant;
        return null;
    }
}
