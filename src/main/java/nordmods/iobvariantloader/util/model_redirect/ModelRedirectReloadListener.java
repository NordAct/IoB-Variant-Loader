package nordmods.iobvariantloader.util.model_redirect;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.TranslatableComponent;
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
                String texture = input.has("texture") ? input.get("texture").getAsString() : null;
                String model = input.has("model") ? input.get("model").getAsString() : null;
                String animation = input.has("animation") ? input.get("animation").getAsString() : null;
                String saddle = input.has("saddle") ? input.get("saddle").getAsString() : null;

                String babyTexture = input.has("baby_texture") ? input.get("baby_texture").getAsString() : null;
                String babyModel = input.has("baby_model") ? input.get("baby_model").getAsString() : null;
                String babyAnimation = input.has("baby_animation") ? input.get("baby_animation").getAsString() : null;
                String babySaddle = input.has("baby_saddle") ? input.get("baby_saddle").getAsString() : null;
                //noinspection SimplifiableConditionalExpression
                boolean nameTagAccessible = input.has("nametag_accessible") ? input.get("nametag_accessible").getAsBoolean() : true;

                String eggModel = input.has("egg_model") ? input.get("egg_model").getAsString() : null;
                String eggTexture = input.has("egg_texture") ? input.get("egg_texture").getAsString() : null;

                TranslatableComponent eggItemName = input.has("egg_item_name") ? new TranslatableComponent(input.get("egg_item_name").getAsString()) : null;
                TranslatableComponent eggName = input.has("egg_name") ? new TranslatableComponent(input.get("egg_name").getAsString()) : null;
                TranslatableComponent dragonName = input.has("dragon_name") ? new TranslatableComponent(input.get("dragon_name").getAsString()) : null;
                ModelRedirect modelRedirect = new ModelRedirect(texture, model, animation, saddle, babyTexture, babyModel, babyAnimation, babySaddle, eggModel, eggTexture, eggItemName, eggName, dragonName, nameTagAccessible);
                toPut.put(name, modelRedirect);
            }
            ModelRedirectUtil.add(dragon, toPut);
        }
        ModelRedirectUtil.debugPrint();
    }
}
