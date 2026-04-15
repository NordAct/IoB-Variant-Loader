package nordmods.iobvariantloader.util.breeding_list;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.iobvariantloader.IoBVariantLoader;

import java.util.Map;

public class BreedingListReloadListener extends SimpleJsonResourceReloadListener {
    public BreedingListReloadListener() {
        super(new GsonBuilder().create(), "breeding_lists");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        BreedingListUtil.BREEDING_LISTS.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation fileID = entry.getKey();
            JsonObject entryObject = entry.getValue().getAsJsonObject();

            BreedingList list = BreedingList.CODEC.parse(JsonOps.INSTANCE, entryObject).getOrThrow(false, error -> {
                IoBVariantLoader.LOGGER.error("Failed to parse variant list in {} correctly. Check for syntax errors and try again", fileID.toString());
                IoBVariantLoader.LOGGER.error(error);
            });
            BreedingListUtil.BREEDING_LISTS.add(list);
        }
        BreedingListUtil.debugPrint();
    }
}
