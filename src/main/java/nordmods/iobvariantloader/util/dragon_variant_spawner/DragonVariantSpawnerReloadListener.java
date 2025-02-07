package nordmods.iobvariantloader.util.dragon_variant_spawner;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ResourceUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DragonVariantSpawnerReloadListener extends SimpleJsonResourceReloadListener {

    public DragonVariantSpawnerReloadListener() {
        super(new GsonBuilder().create(), "dragon_variants");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        DragonVariantSpawnerUtil.dragonVariants.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            JsonObject entryObject = entry.getValue().getAsJsonObject();

            String dragon = entryObject.has("dragon") ? entryObject.get("dragon").getAsString() : entry.getKey().getPath();
            if (!ResourceUtil.AllowedValues.isValid(dragon, true)) {
                IoBVariantLoader.LOGGER.warn("Variant spawns entry {} does not match any dragon id and will be skipped", entry.getKey());
                continue;
            }
            List<DragonVariantSpawner> variants = new ArrayList<>();

            JsonArray array = entryObject.get("variants").getAsJsonArray();
            for (JsonElement elem : array) {
                JsonObject input = elem.getAsJsonObject();
                String name = input.get("name").getAsString();
                int weight = input.get("weight").getAsInt();
                int breedingWeight = input.has("breeding_weight") ? input.get("breeding_weight").getAsInt() : weight;

                DragonVariantSpawner.BiomeRestrictions allowedBiomes = getBiomes("allowed_biomes", input, weight > 0);
                DragonVariantSpawner.BiomeRestrictions bannedBiomes = getBiomes("banned_biomes", input, false);
                DragonVariantSpawner.AltitudeRestriction altitudeRestriction = getAltitude(input);
                DragonVariantSpawner.SurfaceRestriction surfaceRestriction = getSurfaceRestriction(input);

                DragonVariantSpawner dragonVariant = new DragonVariantSpawner(name, weight, breedingWeight, allowedBiomes, bannedBiomes, altitudeRestriction, surfaceRestriction);
                if (!variants.contains(dragonVariant)) variants.add(dragonVariant);
            }
            DragonVariantSpawnerUtil.add(dragon, variants);
        }
        DragonVariantSpawnerUtil.debugPrint();
    }

    private DragonVariantSpawner.BiomeRestrictions getBiomes(String list, JsonObject input, boolean defaultIfEmpty) {
        DragonVariantSpawner.BiomeRestrictions restrictions = null;
        if (input.has(list)) {
            JsonObject biomes = GsonHelper.getAsJsonObject(input, list);

            List<String> biomesById = new ArrayList<>();
            if (biomes.has("biome")) {
                JsonArray tags = biomes.get("biome").getAsJsonArray();
                for (int j = 0; j < tags.size(); j++) biomesById.add(tags.get(j).getAsString());
            }

            List<String> biomesByTag = new ArrayList<>();
            if (biomes.has("tag")) {
                JsonArray tags = biomes.get("tag").getAsJsonArray();
                for (int j = 0; j < tags.size(); j++) biomesByTag.add(tags.get(j).getAsString());
            }

            if (defaultIfEmpty && biomesById.isEmpty() && biomesByTag.isEmpty()) biomesByTag.add("forge:is_overworld");

            restrictions = new DragonVariantSpawner.BiomeRestrictions(biomesById, biomesByTag);
        } else if (defaultIfEmpty) restrictions = new DragonVariantSpawner.BiomeRestrictions(List.of(), List.of("forge:is_overworld"));
        return restrictions;
    }

    private DragonVariantSpawner.AltitudeRestriction getAltitude(JsonObject input) {
        int min = -1000;
        int max = 1000;
        if (input.has("altitude")) {
            JsonObject object = GsonHelper.getAsJsonObject(input, "altitude");
            if (object.has("min")) min = object.get("min").getAsInt();
            if (object.has("max")) max = object.get("max").getAsInt();
        }
        return new DragonVariantSpawner.AltitudeRestriction(min, max);
    }

    private DragonVariantSpawner.SurfaceRestriction getSurfaceRestriction(JsonObject input) {
        String restriction = "";
        if (input.has("surface_restriction")) restriction = input.get("surface_restriction").getAsString();
        return switch (restriction) {
            case "underground" -> DragonVariantSpawner.SurfaceRestriction.UNDERGROUND;
            case "surface" -> DragonVariantSpawner.SurfaceRestriction.SURFACE;
            default -> DragonVariantSpawner.SurfaceRestriction.NONE;
        };
    }
}
