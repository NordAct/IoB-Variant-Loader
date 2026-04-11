package nordmods.iobvariantloader.util.variant_collections;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;

import java.util.ArrayList;
import java.util.List;

public class TameVariantFromCollectionTrigger extends SimpleCriterionTrigger<TameVariantFromCollectionTrigger.TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation(IoBVariantLoader.MOD_ID, "tame_variant_from_collection");

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, EntityPredicate.Composite pPlayer, DeserializationContext pContext) {
        JsonElement jsonelement = pJson.get("collections");
        if (jsonelement instanceof JsonArray array) {
            List<String> collections = new ArrayList<>();
            array.forEach(e -> collections.add(e.getAsString()));
            return new TriggerInstance(pPlayer, collections);
        }
        return new TriggerInstance(pPlayer, List.of());
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer pPlayer, ADragonBase dragon) {
        trigger(pPlayer, triggerInstance ->
                VariantCollectionsUtil
                        .getVariantCollections(
                                ((DragonSpeciesHelper)dragon).getSpecies(false),
                                ((VariantNameHelper)dragon).getVariantName()
                        )
                        .stream()
                        .anyMatch(triggerInstance::matches)
        );
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final List<String> collections;

        public TriggerInstance(EntityPredicate.Composite pPlayer, List<String> collections) {
            super(ID, pPlayer);
            this.collections = collections;
        }

        public boolean matches(String collection) {
            return collections.stream().anyMatch(match -> match.equals(collection));
        }

        @Override
        public JsonObject serializeToJson(SerializationContext pConditions) {
            JsonObject jsonobject = super.serializeToJson(pConditions);
            JsonArray array = new JsonArray();
            collections.forEach(array::add);
            jsonobject.add("collections", array);
            return jsonobject;
        }
    }
}
