package nordmods.iobvariantloader.util.breeding_list;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nordmods.iobvariantloader.util.variant_collections.VariantCollectionsUtil;
import nordmods.iobvariantloader.util.variant_collections.VariantList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record BreedingList(Pair<Parent, Parent> parents, List<Entry> entries) {
    public static final Codec<BreedingList> CODEC = RecordCodecBuilder.create(i -> i.group(
            Parent.CODEC.listOf().fieldOf("parents").forGetter(c -> new ArrayList<>(List.of(c.parents().getFirst(), c.parents().getSecond()))),
            Entry.CODEC.listOf().fieldOf("entries").forGetter(BreedingList::entries)
    ).apply(i, (parents, entries) -> new BreedingList(new Pair<>(parents.get(0), parents.get(1)), entries)));

    public record Parent(Optional<List<VariantList>> variantLists, Optional<List<String>> collections) {
        public static final Codec<Parent> CODEC = RecordCodecBuilder.create(i ->  i.group(
                VariantList.CODEC.listOf().optionalFieldOf("variant_lists").forGetter(Parent::variantLists),
                Codec.STRING.listOf().optionalFieldOf("collections").forGetter(Parent::collections)
        ).apply(i, Parent::new));
    }

    public record Entry(Optional<List<VariantList>> variantLists, Optional<List<String>> collections, int weight) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i ->  i.group(
                VariantList.CODEC.listOf().optionalFieldOf("variant_lists").forGetter(Entry::variantLists),
                Codec.STRING.listOf().optionalFieldOf("collections").forGetter(Entry::collections),
                Codec.INT.fieldOf("weight").forGetter(Entry::weight)
        ).apply(i, Entry::new));

        public List<VariantList> collectAllAvailableLists() {
            List<VariantList> available = new ArrayList<>(variantLists().orElse(List.of()));
            collections().ifPresent(collections -> collections.forEach(collection -> available.addAll(VariantCollectionsUtil.getCollectionLists(collection))));

            return available;
        }
    }
}
