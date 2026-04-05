package nordmods.iobvariantloader.util.variant_group;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;

import java.util.ArrayList;
import java.util.List;

public class KillVariantFromGroupTrigger extends SimpleCriterionTrigger<KillVariantFromGroupTrigger.TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation(IoBVariantLoader.MOD_ID, "kill_variant_from_group");

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, EntityPredicate.Composite pPlayer, DeserializationContext pContext) {
        JsonElement jsonelement = pJson.get("groups");
        if (jsonelement instanceof JsonArray array) {
            List<String> groups = new ArrayList<>();
            array.forEach(e -> groups.add(e.getAsString()));
            return new TriggerInstance(pPlayer, groups, DamageSourcePredicate.fromJson(pJson.get("killing_blow")));
        }
        return new TriggerInstance(pPlayer, List.of(), DamageSourcePredicate.fromJson(pJson.get("killing_blow")));
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer pPlayer, ADragonBase dragon, DamageSource source) {
        trigger(pPlayer, triggerInstance ->
                VariantListUtil
                        .getVariantGroups(
                                ((DragonSpeciesHelper)dragon).getSpecies(false),
                                ((VariantNameHelper)dragon).getVariantName()
                        )
                        .stream()
                        .anyMatch(group -> triggerInstance.matches(pPlayer, group, source))
        );
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final List<String> groups;
        private final DamageSourcePredicate killingBlow;

        public TriggerInstance(EntityPredicate.Composite pPlayer, List<String> groups, DamageSourcePredicate killingBlow) {
            super(ID, pPlayer);
            this.groups = groups;
            this.killingBlow = killingBlow;
        }

        public boolean matches(ServerPlayer player, String group, DamageSource source) {
            return killingBlow.matches(player, source) && groups.stream().anyMatch(match -> match.equals(group));
        }

        @Override
        public JsonObject serializeToJson(SerializationContext pConditions) {
            JsonObject jsonobject = super.serializeToJson(pConditions);
            JsonArray array = new JsonArray();
            groups.forEach(array::add);
            jsonobject.add("groups", array);
            return jsonobject;
        }
    }
}
