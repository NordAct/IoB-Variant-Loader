package nordmods.iobvariantloader.util.sound_redirect;

import com.google.gson.*;
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

public class SoundRedirectReloadListener extends SimpleJsonResourceReloadListener {
    public SoundRedirectReloadListener() {
        super(new GsonBuilder().create(), "sound_redirects");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        SoundRedirectUtil.SOUND_REDIRECT.clear();
        SoundRedirectUtil.clearCahce();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation fileID = entry.getKey();
            JsonObject entryObject = entry.getValue().getAsJsonObject();

            String dragon = entryObject.has("dragon") ? entryObject.get("dragon").getAsString() : fileID.getPath();
            boolean valid = ResourceUtil.AllowedValues.isValid(dragon, false);
            Map<String, List<SoundRedirect>> toPut = new HashMap<>();

            JsonArray array = entryObject.get("redirects").getAsJsonArray();
            for (JsonElement elem : array) {
                JsonObject input = elem.getAsJsonObject();
                List<SoundRedirect> data = SoundRedirect.CODEC.listOf().parse(JsonOps.INSTANCE, input.getAsJsonArray("sounds")).getOrThrow(false, (error) -> {
                    IoBVariantLoader.LOGGER.error("Failed to parse sound redirect data file {} correctly. Check for syntax errors and try again", fileID.toString());
                    IoBVariantLoader.LOGGER.error(error);
                });
                if (input.has("name")) {
                    JsonElement nameElem = input.getAsJsonObject().get("name");
                    List<String> names = nameElem.isJsonArray() ?
                            Util.make(new ArrayList<>(), l -> nameElem.getAsJsonArray().forEach(e -> l.add(e.getAsString()))) :
                            List.of(nameElem.getAsString());
                    names.forEach(name -> {
                        if (valid) toPut.put(name, data);
                        else IoBVariantLoader.LOGGER.warn("Sound redirect entry in {} for name {} does not match any dragon id and will be skipped", fileID, name);
                    });
                }
                if (input.has("collections")) {
                    Map<String, Map<String, List<SoundRedirect>>> collections = new HashMap<>();
                    input.getAsJsonArray("collections").forEach(collection -> {
                        VariantCollectionsUtil.getCollectionLists(collection.getAsString()).forEach(variantList -> {
                            Map<String, List<SoundRedirect>> speciesCollection = collections.computeIfAbsent(variantList.dragon(), (s) -> new HashMap<>());
                            variantList.variants().forEach(variant -> speciesCollection.put(variant, data));
                        });
                    });
                    collections.forEach(SoundRedirectUtil::add);
                }
                if (!input.has("name") && input.has("collections")) {
                    IoBVariantLoader.LOGGER.warn("Sound redirect file {} has entries with no names or collections specified", fileID);
                }
            }
            if (valid) SoundRedirectUtil.add(dragon, toPut);
        }
        SoundRedirectUtil.debugPrint();
    }
}
