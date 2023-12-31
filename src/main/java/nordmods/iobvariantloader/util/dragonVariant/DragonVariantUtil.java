package nordmods.iobvariantloader.util.dragonVariant;

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
import nordmods.iobvariantloader.util.VariantNameHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DragonVariantUtil {
    public static final Map<String, List<DragonVariant>> dragonVariants = new HashMap<>();

    public static List<DragonVariant> getVariants(String name) {
        return dragonVariants.get(name);
    }

    public static synchronized void add(String name, List<DragonVariant> variants) {
        List<DragonVariant> content = dragonVariants.get(name);
        if (content != null) {
            content.addAll(variants);
            dragonVariants.put(name, content);
        } else dragonVariants.put(name, variants);
    }

    public static void debugPrint() {
        for (Map.Entry<String, List<DragonVariant>> entry : dragonVariants.entrySet()) {
            for ( DragonVariant variant : entry.getValue()) {
                IoBVariantLoader.LOGGER.debug("{}: variant {} was loaded", entry.getKey(), variant);
            }
        }
    }

    public static boolean isVariantIn(DragonVariant.BiomeRestrictions restrictions, ServerLevelAccessor world, BlockPos blockPos) {
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

    public static void assignVariant(ServerLevelAccessor world, Entity entity) {
        if (entity instanceof VariantNameHelper helper) {
            List<DragonVariant> variants = DragonVariantUtil.getVariants((VariantNameHelper) entity);
            if (variants != null) {

                int totalWeight = 0;
                for (DragonVariant variant : variants) {
                    //banned biomes check (blacklist)
                    if (variant.hasBannedBiomes() && DragonVariantUtil.isVariantIn(variant.bannedBiomes(), world, entity.blockPosition()))
                        continue;
                    if (variant.altitudeRestriction().min() > entity.blockPosition().getY() || entity.blockPosition().getY() > variant.altitudeRestriction().max())
                        continue;
                    //allowed biomes check (whitelist)
                    if (variant.hasAllowedBiomes()) {
                        if (DragonVariantUtil.isVariantIn(variant.allowedBiomes(), world, entity.blockPosition()))
                            totalWeight += variant.weight();
                    } else totalWeight += variant.weight();
                }

                if (totalWeight <= 0)
                    throw new RuntimeException("Failed to assign dragon variant due impossible total weight of all variants for " + entity);

                int roll = ((LivingEntity) entity).getRandom().nextInt(totalWeight);
                int previousBound = 0;

                for (DragonVariant variant : variants) {
                    //banned biomes check (blacklist)
                    if (variant.hasBannedBiomes() && DragonVariantUtil.isVariantIn(variant.bannedBiomes(), world, entity.blockPosition()))
                        continue;
                    if (variant.altitudeRestriction().min() > entity.blockPosition().getY() || entity.blockPosition().getY() > variant.altitudeRestriction().max())
                        continue;
                    //allowed biomes check (whitelist)
                    if (variant.hasAllowedBiomes()) {
                        if (DragonVariantUtil.isVariantIn(variant.allowedBiomes(), world, entity.blockPosition())) {
                            if (roll >= previousBound && roll < previousBound + variant.weight()) {
                                helper.setVariantName(variant.name());
                                break;
                            }
                            previousBound += variant.weight();
                        }
                    } else {
                        if (roll >= previousBound && roll < previousBound + variant.weight()) {
                            helper.setVariantName(variant.name());
                            break;
                        }
                        previousBound += variant.weight();
                    }
                }
            }
        }
    }

    public static List<DragonVariant> getVariants(VariantNameHelper entity) {
        ResourceLocation resourcelocation = EntityType.getKey(((Entity) entity).getType());
        return DragonVariantUtil.getVariants(resourcelocation.getPath());
    }

    public static void assignVariantFrom(VariantNameHelper entity, List<DragonVariant> variants) {
        int totalWeight = 0;
        for (DragonVariant variant : variants) totalWeight += variant.weight();
        if (totalWeight <= 0)
            throw new RuntimeException("Failed to assign dragon variant due impossible total weight of all variants for " + entity);

        int roll = ((LivingEntity) entity).getRandom().nextInt(totalWeight);
        int previousBound = 0;

        for (DragonVariant variant : variants) {
            if (roll >= previousBound && roll < previousBound + variant.weight()) {
                entity.setVariantName(variant.name());
                break;
            }
            previousBound += variant.weight();
        }
    }
}
