package nordmods.iobvariantloader.util.model_redirect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;
//egg item models are processed separately for technical reasons, see ModelBakeryMixin
public record ModelRedirect(
        Optional<String> texture,
        Optional<String> model,
        Optional<String> animation,
        Optional<String> saddle,
        Optional<String> babyTexture,
        Optional<String> babyModel,
        Optional<String> babyAnimation,
        Optional<String> babySaddle,
        Optional<String> eggModel,
        Optional<String> eggTexture,
        Optional<String> eggItemName,
        Optional<String> eggName,
        Optional<String> dragonName,
        boolean nametagAccessible) {
    public static Codec<ModelRedirect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("texture").forGetter(ModelRedirect::texture),
            Codec.STRING.optionalFieldOf("model").forGetter(ModelRedirect::model),
            Codec.STRING.optionalFieldOf("animation").forGetter(ModelRedirect::animation),
            Codec.STRING.optionalFieldOf("saddle").forGetter(ModelRedirect::saddle),
            Codec.STRING.optionalFieldOf("baby_texture").forGetter(ModelRedirect::babyTexture),
            Codec.STRING.optionalFieldOf("baby_model").forGetter(ModelRedirect::babyModel),
            Codec.STRING.optionalFieldOf("baby_animation").forGetter(ModelRedirect::babyAnimation),
            Codec.STRING.optionalFieldOf("baby_saddle").forGetter(ModelRedirect::babySaddle),
            Codec.STRING.optionalFieldOf("egg_model").forGetter(ModelRedirect::eggModel),
            Codec.STRING.optionalFieldOf("egg_texture").forGetter(ModelRedirect::eggModel),
            Codec.STRING.optionalFieldOf("egg_item_name").forGetter(ModelRedirect::eggItemName),
            Codec.STRING.optionalFieldOf("egg_name").forGetter(ModelRedirect::eggName),
            Codec.STRING.optionalFieldOf("dragon_name").forGetter(ModelRedirect::dragonName),
            Codec.BOOL.optionalFieldOf("nametag_accessible", true).forGetter(ModelRedirect::nametagAccessible)
    ).apply(instance, ModelRedirect::new));
}
