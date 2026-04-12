package nordmods.iobvariantloader.util.variant_collections;

import nordmods.iobvariantloader.IoBVariantLoader;

import java.util.*;

public class VariantCollectionsUtil {
    // collection, variant lists
    public static final Map<String, List<VariantList>> VARIANT_COLLECTIONS = new HashMap<>();

    public static synchronized void add(String collection, VariantList list) {
        List<VariantList> content = VARIANT_COLLECTIONS.get(collection);
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
            VARIANT_COLLECTIONS.put(collection, content);
        } else VARIANT_COLLECTIONS.put(collection, new ArrayList<>(List.of(list)));
    }

    public static void debugPrint() {
        if (!IoBVariantLoader.config.logVariantLists.get()) return;
        for (Map.Entry<String, List<VariantList>> entry : VARIANT_COLLECTIONS.entrySet()) {
            StringBuilder info = new StringBuilder();
            info.append("Variants in collection \"").append(entry.getKey()).append("\": \n");
            for (VariantList list : entry.getValue()){
                for (String variant : list.variants()) {
                    info.append("- ").append(variant).append(" (").append(list.dragon()).append(") \n");
                }
            }
            IoBVariantLoader.LOGGER.info("{}", info);
        }
    }

    public static List<VariantList> getCollectionLists(String collection) {
        return VARIANT_COLLECTIONS.computeIfAbsent(collection, (s) -> new ArrayList<>());
    }

    public static List<String> getVariantCollections(String dragon, String variant) {
        List<String> collections = new ArrayList<>();
        VARIANT_COLLECTIONS.forEach((collection, lists) -> {
            for (VariantList list : lists) {
                if (!list.dragon().equals(dragon)) continue;
                for (String listedVariant : list.variants()) {
                    if (listedVariant.equals(variant)) {
                        collections.add(collection);
                        break;
                    }
                }
            }
        });
        return collections;
    }
}
