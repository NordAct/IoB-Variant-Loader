package nordmods.iobvariantloader.util.model_redirect;

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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ModelRedirectReloadListener extends SimpleJsonResourceReloadListener {
    public ModelRedirectReloadListener() {
        super(new GsonBuilder().create(), "model_redirects");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager manager, @NotNull ProfilerFiller pProfiler) {
        ModelRedirectUtil.DRAGON_MODEL_REDIRECTS.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation fileID = entry.getKey();
            JsonObject entryObject = entry.getValue().getAsJsonObject();

            String dragon = entryObject.has("dragon") ? entryObject.get("dragon").getAsString() : fileID.getPath();
            if (!ResourceUtil.AllowedValues.isValid(dragon, true)) {
                IoBVariantLoader.LOGGER.warn("Model redirect entry {} does not match any dragon id and will be skipped", fileID);
                continue;
            }
            Map<String, ModelRedirect> toPut = new HashMap<>();

            JsonArray array = entryObject.get("redirects").getAsJsonArray();
            for (JsonElement elem : array) {
                JsonObject input = elem.getAsJsonObject();
                String name = input.get("name").getAsString();
                ModelRedirect modelRedirect = ModelRedirect.CODEC.parse(JsonOps.INSTANCE, elem).getOrThrow(false, (error) -> {
                    IoBVariantLoader.LOGGER.error("Failed to parse model redirect data file {} correctly. Check for syntax errors and try again", fileID.toString());
                    IoBVariantLoader.LOGGER.error(error);
                });

                toPut.put(name, modelRedirect);
            }
            ModelRedirectUtil.add(dragon, toPut);
        }
        ModelRedirectUtil.debugPrint();
    }
}
