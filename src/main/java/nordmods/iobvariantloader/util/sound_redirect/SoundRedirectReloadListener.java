package nordmods.iobvariantloader.util.sound_redirect;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ResourceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundRedirectReloadListener extends SimpleJsonResourceReloadListener {
    public SoundRedirectReloadListener() {
        super(new GsonBuilder().create(), "sound_redirects");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        SoundRedirectUtil.soundRedirectMap.clear();
        SoundRedirectUtil.clearCahce();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation fileID = entry.getKey();
            JsonObject entryObject = entry.getValue().getAsJsonObject();

            String dragon = entryObject.has("dragon") ? entryObject.get("dragon").getAsString() : fileID.getPath();
            if (!ResourceUtil.AllowedValues.isValid(dragon, true)) {
                IoBVariantLoader.LOGGER.warn("Sound redirect entry {} does not match any dragon id and will be skipped", fileID);
                continue;
            }
            Map<String, List<SoundRedirect>> toPut = new HashMap<>();

            JsonArray array = entryObject.get("redirects").getAsJsonArray();
            for (JsonElement elem : array) {
                JsonObject input = elem.getAsJsonObject();
                String name = input.get("name").getAsString();
                if (input.has("sounds")) {
                    List<SoundRedirect> soundRedirect = SoundRedirect.CODEC.listOf().parse(JsonOps.INSTANCE, input.getAsJsonArray("sounds")).getOrThrow(false, (error) -> {
                        IoBVariantLoader.LOGGER.error("Failed to parse sound redirect data file {} correctly. Check for syntax errors and try again", fileID.toString());
                        IoBVariantLoader.LOGGER.error(error);
                    });
                    toPut.put(name, soundRedirect);
                }
            }
            SoundRedirectUtil.add(dragon, toPut);
        }
        SoundRedirectUtil.debugPrint();
    }
}
