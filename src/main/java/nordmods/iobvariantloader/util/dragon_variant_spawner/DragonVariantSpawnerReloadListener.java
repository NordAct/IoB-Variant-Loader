package nordmods.iobvariantloader.util.dragon_variant_spawner;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ResourceUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DragonVariantSpawnerReloadListener extends SimpleJsonResourceReloadListener {

    public DragonVariantSpawnerReloadListener() {
        super(new GsonBuilder().create(), "dragon_variants");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        DragonVariantSpawnerUtil.dragonVariants.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation fileID = entry.getKey();
            JsonObject entryObject = entry.getValue().getAsJsonObject();

            String dragon = entryObject.has("dragon") ? entryObject.get("dragon").getAsString() : fileID.getPath();
            if (!ResourceUtil.AllowedValues.isValid(dragon, false)) {
                IoBVariantLoader.LOGGER.warn("Variant spawns entry {} does not match any dragon id and will be skipped", fileID);
                continue;
            }
            DataResult<List<DragonVariantSpawner>> result = DragonVariantSpawner.CODEC.listOf().parse(JsonOps.INSTANCE, entryObject.get("variants"));
            List<DragonVariantSpawner> variants = result.getOrThrow(false, (error) -> {
                IoBVariantLoader.LOGGER.error("Failed to parse dragon variant spawner data file {} correctly. Check for syntax errors and try again", fileID.toString());
                IoBVariantLoader.LOGGER.error(error);
            });
            DragonVariantSpawnerUtil.add(dragon, variants);
        }
        DragonVariantSpawnerUtil.debugPrint();
    }
}
