package nordmods.iobvariantloader.util.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record Extras(
        Optional<String> variantGroup,
        Optional<String> lootTableRedirect,
        Optional<List<VariantAttributeModifier>> variantAttributeModifiers
) {
    public static final Codec<Extras> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("variant_group").forGetter(Extras::variantGroup),
            Codec.STRING.optionalFieldOf("loot_table_redirect").forGetter(Extras::lootTableRedirect),
            VariantAttributeModifier.CODEC.listOf().optionalFieldOf("attribute_modifiers").forGetter(Extras::variantAttributeModifiers)
    ).apply(instance, Extras::new));

    public record VariantAttributeModifier(String id, double amount, String operation) {
        public static final Codec<VariantAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Codec.STRING.fieldOf("id").forGetter(VariantAttributeModifier::id),
                        Codec.DOUBLE.fieldOf("amount").forGetter(VariantAttributeModifier::amount),
                        Codec.STRING.fieldOf("operation").forGetter(VariantAttributeModifier::operation)
                ).apply(instance, VariantAttributeModifier::new));
    }
}