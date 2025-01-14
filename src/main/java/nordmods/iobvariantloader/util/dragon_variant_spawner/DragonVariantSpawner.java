package nordmods.iobvariantloader.util.dragon_variant_spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record DragonVariantSpawner(String name, int weight, int breedingWeight, @Nullable BiomeRestrictions allowedBiomes, @Nullable BiomeRestrictions bannedBiomes, AltitudeRestriction altitudeRestriction, SurfaceRestriction surfaceRestriction) {
    //allowed - works as whitelist if presented
    //banned - works as blacklist if presented
    public boolean hasAllowedBiomes() {
        return allowedBiomes != null && (allowedBiomes.hasBiomesByIdList() || allowedBiomes.hasBiomesByTagList());
    }

    public boolean hasBannedBiomes() {
        return bannedBiomes != null && (bannedBiomes.hasBiomesByIdList() || bannedBiomes.hasBiomesByTagList());
    }

    public record BiomeRestrictions(List<String> biomesById, List<String> biomesByTag) {
        public boolean hasBiomesByIdList() {
            return biomesById != null && !biomesById.isEmpty();
        }

        public boolean hasBiomesByTagList() {
            return biomesByTag != null && !biomesByTag.isEmpty();
        }
    }

    public record AltitudeRestriction(int min, int max) {}

    public enum SurfaceRestriction {
        NONE(true, true),
        SURFACE(true, false),
        UNDERGROUND(false, true);
        private final boolean surfaceSpawn;
        private final boolean undergroundSpawn;

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
