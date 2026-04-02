package nordmods.iobvariantloader.util.extras;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
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
    public static ResourceLocation getLootTableRedirect(String dragon, String variant) {
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
                    info.append("Attribute Modifiers: ").append("\n");
                    c.forEach(modifier -> {
                        info.append("- Id: ").append(modifier.id()).append("\n");
                        info.append("- Amount: ").append(modifier.amount()).append("\n");
                        info.append("- Operation: ").append(modifier.operation()).append("\n");
                        info.append("\n");
                    });
                });

                extra.breedingItems().ifPresent(items -> {
                    info.append("Breeding Items: \n");
                    List<ResourceLocation> itemsById = items.itemsById();
                    List<ResourceLocation> itemsByTag = items.itemsByTag();
                    if (!itemsById.isEmpty()) {
                        info.append("- Items by ID: ");
                        itemsById.forEach(id -> info.append(id.toString()).append(" "));
                        info.append("\n");
                    }
                    if (!itemsByTag.isEmpty()) {
                        info.append("- Items by tag: ");
                        itemsByTag.forEach(id -> info.append(id.toString()).append(" "));
                        info.append("\n");
                    }
                });

                extra.tamingItems().ifPresent(items -> {
                    info.append("Taming Items: \n");
                    List<ResourceLocation> itemsById = items.itemsById();
                    List<ResourceLocation> itemsByTag = items.itemsByTag();
                    if (!itemsById.isEmpty()) {
                        info.append("- Items by ID: ");
                        itemsById.forEach(id -> info.append(id.toString()).append(" "));
                        info.append("\n");
                    }
                    if (!itemsByTag.isEmpty()) {
                        info.append("- Items by tag: ");
                        itemsByTag.forEach(id -> info.append(id.toString()).append(" "));
                        info.append("\n");
                    }
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

    @Nullable
    public static Boolean isTamingItem(String dragon, String variant, ItemStack stack) {
        if (extrasMap.containsKey(dragon)){
            Map<String, Extras> extras = extrasMap.get(dragon);
            if (extras.containsKey(variant) && extras.get(variant).tamingItems().isPresent()) {
                return isItemInList(extras.get(variant).tamingItems().get(), stack);
            }
        }
        return null;
    }

    @Nullable
    public static Boolean isBreedingItem(String dragon, String variant, ItemStack stack) {
        if (extrasMap.containsKey(dragon)){
            Map<String, Extras> extras = extrasMap.get(dragon);
            if (extras.containsKey(variant) && extras.get(variant).breedingItems().isPresent()) {
                return isItemInList(extras.get(variant).breedingItems().get(), stack);
            }
        }
        return null;
    }

    private static boolean isItemInList(Extras.ItemRestriction restriction, ItemStack stack) {
        boolean isIn = false;
        for (ResourceLocation item : restriction.itemsById()) {
            if (stack.getItem().builtInRegistryHolder().key().location().equals(item)) {
                isIn = true;
                break;
            }
        }

        if (!isIn) for (ResourceLocation tag : restriction.itemsByTag()) {
            if (stack.is(TagKey.create(Registry.ITEM_REGISTRY, tag))) {
                isIn = true;
                break;
            }
        }

        return isIn;
    }
}
