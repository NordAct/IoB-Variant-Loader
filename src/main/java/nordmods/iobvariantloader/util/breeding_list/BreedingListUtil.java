package nordmods.iobvariantloader.util.breeding_list;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.mojang.datafixers.util.Pair;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;
import nordmods.iobvariantloader.util.variant_collections.VariantList;
import nordmods.iobvariantloader.util.variant_collections.VariantCollectionsUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BreedingListUtil {
    public static final List<BreedingList> BREEDING_LISTS = new ArrayList<>();

    @Nullable
    public static Pair<String, String> getRandomDragonAndVariant(ADragonBase parent1, ADragonBase parent2) {
        if (parent1.getRandom().nextDouble() < IoBVariantLoader.config.inheritanceChance.get()) {
            boolean takeFromFirstParent = parent1.getRandom().nextBoolean();
            return takeFromFirstParent ?
                    new Pair<>(((DragonSpeciesHelper)parent1).getSpecies(false), ((VariantNameHelper)parent1).getVariantName()) :
                    new Pair<>(((DragonSpeciesHelper)parent2).getSpecies(false), ((VariantNameHelper)parent2).getVariantName()) ;
        }

        List<BreedingList.Entry> entries = getAvailableLists(parent1, parent2)
                .stream()
                .map(BreedingList::entries)
                .filter(list -> !list.isEmpty())
                .flatMap(List::stream)
                .toList();

        int totalWeight = entries.stream().mapToInt(BreedingList.Entry::weight).sum();

        if (totalWeight <= 0) return null;

        long roll = parent1.getRandom().nextLong(totalWeight);
        long previousBound = 0;

        BreedingList.Entry selectedEntry = null;
        for (BreedingList.Entry entry :entries) {
            if (roll >= previousBound && roll < previousBound + entry.weight()) {
                selectedEntry = entry;
                break;
            }
            previousBound += entry.weight();
        }

        if (selectedEntry == null) return null;

        List<VariantList> variantLists = new ArrayList<>(selectedEntry.variantLists().orElse(List.of()));
        selectedEntry.collections().ifPresent(collections -> collections.forEach(collection -> variantLists.addAll(VariantCollectionsUtil.getCollectionLists(collection))));

        VariantList list = variantLists.get(parent1.getRandom().nextInt(variantLists.size()));
        return new Pair<>(list.dragon(), list.variants().get(parent1.getRandom().nextInt(list.variants().size())));
    }

    public static List<BreedingList> getAvailableLists(ADragonBase parent1, ADragonBase parent2) {
        String parent1Variant = ((VariantNameHelper)parent1).getVariantName();
        String parent1Dragon = ((DragonSpeciesHelper)parent1).getSpecies(false);
        List<String> parent1Collections = VariantCollectionsUtil.getVariantCollections(parent1Dragon, parent1Variant);

        String parent2Variant = ((VariantNameHelper)parent2).getVariantName();
        String parent2Dragon = ((DragonSpeciesHelper)parent2).getSpecies(false);
        List<String> parent2Collections = VariantCollectionsUtil.getVariantCollections(parent2Dragon, parent2Variant);

        return BREEDING_LISTS
                .stream()
                .filter(breedingList -> isParentDragonInList(breedingList, parent1Variant, parent1Dragon, parent1Collections, parent2Variant, parent2Dragon, parent2Collections))
                .toList();
    }

    private static boolean isParentDragonInList(
            BreedingList breedingList,
            String parent1Variant,
            String parent1Dragon,
            List<String> parent1Collections,
            String parent2Variant,
            String parent2Dragon,
            List<String> parent2Collections
    ) {
        BreedingList.Parent parent1Entry = breedingList.parents().getFirst();
        BreedingList.Parent parent2Entry = breedingList.parents().getSecond();

        boolean parent1IsInParent1Entry = parent1Entry.variantLists().isPresent() && parent1Entry
                .variantLists()
                .get()
                .stream()
                .anyMatch(variantList ->
                        variantList.dragon().equals(parent1Dragon) && variantList.variants().contains(parent1Variant))
                || parent1Entry.collections().isPresent() && parent1Entry
                .collections()
                .stream()
                .anyMatch(collection ->
                        collection.stream().anyMatch(parent1Collections::contains));

        boolean parent2IsInParent2Entry = parent2Entry.variantLists().isPresent() && parent2Entry
                .variantLists()
                .get()
                .stream()
                .anyMatch(variantList ->
                        variantList.dragon().equals(parent2Dragon) && variantList.variants().contains(parent2Variant))
                || parent2Entry.collections().isPresent() && parent2Entry
                .collections()
                .stream()
                .anyMatch(collection ->
                        collection.stream().anyMatch(parent2Collections::contains));

        if (parent1IsInParent1Entry && parent2IsInParent2Entry) return true;

        boolean parent2IsInParent1Entry = parent1Entry.variantLists().isPresent() && parent1Entry
                .variantLists()
                .get()
                .stream()
                .anyMatch(variantList ->
                        variantList.dragon().equals(parent2Dragon) && variantList.variants().contains(parent2Variant))
                || parent1Entry.collections().isPresent() && parent1Entry
                .collections()
                .stream()
                .anyMatch(collection ->
                        collection.stream().anyMatch(parent2Collections::contains));

        boolean parent1IsInParent2Entry = parent2Entry.variantLists().isPresent() && parent2Entry
                .variantLists()
                .get()
                .stream()
                .anyMatch(variantList ->
                        variantList.dragon().equals(parent1Dragon) && variantList.variants().contains(parent1Variant))
                || parent2Entry.collections().isPresent() && parent2Entry
                .collections()
                .stream()
                .anyMatch(collection ->
                        collection.stream().anyMatch(parent1Collections::contains));

        return parent2IsInParent1Entry && parent1IsInParent2Entry;
    }
}
