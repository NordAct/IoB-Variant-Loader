package nordmods.iobvariantloader.util.extras;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import nordmods.iobvariantloader.IoBVariantLoader;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ExtrasUtil {
    //dragon, <variant, extras>
    public static final Map<String, Map<String, Extras>> EXTRAS = new HashMap<>();

    public static synchronized void add(String dragon, Map<String, Extras> extras) {
        Map<String, Extras> content = EXTRAS.get(dragon);
        if (content != null) {
            extras.forEach((variant, extra) -> {
                if (content.containsKey(variant)){
                    Extras existing = content.get(variant);
                    Extras merged = new Extras(
                            extra.variantGroup().isPresent() ? extra.variantGroup() : existing.variantGroup(),
                            extra.lootTableRedirect().isPresent() ? extra.lootTableRedirect() : existing.lootTableRedirect(),
                            Optional.of(Util.make(new ArrayList<>(), list -> {
                                list.addAll(extra.variantAttributeModifiers().orElse(List.of()));
                                list.addAll(existing.variantAttributeModifiers().orElse(List.of()));
                            })),
                            Optional.of(new Extras.ItemRestriction(
                                    Util.make(new ArrayList<>(), list -> {
                                        list.addAll(extra
                                                .tamingItems()
                                                .orElse(Extras.ItemRestriction.dummy())
                                                .itemsById()
                                        );
                                        list.addAll(extra
                                                .tamingItems()
                                                .orElse(Extras.ItemRestriction.dummy())
                                                .itemsById()
                                        );
                                    }),
                                    Util.make(new ArrayList<>(), list -> {
                                        list.addAll(extra
                                                .tamingItems()
                                                .orElse(Extras.ItemRestriction.dummy())
                                                .itemsByTag()
                                        );
                                        list.addAll(existing
                                                .tamingItems()
                                                .orElse(Extras.ItemRestriction.dummy())
                                                .itemsByTag()
                                        );
                                    })
                            )),
                            Optional.of(new Extras.ItemRestriction(
                                    Util.make(new ArrayList<>(), list -> {
                                        list.addAll(extra
                                                .breedingItems()
                                                .orElse(Extras.ItemRestriction.dummy())
                                                .itemsById()
                                        );
                                        list.addAll(existing
                                                .breedingItems()
                                                .orElse(Extras.ItemRestriction.dummy())
                                                .itemsById()
                                        );
                                    }),
                                    Util.make(new ArrayList<>(), list -> {
                                        list.addAll(extra
                                                .breedingItems()
                                                .orElse(Extras.ItemRestriction.dummy())
                                                .itemsByTag()
                                        );
                                        list.addAll(existing
                                                .breedingItems()
                                                .orElse(Extras.ItemRestriction.dummy())
                                                .itemsByTag()
                                        );
                                    })
                            )),
                            Optional.of(Util.make(new ArrayList<>(), list -> {
                                list.addAll(extra.customItemInteractions().orElse(List.of()));
                                list.addAll(existing.customItemInteractions().orElse(List.of()));
                            }))
                    );
                    content.put(variant, merged);
                } else {
                    content.put(variant, extra);
                }
            });
            EXTRAS.put(dragon, content);
        } else EXTRAS.put(dragon, extras);
    }

    @Nullable
    public static ResourceLocation getLootTableRedirect(String dragon, String variant) {
        if (EXTRAS.containsKey(dragon)){
            Map<String, Extras> extras = EXTRAS.get(dragon);
            if (extras.containsKey(variant)) return extras.get(variant).lootTableRedirect().orElse(null);
        }
        return null;
    }

    @Nullable
    public static String getVariandGroup(String dragon, String variant) {
        if (EXTRAS.containsKey(dragon)){
            Map<String, Extras> extras = EXTRAS.get(dragon);
            if (extras.containsKey(variant)) return extras.get(variant).variantGroup().orElse(null);
        }
        return null;
    }

    public static void debugPrint() {
        if (!IoBVariantLoader.config.logExtras.get()) return;
        for (Map.Entry<String, Map<String, Extras>> entry : EXTRAS.entrySet()) {
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

                extra.customItemInteractions().ifPresent(customItemInteractions -> {
                    info.append("Custom Item Interactions: \n");
                    customItemInteractions.forEach(interaction -> {
                        info.append("- Items: ").append("\n");
                        List<ResourceLocation> itemsById = interaction.items().itemsById();
                        List<ResourceLocation> itemsByTag = interaction.items().itemsByTag();
                        if (!itemsById.isEmpty()) {
                            info.append("-- Items by ID: ");
                            itemsById.forEach(id -> info.append(id.toString()).append(" "));
                            info.append("\n");
                        }
                        if (!itemsByTag.isEmpty()) {
                            info.append("-- Items by tag: ");
                            itemsByTag.forEach(id -> info.append(id.toString()).append(" "));
                            info.append("\n");
                        }
                        info.append("- Required Amount: ").append(interaction.requiredAmount()).append("\n");
                        info.append("- Consume on Use: ").append(interaction.consumeOnUse()).append("\n");
                        info.append("- Can Interact with Untamed: ").append(interaction.canInteractWithUntamed()).append("\n");
                        info.append("- Can Interact if Not Owner: ").append(interaction.canInteractIfNotOwner()).append("\n");
                        info.append("- Commands: ").append("\n");
                        interaction.commands().forEach(command -> {
                            info.append("-- Executed command: ").append(command.executedCommand()).append("\n");
                            info.append("-- Executor: ").append(command.executor()).append("\n");
                            info.append("\n");
                        });

                        info.append("\n");
                    });
                });
                IoBVariantLoader.LOGGER.info("{}: variant {} was redirected to:\n{}", entry.getKey(), extrasEntry.getKey(), info);
            }
        }
    }

    @Nullable
    public static List<Extras.VariantAttributeModifier> getVariantAttributeModifiers(String dragon, String variant) {
        if (EXTRAS.containsKey(dragon)){
            Map<String, Extras> extras = EXTRAS.get(dragon);
            if (extras.containsKey(variant)) return extras.get(variant).variantAttributeModifiers().orElse(null);
        }
        return null;
    }

    @Nullable
    public static Boolean isTamingItem(String dragon, String variant, ItemStack stack) {
        if (EXTRAS.containsKey(dragon)){
            Map<String, Extras> extras = EXTRAS.get(dragon);
            if (extras.containsKey(variant) && extras.get(variant).tamingItems().isPresent()) {
                return isItemInList(extras.get(variant).tamingItems().get(), stack);
            }
        }
        return null;
    }

    @Nullable
    public static Boolean isBreedingItem(String dragon, String variant, ItemStack stack) {
        if (EXTRAS.containsKey(dragon)){
            Map<String, Extras> extras = EXTRAS.get(dragon);
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

    public static List<Extras.CustomItemInteraction> getCustomItemInteractions(String dragon, String variant, ItemStack itemStack) {
        if (EXTRAS.containsKey(dragon)) {
            Map<String, Extras> extras = EXTRAS.get(dragon);
            if (extras.containsKey(variant) && extras.get(variant).customItemInteractions().isPresent()) {
                return extras.get(variant)
                        .customItemInteractions()
                        .get()
                        .stream()
                        .filter(customItemInteraction -> {
                            if (customItemInteraction.requiredAmount() > itemStack.getCount()) return false;
                            return isItemInList(customItemInteraction.items(), itemStack);
                        })
                        .toList();
            }
        }
        return List.of();
    }
}
