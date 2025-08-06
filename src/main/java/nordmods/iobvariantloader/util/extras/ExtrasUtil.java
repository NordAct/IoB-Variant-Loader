package nordmods.iobvariantloader.util.extras;

import nordmods.iobvariantloader.IoBVariantLoader;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtrasUtil {
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

    public static void debugPrint() {
        if (!IoBVariantLoader.config.logExtras.get()) return;
        for (Map.Entry<String, Map<String, Extras>> entry : extrasMap.entrySet()) {
            for (Map.Entry<String, Extras> extrasEntry : entry.getValue().entrySet()){
                StringBuilder info = new StringBuilder();
                Extras extra = extrasEntry.getValue();
                extra.variantGroup().ifPresent(c -> info.append("Variant Group: ").append(c).append("\n"));
                extra.lootTableRedirect().ifPresent(c -> info.append("Loot Table Redirect: ").append(c).append("\n"));
                extra.variantAttributeModifiers().ifPresent(c -> {
                    info.append("Attribute modifiers: ").append("\n");
                    c.forEach(modifier -> {
                        info.append("- Id: ").append(modifier.id()).append("\n");
                        info.append("- Amount: ").append(modifier.amount()).append("\n");
                        info.append("- Operation: ").append(modifier.operation()).append("\n");
                        info.append("\n");
                    });
                });
                IoBVariantLoader.LOGGER.info("{}: variant {} was redirected to:\n{}", entry.getKey(), extrasEntry.getKey(), info);
            }
        }
    }

    @Nullable
    public static List<Extras.VariantAttributeModifier> getVariantAttributeModifiers(String dragon, String variant) {
        if (extrasMap.containsKey(dragon)){
            Map<String, Extras> extras = extrasMap.get(dragon);
            if (extras.containsKey(variant)) return extras.get(variant).variantAttributeModifiers().orElse(null);
        }
        return null;
    }
}
