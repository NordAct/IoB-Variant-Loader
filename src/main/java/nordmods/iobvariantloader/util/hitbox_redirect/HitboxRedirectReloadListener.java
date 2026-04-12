package nordmods.iobvariantloader.util.hitbox_redirect;

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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HitboxRedirectReloadListener extends SimpleJsonResourceReloadListener {

    public HitboxRedirectReloadListener() {
        super(new GsonBuilder().create(), "hitbox_redirects");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        HitboxRedirectUtil.DRAGON_HITBOX_REDIRECTS.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation fileID = entry.getKey();
            JsonObject entryObject = entry.getValue().getAsJsonObject();

            String dragon = entryObject.has("dragon") ? entryObject.get("dragon").getAsString() : fileID.getPath();
            boolean valid = ResourceUtil.AllowedValues.isValid(dragon, false);
            Map<String, HitboxRedirect> toPut = new HashMap<>();

            JsonArray array = entry.getValue().getAsJsonObject().get("redirects").getAsJsonArray();
            for (JsonElement elem : array) {
                JsonObject input = elem.getAsJsonObject();
                HitboxRedirect data = HitboxRedirect.CODEC.parse(JsonOps.INSTANCE, input).getOrThrow(false, (error) -> {
                    IoBVariantLoader.LOGGER.error("Failed to parse hitbox redirect data file {} correctly. Check for syntax errors and try again", fileID.toString());
                    IoBVariantLoader.LOGGER.error(error);
                });
                if (input.has("name")) {
                    JsonElement nameElem = input.getAsJsonObject().get("name");
                    List<String> names = nameElem.isJsonArray() ?
                            Util.make(new ArrayList<>(), l -> nameElem.getAsJsonArray().forEach(e -> l.add(e.getAsString()))) :
                            List.of(nameElem.getAsString());
                    names.forEach(name -> {
                        if (valid) toPut.put(name, data);
                        else IoBVariantLoader.LOGGER.warn("Hitbox redirect entry in {} for name {} does not match any dragon id and will be skipped", fileID, name);
                    });
                }
                if (input.has("collections")) {
                    Map<String, Map<String, HitboxRedirect>> collections = new HashMap<>();
                    input.getAsJsonArray("collections").forEach(collection -> {
                        VariantCollectionsUtil.getCollectionLists(collection.getAsString()).forEach(variantList -> {
                            Map<String, HitboxRedirect> speciesCollection = collections.computeIfAbsent(variantList.dragon(), (s) -> new HashMap<>());
                            variantList.variants().forEach(variant -> speciesCollection.put(variant, data));
                        });
                    });
                    collections.forEach(HitboxRedirectUtil::add);
                }
                if (!input.has("name") && input.has("collections")) {
                    IoBVariantLoader.LOGGER.warn("Hitbox redirect file {} has entries with no names or collections specified", fileID);
                }
            }
            if (valid) HitboxRedirectUtil.add(dragon, toPut);
        }
        HitboxRedirectUtil.debugPrint();
    }
}
