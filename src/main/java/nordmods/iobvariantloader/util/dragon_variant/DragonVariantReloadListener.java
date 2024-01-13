package nordmods.iobvariantloader.util.dragon_variant;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DragonVariantReloadListener extends SimpleJsonResourceReloadListener {

    public DragonVariantReloadListener() {
        super(new GsonBuilder().create(), "dragon_variants");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        DragonVariantUtil.dragonVariants.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            String dragon = entry.getKey().getPath();
            List<DragonVariant> variants = new ArrayList<>();

            JsonArray array = entry.getValue().getAsJsonObject().get("variants").getAsJsonArray();

            for (JsonElement elem : array) {
                JsonObject input = elem.getAsJsonObject();
                String name = input.get("name").getAsString();
                int weight = input.get("weight").getAsInt();
                int breedingWeight = input.has("breeding_weight") ? input.get("breeding_weight").getAsInt() : weight;

                DragonVariant.BiomeRestrictions allowedBiomes = getBiomes("allowed_biomes", input);
                DragonVariant.BiomeRestrictions bannedBiomes = getBiomes("banned_biomes", input);
                DragonVariant.AltitudeRestriction altitudeRestriction = getAltitude(input);

                DragonVariant dragonVariant = new DragonVariant(name, weight, breedingWeight, allowedBiomes, bannedBiomes, altitudeRestriction);
                if (!variants.contains(dragonVariant)) variants.add(dragonVariant);
            }
            DragonVariantUtil.add(dragon, variants);
        }
        DragonVariantUtil.debugPrint();
    }

    private DragonVariant.BiomeRestrictions getBiomes(String list, JsonObject input) {
        DragonVariant.BiomeRestrictions restrictions = null;
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

            restrictions = new DragonVariant.BiomeRestrictions(biomesById, biomesByTag);
        }
        return restrictions;
    }

    private DragonVariant.AltitudeRestriction getAltitude(JsonObject input) {
        int min = -1000;
        int max = 1000;
        if (input.has("altitude")) {
            JsonObject object = GsonHelper.getAsJsonObject(input, "altitude");
            if (object.has("min")) min = object.get("min").getAsInt();
            if (object.has("max")) max = object.get("max").getAsInt();
        }
        return new DragonVariant.AltitudeRestriction(min, max);
    }

}
