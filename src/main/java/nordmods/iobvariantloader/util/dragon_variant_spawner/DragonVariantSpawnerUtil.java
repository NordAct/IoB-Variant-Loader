package nordmods.iobvariantloader.util.dragon_variant_spawner;

import com.mojang.datafixers.util.Pair;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DragonVariantSpawnerUtil {
    public static final Map<String, List<Pair<List<String>, DragonVariantSpawner>>> DRAGON_VARIANT_SPAWNS = new HashMap<>();

    public static List<Pair<List<String>, DragonVariantSpawner>> getVariantsFor(String name) {
        return DRAGON_VARIANT_SPAWNS.get(name);
    }

    public static synchronized void add(String name, List<Pair<List<String>, DragonVariantSpawner>> variants) {
        List<Pair<List<String>, DragonVariantSpawner>> content = DRAGON_VARIANT_SPAWNS.get(name);
        if (content != null) {
            List<Pair<List<String>, DragonVariantSpawner>> copy = new ArrayList<>();
            copy.addAll(variants);
            copy.addAll(content);
            DRAGON_VARIANT_SPAWNS.put(name, copy);
        } else DRAGON_VARIANT_SPAWNS.put(name, variants);
    }

    public static void debugPrint() {
        if (!IoBVariantLoader.config.logDragonVariantSpawns.get()) return;
        for (Map.Entry<String, List<Pair<List<String>, DragonVariantSpawner>>> entry : DRAGON_VARIANT_SPAWNS.entrySet()) {
            for (Pair<List<String>, DragonVariantSpawner> variants : entry.getValue()) {
                if (variants.getFirst().isEmpty()) continue;

                StringBuilder names = new StringBuilder();
                names.append(variants.getFirst().get(0));
                for (int i = 1; i < variants.getFirst().size(); i++) {
                    names.append(", ").append(variants.getFirst().get(i));
                }

                DragonVariantSpawner spawn = variants.getSecond();
                StringBuilder conditions = new StringBuilder();
                conditions.append("Weight: ").append(spawn.weight()).append("\n");
                conditions.append("Breeding Weight: ").append(spawn.breedingWeight()).append("\n");
                if (spawn.hasAllowedBiomes()) {
                    conditions.append("Allowed Biomes: \n");
                    List<ResourceLocation> biomesById = spawn.allowedBiomes().biomesById();
                    List<ResourceLocation> biomesByTag = spawn.allowedBiomes().biomesByTag();
                    if (!biomesById.isEmpty()) {
                        conditions.append("- Biomes by ID: ");
                        biomesById.forEach(id -> conditions.append(id.toString()).append(" "));
                        conditions.append("\n");
                    }
                    if (!biomesByTag.isEmpty()) {
                        conditions.append("- Biomes by tag: ");
                        biomesByTag.forEach(id -> conditions.append(id.toString()).append(" "));
                        conditions.append("\n");
                    }
                }

                if (spawn.hasBannedBiomes()) {
                    conditions.append("Banned Biomes: \n");
                    List<ResourceLocation> biomesById = spawn.bannedBiomes().biomesById();
                    List<ResourceLocation> biomesByTag = spawn.bannedBiomes().biomesByTag();
                    if (!biomesById.isEmpty()) {
                        conditions.append("- Biomes by ID: ");
                        biomesById.forEach(id -> conditions.append(id.toString()).append(" "));
                        conditions.append("\n");
                    }
                    if (!biomesByTag.isEmpty()) {
                        conditions.append("- Biomes by tag: ");
                        biomesByTag.forEach(id -> conditions.append(id.toString()).append(" "));
                        conditions.append("\n");
                    }
                }

                conditions.append("Altitude Restriction: \n");
                conditions.append("- Min: ").append(spawn.altitudeRestriction().min()).append("\n");
                conditions.append("- Max: ").append(spawn.altitudeRestriction().max()).append("\n");

                conditions.append("Surface Restriction: ").append(spawn.surfaceRestriction()).append("\n");

                IoBVariantLoader.LOGGER.info("{}: variant(s) {} was/were loaded with following conditions:\n{}", entry.getKey(), names, conditions);
            }
        }
    }

    public static boolean isVariantIn(DragonVariantSpawner.BiomeRestrictions restrictions, ServerLevelAccessor world, BlockPos blockPos) {
        Holder<Biome> biome = world.getBiome(blockPos);
        List<ResourceLocation> ids = restrictions.hasBiomesByIdList() ? restrictions.biomesById() : List.of();
        List<ResourceLocation> tags = restrictions.hasBiomesByTagList() ? restrictions.biomesByTag() : List.of();

        boolean isIn = false;
        for (ResourceLocation id : ids) {
            if (biome.is(id)) {
                isIn = true;
                break;
            }
        }

        if (!isIn) for (ResourceLocation tag : tags) {
            if (biome.is(TagKey.create(Registry.BIOME_REGISTRY, tag))) {
                isIn = true;
                break;
            }
        }

        return isIn;
    }

    public static void assignVariant(ServerLevelAccessor world, LivingEntity entity, boolean naturalSpawn) {
        assignVariant(world, entity, naturalSpawn, null);
    }

    public static void assignVariant(ServerLevelAccessor world, LivingEntity entity, boolean naturalSpawn, @Nullable VariantNameHelper sourceEntity) {
        List<Pair<List<String>, DragonVariantSpawner>> variants = sourceEntity != null ? DragonVariantSpawnerUtil.getVariantsFor(sourceEntity) : DragonVariantSpawnerUtil.getVariantsFor((VariantNameHelper)entity);
        assignVariantFromList(world, entity, naturalSpawn, variants);
    }

    @SuppressWarnings("DataFlowIssue")
    public static void assignVariantFromList(ServerLevelAccessor world, LivingEntity entity, boolean naturalSpawn, List<Pair<List<String>, DragonVariantSpawner>> variants) {
        if (!(entity instanceof VariantNameHelper helper)) return;

        if (variants != null) {
            BlockPos pos = entity.blockPosition();
            long totalWeight = 0;
            for (Pair<List<String>, DragonVariantSpawner> entry : variants) {
                if (entry.getFirst().isEmpty()) continue;

                DragonVariantSpawner spawn = entry.getSecond();
                //surface check
                if (!(spawn.surfaceRestriction().canSpawn(pos, world))) continue;
                //banned biomes check (blacklist)
                if (spawn.hasBannedBiomes() && DragonVariantSpawnerUtil.isVariantIn(spawn.bannedBiomes(), world, pos)) continue;
                if (spawn.altitudeRestriction().min() > pos.getY() || pos.getY() > spawn.altitudeRestriction().max()) continue;

                //allowed biomes check (whitelist)
                if (spawn.hasAllowedBiomes()) {
                    if (DragonVariantSpawnerUtil.isVariantIn(spawn.allowedBiomes(), world, pos)) {
                        if (naturalSpawn) totalWeight += spawn.weight();
                        else totalWeight += spawn.breedingWeight();
                    }
                } else {
                    if (naturalSpawn) totalWeight += spawn.weight();
                    else totalWeight += spawn.breedingWeight();
                }
            }

            if (totalWeight <= 0) {
                if (entity instanceof DefaultVariantNameHelper defaultVariantNameHelper) helper.setVariantName(defaultVariantNameHelper.getFromBaseVariant());
                else helper.setVariantName("");

                IoBVariantLoader.LOGGER.warn("Failed to get variant for {} ({}) at {}, setting default", entity.getName().getString(), entity.getType().getRegistryName().getPath(), pos.toShortString());
                return;
            }

            long roll = entity.getRandom().nextLong(totalWeight);
            long previousBound = 0;

            for (Pair<List<String>, DragonVariantSpawner> entry : variants) {
                DragonVariantSpawner spawn = entry.getSecond();
                //surface check
                if (!(spawn.surfaceRestriction().canSpawn(pos, world))) continue;
                //banned biomes check (blacklist)
                if (spawn.hasBannedBiomes() && DragonVariantSpawnerUtil.isVariantIn(spawn.bannedBiomes(), world, pos)) continue;
                if (spawn.altitudeRestriction().min() > pos.getY() || pos.getY() > spawn.altitudeRestriction().max()) continue;
                //allowed biomes check (whitelist)
                if (spawn.hasAllowedBiomes()) {
                    if (DragonVariantSpawnerUtil.isVariantIn(spawn.allowedBiomes(), world, pos)) {
                        if (roll >= previousBound && roll < previousBound + (naturalSpawn ? spawn.weight() : spawn.breedingWeight())) {
                            helper.setVariantName(entry.getFirst().get(entity.getRandom().nextInt(entry.getFirst().size())));
                            break;
                        }
                        if (naturalSpawn) previousBound += spawn.weight();
                        else previousBound += spawn.breedingWeight();
                    }
                } else {
                    if (roll >= previousBound && roll < previousBound + (naturalSpawn ? spawn.weight() : spawn.breedingWeight())) {
                        helper.setVariantName(entry.getFirst().get(entity.getRandom().nextInt(entry.getFirst().size())));
                        break;
                    }
                    if (naturalSpawn) previousBound += spawn.weight();
                    else previousBound += spawn.breedingWeight();
                }
            }
        }
    }

    public static boolean hasNaturalVariantsForSpot(String entity, ServerLevelAccessor world, BlockPos pos) {
        List<Pair<List<String>, DragonVariantSpawner>> variants = getVariantsFor(entity);
        for (Pair<List<String>, DragonVariantSpawner> entry : variants) {
            if (entry.getFirst().isEmpty()) continue;

            DragonVariantSpawner spawn = entry.getSecond();
            //weight check
            if (spawn.weight() <= 0) continue;
            //surface check
            if (!(spawn.surfaceRestriction().canSpawn(pos, world))) continue;
            //banned biomes check (blacklist)
            if (spawn.hasBannedBiomes() && DragonVariantSpawnerUtil.isVariantIn(spawn.bannedBiomes(), world, pos)) continue;
            if (spawn.altitudeRestriction().min() > pos.getY() || pos.getY() > spawn.altitudeRestriction().max()) continue;

            //allowed biomes check (whitelist)
            if (spawn.hasAllowedBiomes()) {
                if (!DragonVariantSpawnerUtil.isVariantIn(spawn.allowedBiomes(), world, pos)) continue;
            }

            return true;
        }
        return false;
    }

    public static List<Pair<List<String>, DragonVariantSpawner>> getVariantsFor(VariantNameHelper entity) {
        ResourceLocation resourcelocation = EntityType.getKey(((Entity) entity).getType());
        return DragonVariantSpawnerUtil.getVariantsFor(resourcelocation.getPath());
    }

    @Nullable
    public static DragonVariantSpawner getVariantByName(VariantNameHelper entity, String name) {
        List<Pair<List<String>, DragonVariantSpawner>> list = getVariantsFor(entity);
        for (Pair<List<String>, DragonVariantSpawner> variant : list) if (variant.getFirst().contains(name)) return variant.getSecond();
        return null;
    }
}
