package nordmods.iobvariantloader.util.modelRedirect;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModelRedirectReloadListener extends SimplePreparableReloadListener<Map<String, Map<String, ModelRedirect>>> {

    @Override
    protected synchronized Map<String, Map<String, ModelRedirect>> prepare(ResourceManager manager, ProfilerFiller profiler) {
        ModelRedirectUtil.dragonModelRedirects.clear();
        Collection<ResourceLocation> resourceCollection = manager.listResources("model_redirects", path -> path.endsWith(".json"));
        for (ResourceLocation id : resourceCollection) {
            String path = id.getPath();
            String dragon = path.substring(path.lastIndexOf("/") + 1, path.indexOf(".json"));

            Map<String, ModelRedirect> map = new HashMap<>();
            try (InputStream stream = manager.getResource(id).getInputStream()) {

                InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    JsonElement element = JsonParser.parseReader(bufferedReader);
                    JsonArray array = GsonHelper.getAsJsonArray((JsonObject) element, "redirects");
                    for (JsonElement elem : array) {
                        JsonObject input = elem.getAsJsonObject();
                        String name = input.get("name").getAsString();

                        String model = input.has("model") ? input.get("model").getAsString() : null;
                        String animation = input.has("animation") ? input.get("animation").getAsString() : null;

                        if (!map.containsKey(name)) map.put(name, new ModelRedirect(model, animation));
                    }
                } catch (JsonIOException e) {
                    IoBVariantLoader.LOGGER.error("Failed to read json " + id, e);
                }
                ModelRedirectUtil.add(dragon, map);

            } catch (Exception e) {
                IoBVariantLoader.LOGGER.error("Error occurred while loading resource json " + id, e);
            }
        }
        ModelRedirectUtil.debugPrint();
        return ModelRedirectUtil.dragonModelRedirects;
    }

    @Override
    protected void apply(Map<String, Map<String, ModelRedirect>> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {

    }
}
