package nordmods.iobvariantloader.util.variant_group;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record VariantList(String dragon, List<String> variants) {
    public static final Codec<VariantList> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("dragon").forGetter(VariantList::dragon),
            Codec.STRING.listOf().fieldOf("variants").forGetter(VariantList::variants)
    ).apply(i, VariantList::new));
}
