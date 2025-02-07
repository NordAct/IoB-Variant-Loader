package nordmods.iobvariantloader.util.hitbox_redirect;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HitboxRedirectUtil {
    //key - dragon id
    //value - redirect per variant
    public static final Map<String, Map<String, HitboxRedirect>> dragonHitboxRedirects = new HashMap<>();

    public static synchronized void add(String dragon, Map<String, HitboxRedirect> overrides) {
        Map<String, HitboxRedirect> content = dragonHitboxRedirects.get(dragon);
        if (content != null) {
            content.putAll(overrides);
            dragonHitboxRedirects.put(dragon, content);
        } else dragonHitboxRedirects.put(dragon, overrides);
    }

    public static void debugPrint() {
        if (!IoBVariantLoader.config.logHitboxRedirects.get()) return;
        for (Map.Entry<String, Map<String, HitboxRedirect>> entry : dragonHitboxRedirects.entrySet()) {
            for (Map.Entry<String, HitboxRedirect> overrideEntry : entry.getValue().entrySet()) {
                HitboxRedirect hitboxRedirect = overrideEntry.getValue();

                StringBuilder hitboxRedirectInfo = new StringBuilder();
                if (hitboxRedirect.hitbox() != null) {
                    hitboxRedirectInfo.append("Hitbox: ").append("\n");
                    hitboxRedirectInfo.append("- Width: ").append(hitboxRedirect.hitbox().getFirst()).append("\n");
                    hitboxRedirectInfo.append("- Height: ").append(hitboxRedirect.hitbox().getSecond()).append("\n");
                }

                if (hitboxRedirect.attackBox() != null) {
                    hitboxRedirectInfo.append("Attack Box: ").append("\n");
                    hitboxRedirectInfo.append("- Width: ").append(hitboxRedirect.attackBox().getFirst()).append("\n");
                    hitboxRedirectInfo.append("- Height: ").append(hitboxRedirect.attackBox().getSecond()).append("\n");
                }

                if (hitboxRedirect.attackBoxPos() != null) {
                    Vec3 pos = hitboxRedirect.attackBoxPos();
                    hitboxRedirectInfo.append("Attack Box Position: ")
                            .append("(x: ").append(pos.x())
                            .append(", y: ").append(pos.y())
                            .append(", z: ").append(pos.z())
                            .append(")");
                    hitboxRedirectInfo.append("\n");
                }

                if (!hitboxRedirect.passengerPositions().isEmpty()) {
                    hitboxRedirectInfo.append("Passenger Positions:");
                    hitboxRedirect.passengerPositions().forEach(pos -> {
                        hitboxRedirectInfo.append(" ")
                                .append("(x: ").append(pos.x())
                                .append(", y: ").append(pos.y())
                                .append(", z: ").append(pos.z())
                                .append(")");
                    });
                }
                hitboxRedirectInfo.append("\n");

                IoBVariantLoader.LOGGER.info("{}: variant {} got following hitbox redirects:\n{}", entry.getKey(), overrideEntry.getKey(), hitboxRedirectInfo);
            }
        }
    }

    @Nullable
    public static EntityDimensions getHitboxOverride(ADragonBase dragon) {
        String species = ((DragonSpeciesHelper)dragon).getSpecies(false);
        String variant = ((VariantNameHelper)dragon).getVariantName();
        if (dragonHitboxRedirects.containsKey(species)) {
            Map<String, HitboxRedirect> speciesMap = dragonHitboxRedirects.get(species);
            if (speciesMap.containsKey(variant)) {
                Pair<Float, Float> pair = speciesMap.get(variant).hitbox();
                if (pair == null) return null;
                return EntityDimensions.scalable(pair.getFirst(), pair.getSecond());
            }
        }
        return null;
    }

    @Nullable
    public static EntityDimensions getAttackBoxOverride(ADragonBase dragon) {
        String species = ((DragonSpeciesHelper)dragon).getSpecies(false);
        String variant = ((VariantNameHelper)dragon).getVariantName();
        if (dragonHitboxRedirects.containsKey(species)) {
            Map<String, HitboxRedirect> speciesMap = dragonHitboxRedirects.get(species);
            if (speciesMap.containsKey(variant)) {
                Pair<Float, Float> pair = speciesMap.get(variant).attackBox();
                if (pair == null) return null;
                return EntityDimensions.scalable(pair.getFirst(), pair.getSecond());
            }
        }
        return null;
    }

    @Nullable
    public static Vec3 getAttackBoxPos(ADragonBase dragon) {
        String species = ((DragonSpeciesHelper)dragon).getSpecies(false);
        String variant = ((VariantNameHelper)dragon).getVariantName();
        if (dragonHitboxRedirects.containsKey(species)) {
            Map<String, HitboxRedirect> speciesMap = dragonHitboxRedirects.get(species);
            if (speciesMap.containsKey(variant)) return speciesMap.get(variant).attackBoxPos();
        }
        return null;
    }

    public static List<Vec3> getPassengerPositions(ADragonBase dragon) {
        String species = ((DragonSpeciesHelper)dragon).getSpecies(false);
        String variant = ((VariantNameHelper)dragon).getVariantName();
        if (dragonHitboxRedirects.containsKey(species)) {
            Map<String, HitboxRedirect> speciesMap = dragonHitboxRedirects.get(species);
            if (speciesMap.containsKey(variant)) return speciesMap.get(variant).passengerPositions();
        }
        return List.of();
    }
}
