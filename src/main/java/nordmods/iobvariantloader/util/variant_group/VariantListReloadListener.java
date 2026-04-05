package nordmods.iobvariantloader.util.variant_group;

import com.google.gson.GsonBuilder;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VariantListReloadListener extends SimpleJsonResourceReloadListener {
    public VariantListReloadListener() {
        super(new GsonBuilder().create(), "variant_groups");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        VariantListUtil.VARIANT_GROUPS.clear();
        Map<String, Set<String>> groupsToInclude = new HashMap<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation fileID = entry.getKey();
            JsonObject entryObject = entry.getValue().getAsJsonObject();


            if (!entryObject.has("group_name")) {
                IoBVariantLoader.LOGGER.error("Variant list {} has no group specified and will be skipped", fileID);
                continue;
            }
            String group = entryObject.get("group_name").getAsString();


            if (entryObject.has("variant_lists")) {
                for (JsonElement elem : entryObject.get("variant_lists").getAsJsonArray()) {
                    VariantList list = VariantList.CODEC.parse(JsonOps.INSTANCE, elem).getOrThrow(false, error -> {
                        IoBVariantLoader.LOGGER.error("Failed to parse variant list in {} correctly. Check for syntax errors and try again", fileID.toString());
                        IoBVariantLoader.LOGGER.error(error);
                    });
                    String dragon = list.dragon();
                    if (!ResourceUtil.AllowedValues.isValid(dragon, false)) {
                        IoBVariantLoader.LOGGER.warn("Variant list entry of {} with dragon id {} does not match any existing dragon id and will be skipped", fileID, dragon);
                        continue;
                    }
                    VariantListUtil.add(group, list);
                }
            }

            if (entryObject.has("included_groups")) {
                Set<String> includedGroups = groupsToInclude.computeIfAbsent(group, (g) -> new HashSet<>());
                entryObject.get("included_groups").getAsJsonArray().forEach(g -> includedGroups.add(g.getAsString()));
            }
        }
        groupsToInclude.forEach((group, included) -> addIncludedGroupsToGroup(group, included, groupsToInclude));
        VariantListUtil.debugPrint();
    }

    private void addIncludedGroupsToGroup(String group, Set<String> includedGroups, Map<String, Set<String>> groupInGroup) {
        includedGroups.forEach(includedGroup -> {
            if (includedGroup.equals(group)) return;

            if (groupInGroup.containsKey(includedGroup))
                addIncludedGroupsToGroup(group, groupInGroup.get(includedGroup), groupInGroup);

            if (VariantListUtil.VARIANT_GROUPS.containsKey(includedGroup))
                VariantListUtil.VARIANT_GROUPS.get(includedGroup).forEach(includedList -> VariantListUtil.add(group, includedList));
        });
    }
}
