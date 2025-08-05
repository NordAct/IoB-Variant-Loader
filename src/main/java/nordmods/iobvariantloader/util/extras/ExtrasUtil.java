package nordmods.iobvariantloader.util.extras;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ExtrasUtil { //todo docs
    //dragon, <variant, extras>
    public static final Map<String, Map<String, Extras>> extrasMap = new HashMap<>();


    public static synchronized void add(String dragon, Map<String, Extras> extras) {
        Map<String, Extras> content = extrasMap.get(dragon);
        if (content != null) {
            content.putAll(extras);
            extrasMap.put(dragon, content);
        } else extrasMap.put(dragon, extras);
    }

    @Nullable
    public static String getLootTableRedirect(String dragon, String variant) {
        if (extrasMap.containsKey(dragon)){
            Map<String, Extras> extras = extrasMap.get(dragon);
            if (extras.containsKey(variant)) return extras.get(variant).lootTableRedirect().orElse(null);
        }
        return null;
    }

    @Nullable
    public static String getVariandGroup(String dragon, String variant) {
        if (extrasMap.containsKey(dragon)){
            Map<String, Extras> extras = extrasMap.get(dragon);
            if (extras.containsKey(variant)) return extras.get(variant).variantGroup().orElse(null);
        }
        return null;
    }
}
