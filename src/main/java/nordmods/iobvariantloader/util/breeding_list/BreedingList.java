package nordmods.iobvariantloader.util.breeding_list;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nordmods.iobvariantloader.util.variant_group.VariantList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record BreedingList(Pair<Parent, Parent> parents, List<Entry> entries) {
    public static final Codec<BreedingList> CODEC = RecordCodecBuilder.create(i -> i.group(
            Parent.CODEC.listOf().fieldOf("parents").forGetter(c -> new ArrayList<>(List.of(c.parents().getFirst(), c.parents().getSecond()))),
            Entry.CODEC.listOf().fieldOf("entries").forGetter(BreedingList::entries)
    ).apply(i, (parents, entries) -> new BreedingList(new Pair<>(parents.get(0), parents.get(1)), entries)));

    public record Parent(Optional<List<VariantList>> variantLists, Optional<List<String>> groups) {
        public static final Codec<Parent> CODEC = RecordCodecBuilder.create(i ->  i.group(
                VariantList.CODEC.listOf().optionalFieldOf("variant_lists").forGetter(Parent::variantLists),
                Codec.STRING.listOf().optionalFieldOf("groups").forGetter(Parent::groups)
        ).apply(i, Parent::new));
    }

    public record Entry(Optional<List<VariantList>> variantLists, Optional<List<String>> groups, int weight) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i ->  i.group(
                VariantList.CODEC.listOf().optionalFieldOf("variant_lists").forGetter(Entry::variantLists),
                Codec.STRING.listOf().optionalFieldOf("groups").forGetter(Entry::groups),
                Codec.INT.fieldOf("weight").forGetter(Entry::weight)
        ).apply(i, Entry::new));
    }
}
