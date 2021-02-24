package com.experimental.ai.rl.neat;

import com.dipasquale.data.structure.map.SortedByValueMap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class Species<T extends Comparable<T>> {
    private final Context<T> context;
    private final Population<T> population;
    private Organism<T> originalOrganism;
    private final SortedByValueMap<Organism<T>, Float> organisms;
    private int age;
    @Getter
    private float sharedFitness;
    @Getter
    private float maximumFitness;

    public Species(final Context<T> context, final Population<T> population, final Organism<T> originalOrganism) {
        this.context = context;
        this.population = population;
        this.originalOrganism = originalOrganism;
        this.organisms = createOrganisms(originalOrganism);
        this.age = 1;
        this.sharedFitness = 0f;
        this.maximumFitness = 0f;
    }

    private static <T extends Comparable<T>> SortedByValueMap<Organism<T>, Float> createOrganisms(final Organism<T> originalOrganism) {
        SortedByValueMap<Organism<T>, Float> organisms = SortedByValueMap.createIdentity(Float::compareTo);

        organisms.put(originalOrganism, 0f);

        return organisms;
    }

    private boolean belongs(final Organism<T> organism) {
        return context.speciation().belongs(originalOrganism.getGenome(), organism.getGenome(), population.getGeneration());
    }

    public boolean addIfCompatible(final Organism<T> organism) {
        if (organisms.size() < context.speciation().maximumSize() && belongs(organism)) {
            organisms.put(organism, 0f);

            return true;
        }

        return false;
    }

    public void adjustFitness() {
        List<Map.Entry<Organism<T>, Float>> organismEntries = new ArrayList<>(organisms.entrySet());
        float sharedFitnessTotal = 0f;
        float fitnessMaximum = -Float.MAX_VALUE;

        for (Map.Entry<Organism<T>, Float> entry : organismEntries) {
            float fitness = entry.getKey().determineFitness();
            float sharedFitness = fitness / organismEntries.size();

            sharedFitnessTotal += sharedFitness;
            fitnessMaximum = Math.max(fitness, fitnessMaximum);
            organisms.put(entry.getKey(), sharedFitness);
        }

        sharedFitness = sharedFitnessTotal;
        maximumFitness = fitnessMaximum;
    }

    public List<Organism<T>> removeLeastFit() {
        List<Organism<T>> organismsKilled = new ArrayList<>();
        int size = organisms.size();

        if (size > 1) {
            int keep = Math.max(1, (int) Math.floor((double) context.speciation().survivalThreshold() * (double) organisms.size()));

            for (int i = 0; i < keep; i++) {
                Organism<T> organism = organisms.headKey();

                organisms.remove(organism);
                organismsKilled.add(organism);
            }
        }

        return organismsKilled;
    }

    private Organism<T> create(final GenomeDefault<T> genome) {
        return new Organism<>(context.general(), genome);
    }

    private Organism<T> breed(final Organism<T> organism1, final Organism<T> organism2) {
        if (Float.compare(organisms.get(organism1), organisms.get(organism2)) >= 0) {
            return create(GenomeDefault.crossover(organism1.getGenome(), organism2.getGenome()));
        }

        return create(GenomeDefault.crossover(organism2.getGenome(), organism1.getGenome()));
    }

    public List<Organism<T>> reproduceOutcast(final int count) {
        List<Organism<T>> organismsAdded = new ArrayList<>();

        if (organisms.size() > 0) {
            List<Organism<T>> organismsToReproduce = new ArrayList<>(organisms.keySet()); // TODO: is a random index in a sorted list random enough?

            for (int i = 0; i < count; i++) {
                if (organismsToReproduce.size() > 1 && context.random().isAtMost(context.crossover().rate())) {
                    Organism<T> organism1 = context.random().nextItem(organismsToReproduce);
                    Organism<T> organism2 = context.random().nextItem(organismsToReproduce);

                    if (organism1 != organism2) {
                        Organism<T> organismNew = breed(organism1, organism2);

                        organismsAdded.add(organismNew);
                    }
                }

                if (organismsAdded.size() <= i) {
                    Organism<T> organism = context.random().nextItem(organismsToReproduce);
                    Organism<T> organismNew = create(organism.getGenome().createCopy());

                    organismNew.getGenome().mutate();
                    organismsAdded.add(organismNew);
                }
            }
        }

        return organismsAdded;
    }

    public List<Organism<T>> reproduce(final int count) {
        List<Organism<T>> organismsAdded = reproduceOutcast(count);

        organismsAdded.forEach(o -> organisms.put(o, 0f));

        return organismsAdded;
    }

    public Collection<Organism<T>> restart() {
        Set<Organism<T>> organismsOld = new HashSet<>(organisms.keySet());
        Organism<T> originalOrganismNew = context.random().nextItem(new ArrayList<>(organismsOld));

        originalOrganism = originalOrganismNew;
        organisms.clear();
        organisms.put(originalOrganismNew, 0f);
        sharedFitness = 0f;
        maximumFitness = 0f;
        organismsOld.remove(originalOrganismNew);

        return organismsOld;
    }
}