package nordmods.iobvariantloader.util.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record Extras(
        Optional<String> variantGroup,
        Optional<String> lootTableRedirect
) {
    public static Codec<Extras> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("variant_group").forGetter(Extras::variantGroup),
            Codec.STRING.optionalFieldOf("loot_table_redirect").forGetter(Extras::lootTableRedirect)
    ).apply(instance, Extras::new));
}
