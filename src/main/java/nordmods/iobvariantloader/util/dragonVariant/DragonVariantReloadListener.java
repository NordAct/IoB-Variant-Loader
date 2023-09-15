package nordmods.iobvariantloader.util.dragonVariant;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.iobvariantloader.IoBVariantLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DragonVariantReloadListener extends SimplePreparableReloadListener<Map<String, List<DragonVariant>>> {

    //idgaf who and how, but without synchronized this thing is called from multiple threads... And yet it still sometimes somehow does ConcurrentModificationException
    @Override
    protected synchronized Map<String, List<DragonVariant>> prepare(ResourceManager manager, ProfilerFiller pProfiler) {
        DragonVariantUtil.dragonVariants.clear();
        Collection<ResourceLocation> resourceCollection = manager.listResources("dragon_variants", path -> path.endsWith(".json"));
        for (ResourceLocation id : resourceCollection) {
            String path = id.getPath();
            String dragon = path.substring(path.lastIndexOf("/") + 1, path.indexOf(".json"));

            List<DragonVariant> variants = new ArrayList<>();

            try (InputStream stream = manager.getResource(id).getInputStream()) {

                InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    JsonElement element = JsonParser.parseReader(bufferedReader);
                    JsonArray array = GsonHelper.getAsJsonArray((JsonObject) element, "variants");
                    for (JsonElement elem : array) {
                        JsonObject input = elem.getAsJsonObject();
                        String name = input.get("name").getAsString();
                        int weight = input.get("weight").getAsInt();

                        DragonVariant.BiomeRestrictions allowedBiomes = getBiomes("allowed_biomes", input);
                        DragonVariant.BiomeRestrictions bannedBiomes = getBiomes("banned_biomes", input);
                        DragonVariant.AltitudeRestriction altitudeRestriction = getAltitude(input);

                        DragonVariant dragonVariant = new DragonVariant(name, weight, allowedBiomes, bannedBiomes, altitudeRestriction);
                        if (!variants.contains(dragonVariant)) variants.add(dragonVariant);
                    }
                } catch (JsonIOException e) {
                    IoBVariantLoader.LOGGER.error("Failed to read json " + id, e);
                }

            } catch (Exception e) {
                IoBVariantLoader.LOGGER.error("Error occurred while loading resource json " + id, e);
            }
           DragonVariantUtil.add(dragon, variants);
        }
        DragonVariantUtil.debugPrint();
        return DragonVariantUtil.dragonVariants;
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
            JsonObject object = GsonHelper.getAsJsonObject(input,"altitude");
            if (object.has("min")) min = object.get("min").getAsInt();
            if (object.has("max")) max = object.get("max").getAsInt();
        }
        return new DragonVariant.AltitudeRestriction(min, max);
    }

    @Override
    protected void apply(Map<String, List<DragonVariant>> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
    }
}
