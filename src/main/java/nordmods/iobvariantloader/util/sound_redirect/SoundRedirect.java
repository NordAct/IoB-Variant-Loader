package nordmods.iobvariantloader.util.sound_redirect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SoundRedirect(String name, String sound, float volume, float pitch){
    public static Codec<SoundRedirect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(SoundRedirect::name),
            Codec.STRING.fieldOf("id").forGetter(SoundRedirect::sound),
            Codec.FLOAT.optionalFieldOf("volume", 1f).forGetter(SoundRedirect::volume),
            Codec.FLOAT.optionalFieldOf("pitch", 1f).forGetter(SoundRedirect::pitch)
    ).apply(instance, SoundRedirect::new));
}
