package com.dipasquale.ai.rl.neat;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class Species<T extends Comparable<T>> {
    private final Context<T> context;
    @Getter
    private final T id;
    private final Population<T> population;
    private Organism<T> originalOrganism;
    private final List<Organism<T>> organisms;
    private boolean isOrganismsSorted;
    @Getter
    private float sharedFitness;
    private float maximumSharedFitness;
    private final int birthGeneration;
    private int ageLastImproved;

    Species(final Context<T> context, final Population<T> population, final Organism<T> originalOrganism) {
        this.context = context;
        this.id = context.general().createSpeciesId();
        this.population = population;
        this.originalOrganism = originalOrganism;
        this.organisms = Arrays.asList(originalOrganism);
        this.isOrganismsSorted = true;
        this.sharedFitness = 0f;
        this.maximumSharedFitness = 0f;
        this.birthGeneration = population.getGeneration();
        this.ageLastImproved = 0;
    }

    public boolean addIfCompatible(final Organism<T> organism) {
        if (organisms.size() < context.speciation().maximumGenomes() && originalOrganism.isCompatible(organism)) {
            organisms.add(organism);
            isOrganismsSorted = false;

            return true;
        }

        return false;
    }

    public int size() {
        return organisms.size();
    }

    private int getAge() {
        return population.getGeneration() - birthGeneration;
    }

    public float updateFitness() {
        float fitnessTotal = 0f;

        for (Organism<T> organism : organisms) {
            float fitness = organism.updateFitness();

            fitnessTotal += fitness;
        }

        float sharedFitnessNew = fitnessTotal / organisms.size();
        int age = getAge();
        int ageLastImprovedNew = Float.compare(sharedFitnessNew, maximumSharedFitness) > 0 ? age : ageLastImproved;

        sharedFitness = sharedFitnessNew;
        maximumSharedFitness = Math.max(sharedFitnessNew, maximumSharedFitness);
        ageLastImproved = ageLastImprovedNew;

        return sharedFitness;
    }

    private void ensureOrganismsIsSorted() {
        if (!isOrganismsSorted) {
            isOrganismsSorted = true;
            Collections.sort(organisms);
        }
    }

    public List<Organism<T>> removeUnfitToReproduce() {
        List<Organism<T>> organismsRemoved = new ArrayList<>();
        int size = organisms.size();

        if (size > 1) {
            int keep = Math.max(1, (int) Math.floor((double) context.speciation().eugenicsThreshold() * (double) size));
            int remove = size - keep;

            ensureOrganismsIsSorted();

            for (int i = 0; i < remove; i++) {
                Organism<T> organism = organisms.remove(0);

                organismsRemoved.add(organism);
            }
        }

        return organismsRemoved;
    }

    public List<Organism<T>> reproduceOutcast(final int count) {
        List<Organism<T>> organismsAdded = new ArrayList<>();
        int size = organisms.size();

        if (size > 0) {
            for (int i = 0; i < count; i++) {
                if (size > 1 && context.random().isLessThan(context.crossOver().rate())) {
                    Organism<T> organism1 = context.random().nextItem(organisms);
                    Organism<T> organism2 = context.random().nextItem(organisms);

                    if (organism1 != organism2) {
                        Organism<T> organismNew = organism1.mate(organism2);

                        organismsAdded.add(organismNew);
                    }
                }

                if (organismsAdded.size() <= i) {
                    Organism<T> organism = context.random().nextItem(organisms);
                    Organism<T> organismNew = organism.createCopy();

                    organismNew.mutate();
                    organismsAdded.add(organismNew);
                }
            }
        }

        return organismsAdded;
    }

    public List<Organism<T>> selectElitists() {
        List<Organism<T>> organismsSelected = new ArrayList<>();
        int size = organisms.size();

        if (size > 1) {
            int select = (int) Math.floor((double) context.speciation().elitistThreshold() * (double) size);
            int selectFixed = size - Math.max(size - select, 1);

            ensureOrganismsIsSorted();

            for (int i = 0, endIndex = size - 1; i < selectFixed; i++) {
                Organism<T> organism = organisms.get(endIndex - i);

                organismsSelected.add(organism);
            }
        }

        return organismsSelected;
    }

    public boolean shouldSurvive() {
        return getAge() - ageLastImproved < context.speciation().dropOffAge();
    }

    public void clear() {
        Organism<T> originalOrganismNew = context.random().nextItem(organisms);

        originalOrganism = originalOrganismNew;
        organisms.clear();
        organisms.add(originalOrganismNew);
        sharedFitness = 0f;
        maximumSharedFitness = 0f;
    }
}

/*
package com.dipasquale.ai.rl.neat;

import com.dipasquale.data.structure.map.SortedByValueMap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class Species<T extends Comparable<T>> {
    private final Context<T> context;
    @Getter
    private final T id;
    private final Population<T> population;
    private Organism<T> originalOrganism;
    private final SortedByValueMap<Organism<T>, Float> organisms;
    @Getter
    private float sharedFitness;
    private float maximumSharedFitness;
    private final int generationBirth;
    private int ageLastImproved;

    Species(final Context<T> context, final Population<T> population, final Organism<T> originalOrganism) {
        this.context = context;
        this.id = context.general().createSpeciesId();
        this.population = population;
        this.originalOrganism = originalOrganism;
        this.organisms = createIdentitySortedByValueMap(originalOrganism, 0f); // TODO: ensure this data structure is not a bottle neck in performance, since it is changing so much, might not be worth using a SortedByValueMap
        this.sharedFitness = 0f;
        this.maximumSharedFitness = 0f;
        this.generationBirth = population.getGeneration();
        this.ageLastImproved = 0;
    }

    private static <TKey, TValue extends Comparable<TValue>> SortedByValueMap<TKey, TValue> createIdentitySortedByValueMap(final TKey key, final TValue value) {
        SortedByValueMap<TKey, TValue> map = SortedByValueMap.createIdentity(TValue::compareTo);

        map.put(key, value);

        return map;
    }

    public boolean addIfCompatible(final Organism<T> organism) {
        if (organisms.size() < context.speciation().maximumGenomes() && originalOrganism.isCompatible(organism)) {
            organisms.put(organism, 0f);

            return true;
        }

        return false;
    }

    public int size() {
        return organisms.size();
    }

    private int getAge() {
        return population.getGeneration() - generationBirth;
    }

    public float updateFitness() {
        List<Map.Entry<Organism<T>, Float>> organismEntries = new ArrayList<>(organisms.entrySet());
        float fitnessTotal = 0f;

        for (Map.Entry<Organism<T>, Float> entry : organismEntries) {
            float fitness = entry.getKey().updateFitness();

            organisms.put(entry.getKey(), fitness);
            fitnessTotal += fitness;
        }

        float sharedFitnessNew = fitnessTotal / organismEntries.size();
        int age = getAge();
        int ageLastImprovedNew = Float.compare(sharedFitnessNew, maximumSharedFitness) > 0 ? age : ageLastImproved;

        sharedFitness = sharedFitnessNew;
        maximumSharedFitness = Math.max(sharedFitnessNew, maximumSharedFitness);
        ageLastImproved = ageLastImprovedNew;

        return sharedFitness;
    }

    public List<Organism<T>> removeUnfitToReproduce() {
        List<Organism<T>> organismsRemoved = new ArrayList<>();
        int size = organisms.size();

        if (size > 1) {
            int keep = Math.max(1, (int) Math.floor((double) context.speciation().eugenicsThreshold() * (double) size));
            int remove = size - keep;

            for (int i = 0; i < remove; i++) {
                Organism<T> organism = organisms.headKey();

                organisms.remove(organism);
                organismsRemoved.add(organism);
            }
        }

        return organismsRemoved;
    }

    public List<Organism<T>> reproduceOutcast(final int count) {
        List<Organism<T>> organismsAdded = new ArrayList<>();

        if (organisms.size() > 0) {
            List<Organism<T>> organismsToReproduce = new ArrayList<>(organisms.keySet());

            for (int i = 0; i < count; i++) {
                if (organismsToReproduce.size() > 1 && context.random().isLessThan(context.crossOver().rate())) {
                    Organism<T> organism1 = context.random().nextItem(organismsToReproduce);
                    Organism<T> organism2 = context.random().nextItem(organismsToReproduce);

                    if (organism1 != organism2) {
                        Organism<T> organismNew = organism1.mate(organism2);

                        organismsAdded.add(organismNew);
                    }
                }

                if (organismsAdded.size() <= i) {
                    Organism<T> organism = context.random().nextItem(organismsToReproduce);
                    Organism<T> organismNew = organism.createCopy();

                    organismNew.mutate();
                    organismsAdded.add(organismNew);
                }
            }
        }

        return organismsAdded;
    }

    public List<Organism<T>> selectElitists() {
        List<Organism<T>> organismsSelected = new ArrayList<>();
        int size = organisms.size();

        if (size > 1) {
            int select = (int) Math.floor((double) context.speciation().elitistThreshold() * (double) size);
            int selectFixed = size - Math.max(size - select, 1);

            for (Organism<T> organism : organisms.descendingKeySet()) {
                organismsSelected.add(organism);

                if (organismsSelected.size() == selectFixed) {
                    return organismsSelected;
                }
            }
        }

        return organismsSelected;
    }

    public boolean shouldSurvive() {
        return getAge() - ageLastImproved < context.speciation().dropOffAge();
    }

    public void restart() {
        Organism<T> originalOrganismNew = context.random().nextItem(new ArrayList<>(organisms.keySet()));

        originalOrganism = originalOrganismNew;
        organisms.clear();
        organisms.put(originalOrganismNew, 0f);
        sharedFitness = 0f;
        maximumSharedFitness = 0f;
    }
}
 */