package nordmods.iobvariantloader.util.variant_group;

import nordmods.iobvariantloader.IoBVariantLoader;

import java.util.*;

public class VariantListUtil {
    // group, variant lists
    public static final Map<String, List<VariantList>> VARIANT_GROUPS = new HashMap<>();

    public static synchronized void add(String group, VariantList list) {
        List<VariantList> content = VARIANT_GROUPS.get(group);
        if (content != null) {
            VariantList sameDragonList = content.stream().filter(variantList -> variantList.dragon().equals(list.dragon())).findFirst().orElse(null);
            if (sameDragonList != null) {
                Set<String> variants = new HashSet<>(sameDragonList.variants());
                variants.addAll(list.variants());
                content.remove(sameDragonList);
                content.add(new VariantList(sameDragonList.dragon(),  variants.stream().toList()));
            } else {
                content.add(list);
            }
            VARIANT_GROUPS.put(group, content);
        } else VARIANT_GROUPS.put(group, new ArrayList<>(List.of(list)));
    }

    public static void debugPrint() {
        if (!IoBVariantLoader.config.logVariantLists.get()) return;
        for (Map.Entry<String, List<VariantList>> entry : VARIANT_GROUPS.entrySet()) {
            StringBuilder info = new StringBuilder();
            info.append("Variants in group \"").append(entry.getKey()).append("\": \n");
            for (VariantList list : entry.getValue()){
                for (String variant : list.variants()) {
                    info.append("- ").append(variant).append(" (").append(list.dragon()).append(") \n");
                }
            }
            IoBVariantLoader.LOGGER.info("{}", info);
        }
    }

    public static List<VariantList> getGroupLists(String group) {
        return VARIANT_GROUPS.get(group);
    }

    public static List<String> getVariantGroups(String dragon, String variant) {
        List<String> groups = new ArrayList<>();
        VARIANT_GROUPS.forEach((group, lists) -> {
            for (VariantList list : lists) {
                if (!list.dragon().equals(dragon)) continue;
                for (String listedVariant : list.variants()) {
                    if (listedVariant.equals(variant)) {
                        groups.add(group);
                        break;
                    }
                }
            }
        });
        return groups;
    }
}
