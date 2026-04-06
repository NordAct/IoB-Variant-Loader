package nordmods.iobvariantloader.util.breeding_list;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.mojang.datafixers.util.Pair;
import nordmods.iobvariantloader.util.ducks.DragonSpeciesHelper;
import nordmods.iobvariantloader.util.ducks.VariantNameHelper;
import nordmods.iobvariantloader.util.variant_group.VariantList;
import nordmods.iobvariantloader.util.variant_group.VariantListUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BreedingListUtil {
    public static final List<BreedingList> BREEDING_LISTS = new ArrayList<>();

    @Nullable
    public static Pair<String, String> getRandomDragonAndVariant(List<BreedingList> breedingLists, Random random) {
        List<BreedingList.Entry> entries = breedingLists.stream().map(BreedingList::entries).flatMap(List::stream).toList();

        int totalWeight = entries.stream().mapToInt(BreedingList.Entry::weight).sum();

        if (totalWeight <= 0) return null;

        long roll = random.nextLong(totalWeight);
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
        selectedEntry.groups().ifPresent(groups -> groups.forEach(group -> variantLists.addAll(VariantListUtil.getGroupLists(group))));

        VariantList list = variantLists.get(random.nextInt(variantLists.size()));
        return new Pair<>(list.dragon(), list.variants().get(random.nextInt(list.variants().size())));
    }

    public static List<BreedingList> getAvailableLists(ADragonBase parent1, ADragonBase parent2) {
        String parent1Variant = ((VariantNameHelper)parent1).getVariantName();
        String parent1Dragon = ((DragonSpeciesHelper)parent1).getSpecies(false);
        List<String> parent1Groups = VariantListUtil.getVariantGroups(parent1Dragon, parent1Variant);

        String parent2Variant = ((VariantNameHelper)parent2).getVariantName();
        String parent2Dragon = ((DragonSpeciesHelper)parent2).getSpecies(false);
        List<String> parent2Groups = VariantListUtil.getVariantGroups(parent2Dragon, parent2Variant);

        return BREEDING_LISTS
                .stream()
                .filter(breedingList -> isParentDragonInList(breedingList, parent1Variant, parent1Dragon, parent1Groups, parent2Variant, parent2Dragon, parent2Groups))
                .toList();
    }

    private static boolean isParentDragonInList(
            BreedingList breedingList,
            String parent1Variant,
            String parent1Dragon,
            List<String> parent1Groups,
            String parent2Variant,
            String parent2Dragon,
            List<String> parent2Groups
    ) {
        BreedingList.Parent parent1Entry = breedingList.parents().getFirst();
        BreedingList.Parent parent2Entry = breedingList.parents().getSecond();

        boolean parent1IsInParent1Entry = parent1Entry.variantLists().isPresent() && parent1Entry
                .variantLists()
                .get()
                .stream()
                .anyMatch(variantList ->
                        variantList.dragon().equals(parent1Dragon) && variantList.variants().contains(parent1Variant))
                || parent1Entry.groups().isPresent() && parent1Entry
                .groups()
                .stream()
                .anyMatch(group ->
                        group.stream().anyMatch(parent1Groups::contains));

        boolean parent2IsInParent2Entry = parent2Entry.variantLists().isPresent() && parent2Entry
                .variantLists()
                .get()
                .stream()
                .anyMatch(variantList ->
                        variantList.dragon().equals(parent2Dragon) && variantList.variants().contains(parent2Variant))
                || parent2Entry.groups().isPresent() && parent2Entry
                .groups()
                .stream()
                .anyMatch(group ->
                        group.stream().anyMatch(parent2Groups::contains));

        if (parent1IsInParent1Entry && parent2IsInParent2Entry) return true;

        boolean parent2IsInParent1Entry = parent1Entry.variantLists().isPresent() && parent1Entry
                .variantLists()
                .get()
                .stream()
                .anyMatch(variantList ->
                        variantList.dragon().equals(parent2Dragon) && variantList.variants().contains(parent2Variant))
                || parent1Entry.groups().isPresent() && parent1Entry
                .groups()
                .stream()
                .anyMatch(group ->
                        group.stream().anyMatch(parent2Groups::contains));

        boolean parent1IsInParent2Entry = parent2Entry.variantLists().isPresent() && parent2Entry
                .variantLists()
                .get()
                .stream()
                .anyMatch(variantList ->
                        variantList.dragon().equals(parent1Dragon) && variantList.variants().contains(parent1Variant))
                || parent2Entry.groups().isPresent() && parent2Entry
                .groups()
                .stream()
                .anyMatch(group ->
                        group.stream().anyMatch(parent1Groups::contains));

        return parent2IsInParent1Entry && parent1IsInParent2Entry;
    }
}
