package nordmods.iobvariantloader.util.sound_redirect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.network.PlayDragonSoundS2CPacket;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;
import nordmods.iobvariantloader.util.model_redirect.ModelRedirectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SoundRedirectUtil {
    //hardcoded sound names
    public static final String BITE = "bite"; //bite attack sound
    public static final String STING = "sting"; //sting attack sound
    public static final String MELEE_ATTACK = "melee_attack"; //melee attack sound (used only for triple stryke)
    public static final String FIRE = "fire"; //shot/fire attack sound
    public static final String FIRE_WEAK = "fire_weak"; //weak shot/fire attack sound
    public static final String TAME = "tame"; //dragon tame sound
    public static final String SLEEP = "sleep"; //dragon sleep sound
    public static final String GROWL = "growl"; //dragon idle sound
    public static final String FLAP = "flap"; //dragon wing flap sound
    public static final String HURT = "hurt"; //dragon hurt sound
    public static final String DEATH = "death"; //dragon hurt sound
    public static final String STEP = "step"; //dragon step sound

    //sound cache
    //dragon, <variant, <sound, sound info>>
    private static final Map<String, Map<String, Map<String, SoundInfo>>> SOUND_CACHE = new HashMap<>();
    public static final Map<String, Map<String, List<SoundRedirect>>> SOUND_REDIRECT = new HashMap<>();

    public static <T extends Entity & VariantNameHelper & DragonSpeciesHelper> boolean playSound(@NotNull T entity, String sound) {
        SoundRedirectUtil.SoundInfo soundInfo = null;
        if (entity.hasCustomName() && ModelRedirectUtil.isNametagAccessible(entity.getSpecies(entity.level.isClientSide()), entity.getName().getString().toLowerCase())) {
            soundInfo = SoundRedirectUtil.getSoundInfo(entity.getSpecies(false), entity.getName().getString().toLowerCase(), sound);
        }
        if (soundInfo == null) {
            soundInfo = SoundRedirectUtil.getSoundInfo(entity.getSpecies(false), entity.getVariantName(), sound);
        }
        if (soundInfo != null) {
            if (entity.level.isClientSide()) {
                entity.level.playSound(
                        entity.level.players().stream().filter(Player::isLocalPlayer).findFirst().orElse(null), //incredible mental gymnastic
                        entity,
                        new SoundEvent(soundInfo.id()),
                        entity.getSoundSource(),
                        soundInfo.volume(),
                        soundInfo.pitch()
                );
            } else {
                for (Player player : entity.getLevel().players()) {
                    if (player instanceof ServerPlayer serverPlayer)
                        PlayDragonSoundS2CPacket.INSTANCE
                                .send(PacketDistributor.PLAYER.with(() ->
                                                serverPlayer),
                                        new PlayDragonSoundS2CPacket(
                                                entity.getId(),
                                                soundInfo.id(),
                                                soundInfo.pitch(),
                                                soundInfo.pitch()
                                        )
                                );
                }
            }
        }
        else return false;

        return true;
    }

    @Nullable
    public static SoundInfo getSoundInfo(String dragon, String variant, String sound) {
        generateCache(dragon, variant, sound);
        return SOUND_CACHE
                .getOrDefault(dragon, Collections.emptyMap())
                .getOrDefault(variant, Collections.emptyMap())
                .get(sound);
    }

    private static void generateCache(String dragon, String variant, String sound) {
        SoundInfo soundInfo = getSoundInfoFromRedirects(dragon, variant, sound);
        Map<String, Map<String, SoundInfo>> variantMap = SOUND_CACHE.computeIfAbsent(dragon, k -> new HashMap<>());
        Map<String, SoundInfo> soundInfoMap = variantMap.computeIfAbsent(variant, k -> new HashMap<>());
        soundInfoMap.put(sound, soundInfo);
    }

    @Nullable
    private static SoundInfo getSoundInfoFromRedirects(String dragon, String variant, String sound) {
        Map<String, List<SoundRedirect>> variantRedirectMap = SOUND_REDIRECT.get(dragon);
        if (variantRedirectMap == null || !variantRedirectMap.containsKey(variant)) return null;
        List<SoundRedirect> soundInfos = variantRedirectMap.get(variant);
        return soundInfos.stream()
                .filter(namedSoundInfo -> namedSoundInfo.name().equals(sound))
                .findFirst()
                .map(SoundInfo::fromNamed)
                .orElse(null);
    }

    public static void clearCahce() {
        SOUND_CACHE.clear();
    }

    public static synchronized void add(String dragon, Map<String, List<SoundRedirect>> redirects) {
        Map<String, List<SoundRedirect>> content = SOUND_REDIRECT.get(dragon);
        if (content != null) {
            for (Map.Entry<String, List<SoundRedirect>> entry : content.entrySet()) {
                String variant = entry.getKey();
                if (redirects.containsKey(variant)) {
                    List<SoundRedirect> copy = new ArrayList<>();
                    copy.addAll(redirects.get(variant));
                    copy.addAll(entry.getValue());
                    redirects.put(variant, copy);
                }
            }
            content.putAll(redirects);
            SOUND_REDIRECT.put(dragon, content);
        } else SOUND_REDIRECT.put(dragon, redirects);
    }

    public record SoundInfo(ResourceLocation id, float volume, float pitch) {
        public static SoundInfo fromNamed(SoundRedirect namedSoundInfo) {
            return new SoundInfo(new ResourceLocation(namedSoundInfo.sound()), namedSoundInfo.volume(), namedSoundInfo.pitch());
        }
    }

    public static void debugPrint() {
        if (!IoBVariantLoader.config.logSoundRedirects.get()) return;
        for (Map.Entry<String, Map<String, List<SoundRedirect>>> entry : SOUND_REDIRECT.entrySet()) {
            for (Map.Entry<String, List<SoundRedirect>> extrasEntry : entry.getValue().entrySet()){
                StringBuilder info = new StringBuilder();
                for (SoundRedirect redirect : extrasEntry.getValue()) {
                    info.append("Sound: ").append(redirect.name()).append("\n");
                    info.append("-  Id: ").append(redirect.sound()).append("\n");
                    info.append("-  Volume: ").append(redirect.volume()).append("\n");
                    info.append("-  Pitch: ").append(redirect.pitch()).append("\n");
                    info.append("\n");
                }
                IoBVariantLoader.LOGGER.info("{}: variant {} was redirected to:\n{}", entry.getKey(), extrasEntry.getKey(), info);
            }
        }
    }
}
