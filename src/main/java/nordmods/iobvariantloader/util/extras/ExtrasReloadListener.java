package nordmods.iobvariantloader.util.extras;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.variant_collections.VariantCollectionsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtrasReloadListener extends SimpleJsonResourceReloadListener {
    public ExtrasReloadListener() {
        super(new GsonBuilder().create(), "extras");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ExtrasUtil.EXTRAS.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation fileID = entry.getKey();
            JsonObject entryObject = entry.getValue().getAsJsonObject();

            String dragon = entryObject.has("dragon") ? entryObject.get("dragon").getAsString() : fileID.getPath();
            boolean valid = ResourceUtil.AllowedValues.isValid(dragon, false);
            Map<String, Extras> toPut = new HashMap<>();

            JsonArray array = entryObject.get("extras").getAsJsonArray();
            for (JsonElement elem : array) {
                JsonObject input = elem.getAsJsonObject();
                Extras data = Extras.CODEC.parse(JsonOps.INSTANCE, input).getOrThrow(false, (error) -> {
                    IoBVariantLoader.LOGGER.error("Failed to parse extras data file {} correctly. Check for syntax errors and try again", fileID.toString());
                    IoBVariantLoader.LOGGER.error(error);
                });
                if (input.has("name")) {
                    JsonElement nameElem = input.getAsJsonObject().get("name");
                    List<String> names = nameElem.isJsonArray() ?
                            Util.make(new ArrayList<>(), l -> nameElem.getAsJsonArray().forEach(e -> l.add(e.getAsString()))) :
                            List.of(nameElem.getAsString());
                    names.forEach(name -> {
                        if (valid) toPut.put(name, data);
                        else IoBVariantLoader.LOGGER.warn("Extras entry in {} for name {} does not match any dragon id and will be skipped", fileID, name);
                    });
                }
                if (input.has("collections")) {
                    Map<String, Map<String, Extras>> collections = new HashMap<>();
                    input.getAsJsonArray("collections").forEach(collection -> {
                        VariantCollectionsUtil.getCollectionLists(collection.getAsString()).forEach(variantList -> {
                            Map<String, Extras> speciesCollection = collections.computeIfAbsent(variantList.dragon(), (s) -> new HashMap<>());
                            variantList.variants().forEach(variant -> speciesCollection.put(variant, data));
                        });
                    });
                    collections.forEach(ExtrasUtil::add);
                }
                if (!input.has("name") && input.has("collections")) {
                    IoBVariantLoader.LOGGER.warn("Extras file {} has entries with no names or collections specified", fileID);
                }
            }
            if (valid) ExtrasUtil.add(dragon, toPut);
        }
        ExtrasUtil.debugPrint();
    }
}
