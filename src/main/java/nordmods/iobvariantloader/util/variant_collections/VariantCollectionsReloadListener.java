package nordmods.iobvariantloader.util.variant_collections;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ResourceUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VariantCollectionsReloadListener extends SimpleJsonResourceReloadListener {
    public VariantCollectionsReloadListener() {
        super(new GsonBuilder().create(), "variant_collections");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        VariantCollectionsUtil.VARIANT_COLLECTIONS.clear();
        Map<String, Set<String>> collectionsToInclude = new HashMap<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation fileID = entry.getKey();
            JsonObject entryObject = entry.getValue().getAsJsonObject();
            String collection = fileID.getPath();

            if (entryObject.has("variant_lists")) {
                for (JsonElement elem : entryObject.get("variant_lists").getAsJsonArray()) {
                    VariantList list = VariantList.CODEC.parse(JsonOps.INSTANCE, elem).getOrThrow(false, error -> {
                        IoBVariantLoader.LOGGER.error("Failed to parse variant list in {} correctly. Check for syntax errors and try again", fileID.toString());
                        IoBVariantLoader.LOGGER.error(error);
                    });
                    String dragon = list.dragon();
                    if (!ResourceUtil.AllowedValues.isValid(dragon, false)) {
                        IoBVariantLoader.LOGGER.warn("Variant list entry of {} with dragon id {} does not match any existing dragon id and will be skipped", fileID, dragon);
                        continue;
                    }
                    VariantCollectionsUtil.add(collection, list);
                }
            }

            if (entryObject.has("collections")) {
                Set<String> includedcollections = collectionsToInclude.computeIfAbsent(collection, (g) -> new HashSet<>());
                entryObject.get("collections").getAsJsonArray().forEach(g -> includedcollections.add(g.getAsString()));
            }
        }
        collectionsToInclude.forEach((collection, included) -> addIncludedCollectionsToCollection(collection, included, collectionsToInclude));
        VariantCollectionsUtil.debugPrint();
    }

    private void addIncludedCollectionsToCollection(String collection, Set<String> includedCollections, Map<String, Set<String>> collectionInCollection) {
        includedCollections.forEach(includedCollection -> {
            if (includedCollection.equals(collection)) return;

            if (collectionInCollection.containsKey(includedCollection))
                addIncludedCollectionsToCollection(collection, collectionInCollection.get(includedCollection), collectionInCollection);

            if (VariantCollectionsUtil.VARIANT_COLLECTIONS.containsKey(includedCollection))
                VariantCollectionsUtil.VARIANT_COLLECTIONS.get(includedCollection).forEach(includedList -> VariantCollectionsUtil.add(collection, includedList));
        });
    }
}
