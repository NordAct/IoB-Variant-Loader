package nordmods.iobvariantloader.util.dragon_variant_spawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public record DragonVariantSpawner(
        int weight,
        int breedingWeight,
        @NotNull BiomeRestrictions allowedBiomes,
        @NotNull BiomeRestrictions bannedBiomes,
        AltitudeRestriction altitudeRestriction,
        SurfaceRestriction surfaceRestriction
) {
    public static final Codec<DragonVariantSpawner> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("weight").forGetter(DragonVariantSpawner::weight),
            Codec.INT.optionalFieldOf("breeding_weight", -1).forGetter(DragonVariantSpawner::breedingWeight),
            BiomeRestrictions.CODEC.optionalFieldOf("allowed_biomes", new BiomeRestrictions(List.of(), List.of())).forGetter(DragonVariantSpawner::allowedBiomes),
            BiomeRestrictions.CODEC.optionalFieldOf("banned_biomes", new BiomeRestrictions(List.of(), List.of())).forGetter(DragonVariantSpawner::bannedBiomes),
            AltitudeRestriction.CODEC.optionalFieldOf("altitude", new AltitudeRestriction(-1000, 1000)).forGetter(DragonVariantSpawner::altitudeRestriction),
            SurfaceRestriction.CODEC.optionalFieldOf("surface_restriction", SurfaceRestriction.NONE).forGetter(DragonVariantSpawner::surfaceRestriction)
    ).apply(instance, (weight, breedingWeight, allowedBiomes, bannedBiomes, altitude, surfaceRestriction) -> {
        if (breedingWeight < 0) breedingWeight = weight;
        if (!allowedBiomes.hasBiomesByIdList() && !allowedBiomes.hasBiomesByTagList() && weight > 0) allowedBiomes = new BiomeRestrictions(List.of(), List.of(new ResourceLocation("forge:is_overworld")));
        return new DragonVariantSpawner(weight, breedingWeight, allowedBiomes, bannedBiomes, altitude, surfaceRestriction);
    }));
    //allowed - works as whitelist if presented
    //banned - works as blacklist if presented
    public boolean hasAllowedBiomes() {
        return allowedBiomes.hasBiomesByIdList() || allowedBiomes.hasBiomesByTagList();
    }

    public boolean hasBannedBiomes() {
        return bannedBiomes.hasBiomesByIdList() || bannedBiomes.hasBiomesByTagList();
    }

    public record BiomeRestrictions(@NotNull List<ResourceLocation> biomesById, @NotNull List<ResourceLocation> biomesByTag) {
        public static Codec<BiomeRestrictions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.listOf().optionalFieldOf("biome", List.of()).forGetter(BiomeRestrictions::biomesById),
                ResourceLocation.CODEC.listOf().optionalFieldOf("tag", List.of()).forGetter(BiomeRestrictions::biomesByTag)
        ).apply(instance, BiomeRestrictions::new));

        public boolean hasBiomesByIdList() {
            return !biomesById.isEmpty();
        }

        public boolean hasBiomesByTagList() {
            return !biomesByTag.isEmpty();
        }
    }

    public record AltitudeRestriction(int min, int max) {
        public static Codec<AltitudeRestriction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("min", -1000).forGetter(AltitudeRestriction::min),
                Codec.INT.optionalFieldOf("max", 1000).forGetter(AltitudeRestriction::max)
        ).apply(instance, AltitudeRestriction::new));
    }

    public enum SurfaceRestriction {
        NONE(true, true),
        SURFACE(true, false),
        UNDERGROUND(false, true);
        private final boolean surfaceSpawn;
        private final boolean undergroundSpawn;
        public static Codec<SurfaceRestriction> CODEC = ExtraCodecs.stringResolverCodec(e ->
                e.name().toLowerCase(Locale.ROOT),
                name -> Enum.valueOf(SurfaceRestriction.class, name.toUpperCase(Locale.ROOT))
        );

        SurfaceRestriction(boolean surfaceSpawn, boolean undergroundSpawn) {
            this.surfaceSpawn = surfaceSpawn;
            this.undergroundSpawn = undergroundSpawn;
        }

        public boolean canSpawn(BlockPos pos, ServerLevelAccessor level) {
            boolean surface = level.canSeeSky(pos);
            return !surface && undergroundSpawn || surface && surfaceSpawn;
        }
    }
}
