package nordmods.iobvariantloader.util.variant_group;

import nordmods.iobvariantloader.IoBVariantLoader;

import java.util.*;

public class VariantListUtil {
    // group, variant lists
    public static final Map<String, List<VariantList>> variantGroupsMap = new HashMap<>();

    public static synchronized void add(String group, VariantList list) {
        List<VariantList> content = variantGroupsMap.get(group);
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
            variantGroupsMap.put(group, content);
        } else variantGroupsMap.put(group, new ArrayList<>(List.of(list)));
    }

    public static void debugPrint() {
        if (!IoBVariantLoader.config.logVariantLists.get()) return;
        for (Map.Entry<String, List<VariantList>> entry : variantGroupsMap.entrySet()) {
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
}
