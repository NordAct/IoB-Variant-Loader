package nordmods.iobvariantloader.util.extras;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
import java.util.Map;

public class ExtrasReloadListener extends SimpleJsonResourceReloadListener {
    public ExtrasReloadListener() {
        super(new GsonBuilder().create(), "extras");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ExtrasUtil.extrasMap.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation fileID = entry.getKey();
            JsonObject entryObject = entry.getValue().getAsJsonObject();

            String dragon = entryObject.has("dragon") ? entryObject.get("dragon").getAsString() : fileID.getPath();
            if (!ResourceUtil.AllowedValues.isValid(dragon, true)) {
                IoBVariantLoader.LOGGER.warn("Extras entry {} does not match any dragon id and will be skipped", fileID);
                continue;
            }
            Map<String, Extras> toPut = new HashMap<>();

            JsonArray array = entryObject.get("extras").getAsJsonArray();
            for (JsonElement elem : array) {
                String name = elem.getAsJsonObject().get("name").getAsString();
                Extras extras = Extras.CODEC.parse(JsonOps.INSTANCE, elem).getOrThrow(false, (error) -> {
                    IoBVariantLoader.LOGGER.error("Failed to parse extras data file {} correctly. Check for syntax errors and try again", fileID.toString());
                    IoBVariantLoader.LOGGER.error(error);
                });
                toPut.put(name, extras);
            }
            ExtrasUtil.add(dragon, toPut);
        }
        ExtrasUtil.debugPrint();
    }
}
