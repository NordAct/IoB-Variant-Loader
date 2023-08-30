package nordmods.iobvariantloader.util;

import nordmods.iobvariantloader.IoBVariantLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DragonVariantUtil {
    public static final Map<String, List<DragonVariant>> dragonVariantHolder = new HashMap<>();

    public static List<DragonVariant> getVariants(String name) {
        return dragonVariantHolder.get(name);
    }

    public static void reset() {
        dragonVariantHolder.clear();
    }

    public static void add(String name, List<DragonVariant> variants) {
        dragonVariantHolder.put(name, variants);
    }

    public static void debugPrint() {
        for (Map.Entry<String, List<DragonVariant>> entry : dragonVariantHolder.entrySet()) {
            for ( DragonVariant variant : entry.getValue()) {
                IoBVariantLoader.LOGGER.debug("{}: variant {} was loaded", entry.getKey(), variant);
            }
        }
    }
}
