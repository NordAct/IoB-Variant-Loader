package nordmods.iobvariantloader.util.dragon_variant_spawner;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.variant_collections.VariantCollectionsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DragonVariantSpawnerReloadListener extends SimpleJsonResourceReloadListener {

    public DragonVariantSpawnerReloadListener() {
        super(new GsonBuilder().create(), "dragon_variants");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        DragonVariantSpawnerUtil.DRAGON_VARIANT_SPAWNS.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation fileID = entry.getKey();
            JsonObject entryObject = entry.getValue().getAsJsonObject();

            String dragon = entryObject.has("dragon") ? entryObject.get("dragon").getAsString() : fileID.getPath();
            boolean valid = ResourceUtil.AllowedValues.isValid(dragon, false);
            List<Pair<List<String>, DragonVariantSpawner>> toPut = new ArrayList<>();

            JsonArray array = entryObject.get("variants").getAsJsonArray();
            for (JsonElement elem : array) {
                JsonObject input = elem.getAsJsonObject();
                DragonVariantSpawner data = DragonVariantSpawner.CODEC.parse(JsonOps.INSTANCE, input).getOrThrow(false, (error) -> {
                    IoBVariantLoader.LOGGER.error("Failed to parse dragon variant spawner data file {} correctly. Check for syntax errors and try again", fileID.toString());
                    IoBVariantLoader.LOGGER.error(error);
                });
                if (input.has("name")) {
                    JsonElement nameElem = input.getAsJsonObject().get("name");
                    List<String> names = nameElem.isJsonArray() ?
                            Util.make(new ArrayList<>(), l -> nameElem.getAsJsonArray().forEach(e -> l.add(e.getAsString()))) :
                            List.of(nameElem.getAsString());
                    if (valid) toPut.add(new Pair<>(names, data));
                    else IoBVariantLoader.LOGGER.warn("Dragon variant spawner entry in {} does not match any dragon id and will be skipped", fileID);
                }
                if (input.has("collections")) {
                    Map<String, List<Pair<List<String>, DragonVariantSpawner>>> collections = new HashMap<>();
                    input.getAsJsonArray("collections").forEach(collection -> {
                        VariantCollectionsUtil.getCollectionLists(collection.getAsString()).forEach(variantList -> {
                            if (variantList.variants().isEmpty()) return;
                            List<Pair<List<String>, DragonVariantSpawner>> speciesCollection = collections.computeIfAbsent(variantList.dragon(), (s) -> new ArrayList<>());
                            speciesCollection.add(new Pair<>(variantList.variants(), data));
                        });
                    });
                    collections.forEach(DragonVariantSpawnerUtil::add);
                }
                if (!input.has("name") && input.has("collections")) {
                    IoBVariantLoader.LOGGER.warn("Dragon variant spawner file {} has entries with no names or collections specified", fileID);
                }
            }
            DragonVariantSpawnerUtil.add(dragon, toPut);
        }
        DragonVariantSpawnerUtil.debugPrint();
    }
}
