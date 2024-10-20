package nordmods.iobvariantloader.util.hitbox_redirect;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HitboxRedirectReloadListener extends SimpleJsonResourceReloadListener {

    public HitboxRedirectReloadListener() {
        super(new GsonBuilder().create(), "hitbox_redirects");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        HitboxRedirectUtil.dragonHitboxRedirects.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            String dragon = entry.getKey().getPath();
            Map<String, HitboxRedirect> toPut = new HashMap<>();

            JsonArray array = entry.getValue().getAsJsonObject().get("redirects").getAsJsonArray();

            for (JsonElement elem : array) {
                JsonObject input = elem.getAsJsonObject();
                String name = input.get("name").getAsString();
                Pair<Float, Float> hitbox = getHitbox(input);
                Pair<Float, Float> attackBox = getAttackBox(input);
                Vec3 attackBoxPos = getAttackBoxPos(input);
                List<Vec3> passengerPositions = getPassengerPositions(input);
                HitboxRedirect override = new HitboxRedirect(hitbox, attackBox, attackBoxPos, passengerPositions);
                toPut.put(name, override);
            }
            HitboxRedirectUtil.add(dragon, toPut);
        }
        HitboxRedirectUtil.debugPrint();
    }

    private List<Vec3> getPassengerPositions(JsonObject input) {
        List<Vec3> positions = new ArrayList<>(List.of());
        if (input.has("passenger_positions")) {
            JsonArray array = input.getAsJsonArray("passenger_positions");
            array.forEach(jsonElement -> {
                if (jsonElement instanceof JsonArray pos && pos.size() == 3) {
                    float[] coords = {0,0,0};
                    for (int i = 0; i < 3; i++) coords[i] = pos.get(i).getAsFloat();
                    positions.add(new Vec3(coords[0], coords[1], coords[2]));
                }
            });
        }
        return positions;
    }

    // /summon isleofberk:triple_stryke ~ ~ ~ {NoAI:1, VariantName:blood}
    // /summon isleofberk:triple_stryke ~ ~ ~ {NoAI:1, VariantName:snowy}
    private Pair<Float, Float> getHitbox(JsonObject input) {
        if (input.has("hitbox")) {
            JsonObject object = GsonHelper.getAsJsonObject(input, "hitbox");
            if (object.has("width") && object.has("height"))
                return new Pair<>(object.get("width").getAsFloat(), object.get("height").getAsFloat());
        }
        return null;
    }

    private Pair<Float, Float> getAttackBox(JsonObject input) {
        if (input.has("attack_box")) {
            JsonObject object = GsonHelper.getAsJsonObject(input, "attack_box");
            if (object.has("width") && object.has("height"))
                return new Pair<>(object.get("width").getAsFloat(), object.get("height").getAsFloat());
        }
        return null;
    }

    private Vec3 getAttackBoxPos(JsonObject input) {
        if (input.has("attack_box_position")) {
            JsonArray array = input.getAsJsonArray("attack_box_position");
            float[] coords = {0,0,0};
            for (int i = 0; i < 3; i++) {
                coords[i] = array.get(i).getAsFloat();
            }
            return new Vec3(coords[0], coords[1], coords[2]);
        }
        return null;
    }
}
