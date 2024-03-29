package nordmods.iobvariantloader.util.model_redirect;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ModelRedirectReloadListener extends SimpleJsonResourceReloadListener {
    public ModelRedirectReloadListener() {
        super(new GsonBuilder().create(), "model_redirects");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager manager, @NotNull ProfilerFiller pProfiler) {
        ModelRedirectUtil.dragonModelRedirects.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            String dragon = entry.getKey().getPath();
            Map<String, ModelRedirect> toPut = new HashMap<>();

            JsonArray array = entry.getValue().getAsJsonObject().get("redirects").getAsJsonArray();

            for (JsonElement elem : array) {
                JsonObject input = elem.getAsJsonObject();
                String name = input.get("name").getAsString();
                String model = input.has("model") ? input.get("model").getAsString() : null;
                String animation = input.has("animation") ? input.get("animation").getAsString() : null;
                String saddle = input.has("saddle") ? input.get("saddle").getAsString() : null;
                //noinspection SimplifiableConditionalExpression
                boolean nameTagAccessible = input.has("nametag_accessible") ? input.get("nametag_accessible").getAsBoolean() : true;
                if (!toPut.containsKey(name)) toPut.put(name, new ModelRedirect(model, animation, saddle, nameTagAccessible));
            }

            ModelRedirectUtil.add(dragon, toPut);
        }
        ModelRedirectUtil.debugPrint();
    }
}
