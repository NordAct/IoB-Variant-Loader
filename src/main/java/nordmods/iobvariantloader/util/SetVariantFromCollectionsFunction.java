package nordmods.iobvariantloader.util;

import com.GACMD.isleofberk.items.DragonEggItem;
import com.GACMD.isleofberk.registery.ModItems;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.variant_collections.VariantCollectionsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SetVariantFromCollectionsFunction extends LootItemConditionalFunction {
    private final List<String> collections;
    private final boolean replaceBaseIfDragonMismatch;
    protected SetVariantFromCollectionsFunction(LootItemCondition[] pConditions, List<String> collections, boolean replaceBaseIfDragonMismatch) {
        super(pConditions);
        this.collections = collections;
        this.replaceBaseIfDragonMismatch = replaceBaseIfDragonMismatch;
    }

    @Override
    protected @NotNull ItemStack run(@NotNull ItemStack pStack, @NotNull LootContext pContext) {
        if (!(pStack.getItem() instanceof DragonEggItem eggItem)) return pStack;

        List<Pair<String, String>> variants = new ArrayList<>();
        collections.forEach(c -> {
            VariantCollectionsUtil.getCollectionLists(c).forEach(variantList -> {
                variantList.variants().forEach(var -> variants.add(new Pair<>(variantList.dragon(), var)));
            });
        });
        if (variants.isEmpty()) {
            StringBuilder collectionNames = new StringBuilder();
            if (!collections.isEmpty()) {
                collectionNames.append(collections.get(0));
                for (int i = 1; i < collections.size(); i++) {
                    collectionNames.append(", ").append(collections.get(i));
                }
            }
            IoBVariantLoader.LOGGER.warn("Failed to assign variant from collection in function since all specified collections ({}) are empty", collectionNames);
            return pStack;
        }
        int random = pContext.getRandom().nextInt(variants.size());
        String dragon = variants.get(random).getFirst();
        String variant = variants.get(random).getSecond();
        if (!((DragonSpeciesHelper)eggItem).getSpecies(false).equals(dragon)) {
            if (replaceBaseIfDragonMismatch) {
                DragonEggItem newEgg = switch (dragon) {
                    case "deadly_nadder" -> ModItems.NADDER_EGG.get();
                    case "gronckle" -> ModItems.GRONCKLE_EGG.get();
                    case "light_fury" -> ModItems.LIGHT_FURY_EGG.get();
                    case "monstrous_nightmare" -> ModItems.MONSTROUS_NIGHTMARE_EGG.get();
                    case "night_fury" -> ModItems.NIGHT_FURY_EGG.get();
                    case "night_light" -> ModItems.NIGHT_LIGHT_EGG.get();
                    case "skrill" -> ModItems.SKRILL_EGG.get();
                    case "speed_stinger", "speed_stinger_leader" -> ModItems.SPEED_STINGER_EGG.get();
                    case "stinger" -> ModItems.STINGER_EGG.get();
                    case "terrible_terror" -> ModItems.TERRIBLE_TERROR_EGG.get();
                    case "triple_stryke" -> ModItems.TRIPLE_STRYKE_EGG.get();
                    case "zippleback" -> ModItems.ZIPPLEBACK_EGG.get();
                    default -> eggItem;
                };
                ItemStack newStack = new ItemStack(newEgg);
                newStack.setCount(pStack.getCount());
                newStack.getOrCreateTag()
                        .merge(pStack.getOrCreateTag())
                        .putString("VariantName", variant);
                return newStack;
            }
        } else {
            pStack.getOrCreateTag().putString("VariantName", variant);
        }
        return pStack;
    }

    @Override
    public LootItemFunctionType getType() {
        return IoBVariantLoader.SET_VARIANT_FROM_COLLECTION.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetVariantFromCollectionsFunction> {
        @Override
        public void serialize(JsonObject pJson, SetVariantFromCollectionsFunction pValue, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pValue, pSerializationContext);
            pJson.add("replace_base_if_dragon_mismatch", pSerializationContext.serialize(pValue.replaceBaseIfDragonMismatch));
            pJson.add("collections", pSerializationContext.serialize(pValue.collections));
        }

        @Override
        public SetVariantFromCollectionsFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            boolean replaceBaseIfDragonMismatch = GsonHelper.getAsBoolean(pObject, "replace_base_if_dragon_mismatch");
            List<String> collections = new ArrayList<>();
            GsonHelper.getAsJsonArray(pObject, "collections").forEach(c -> collections.add(c.getAsString()));
            return new SetVariantFromCollectionsFunction(pConditions, collections, replaceBaseIfDragonMismatch);
        }
    }
}
