package nordmods.iobvariantloader.util.sound_redirect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;
import nordmods.iobvariantloader.util.model_redirect.ModelRedirectUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//todo make it in datapack and packet for client sync
public class SoundRedirectUtil {
    //hardcoded sound names
    public static final String BITE = "bite"; //bite attack sound
    public static final String STING = "sting"; //sting attack sound
    public static final String FIRE = "fire"; //shot/fire attack sound
    public static final String TAME = "tame"; //dragon tame sound
    public static final String SLEEP = "sleep"; //dragon sleep sound
    public static final String GROWL = "growl"; //dragon idle sound
    public static final String FLAP = "flap"; //dragon wing flap sound
    public static final String HURT = "hurt"; //dragon hurt sound
    public static final String DEATH = "death"; //dragon hurt sound
    public static final String STEP = "step"; //dragon step sound

    //sound cache
    //dragon, <variant, <sound, sound info>>
    private static final Map<String, Map<String, Map<String, SoundInfo>>> soundCache = new HashMap<>();
    public static final Map<String, Map<String, List<SoundRedirect>>> soundRedirectMap = new HashMap<>();





    public static <T extends Entity & VariantNameHelper & DragonSpeciesHelper> boolean playSound(@Nullable Player player, T entity, String sound) {
        SoundRedirectUtil.SoundInfo soundInfo = null;
        if (entity.hasCustomName() && ModelRedirectUtil.isNametagAccessible(entity.getSpecies(true), entity.getName().getString().toLowerCase())) {
            soundInfo = SoundRedirectUtil.getSoundInfo(entity.getSpecies(true), entity.getName().getString().toLowerCase(), sound);
        }
        if (soundInfo == null) {
            soundInfo = SoundRedirectUtil.getSoundInfo(entity.getSpecies(true), entity.getVariantName(), sound);
        }
        if (soundInfo != null) {
            entity.level.playSound(player, entity, new SoundEvent(soundInfo.id()), entity.getSoundSource(), soundInfo.volume(), soundInfo.pitch());
        }
        else return false;

        return true;
    }

    @Nullable
    public static SoundInfo getSoundInfo(String dragon, String variant, String sound) {
        if (!ResourceUtil.isResourceReloadFinished) return null;
        generateCache(dragon, variant, sound);
        return soundCache
                .getOrDefault(dragon, Collections.emptyMap())
                .getOrDefault(variant, Collections.emptyMap())
                .get(sound);
    }

    private static void generateCache(String dragon, String variant, String sound) {
        SoundInfo soundInfo = getSoundInfoFromRedirects(dragon, variant, sound);
        Map<String, Map<String, SoundInfo>> variantMap = soundCache.computeIfAbsent(dragon, k -> new HashMap<>());
        Map<String, SoundInfo> soundInfoMap = variantMap.computeIfAbsent(variant, k -> new HashMap<>());
        soundInfoMap.put(sound, soundInfo);
    }

    @Nullable
    private static SoundInfo getSoundInfoFromRedirects(String dragon, String variant, String sound) {
        Map<String, List<SoundRedirect>> variantRedirectMap = soundRedirectMap.get(dragon);
        if (variantRedirectMap == null || !variantRedirectMap.containsKey(variant)) return null;
        List<SoundRedirect> soundInfos = variantRedirectMap.get(variant);
        return soundInfos.stream()
                .filter(namedSoundInfo -> namedSoundInfo.name().equals(sound))
                .findFirst()
                .map(SoundInfo::fromNamed)
                .orElse(null);
    }

    public static void clearCahce() {
        soundCache.clear();
    }

    public static synchronized void add(String dragon, Map<String, List<SoundRedirect>> redirects) {
        Map<String, List<SoundRedirect>> content = soundRedirectMap.get(dragon);
        if (content != null) {
            content.putAll(redirects);
            soundRedirectMap.put(dragon, content);
        } else soundRedirectMap.put(dragon, redirects);
    }

    public record SoundInfo(ResourceLocation id, float volume, float pitch) {
        public static SoundInfo fromNamed(SoundRedirect namedSoundInfo) {
            return new SoundInfo(new ResourceLocation(namedSoundInfo.sound()), namedSoundInfo.volume(), namedSoundInfo.pitch());
        }
    }
}
