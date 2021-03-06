package com.dipasquale.ai.rl.neat;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class Species<T extends Comparable<T>> {
    private final Context<T> context;
    @Getter
    private final String id;
    private final Population<T> population;
    private Organism<T> representativeOrganism;
    private final List<Organism<T>> organisms;
    private boolean isOrganismsSorted;
    @Getter
    private float sharedFitness;
    private float maximumSharedFitness;
    private final int birthGeneration;
    private int ageLastImproved;

    Species(final Context<T> context, final Population<T> population, final Organism<T> representativeOrganism) {
        this.context = context;
        this.id = context.general().createSpeciesId();
        this.population = population;
        this.representativeOrganism = representativeOrganism;
        this.organisms = Arrays.asList(representativeOrganism);
        this.isOrganismsSorted = true;
        this.sharedFitness = 0f;
        this.maximumSharedFitness = 0f;
        this.birthGeneration = population.getGeneration();
        this.ageLastImproved = 0;
    }

    public boolean addIfCompatible(final Organism<T> organism) {
        if (organisms.size() < context.speciation().maximumGenomes() && representativeOrganism.isCompatible(organism)) {
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
            int keep = (int) Math.floor((double) context.speciation().eugenicsThreshold() * (double) size);
            int keepFixed = Math.max(1, keep);
            int remove = size - keepFixed;

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

    public Organism<T> reproduceOutcast(final Species<T> other) {
        if (organisms.size() == 0 || other.size() == 0) {
            return null;
        }

        Organism<T> organism1 = context.random().nextItem(organisms);
        Organism<T> organism2 = context.random().nextItem(other.organisms);

        return organism1.mate(organism2);
    }

    public List<Organism<T>> selectElitists() {
        List<Organism<T>> organismsSelected = new ArrayList<>();
        int size = organisms.size();

        if (size > 0) {
            int select = (int) Math.floor((double) context.speciation().elitistThreshold() * (double) size);
            int selectFixed = Math.min(select, context.speciation().elitistThresholdMinimum());

            ensureOrganismsIsSorted();

            for (int i = 0, endIndex = size - 1; i < selectFixed; i++) {
                Organism<T> organism = organisms.get(endIndex - i);

                organismsSelected.add(organism);
            }
        }

        return organismsSelected;
    }

    public boolean shouldSurvive() {
        return getAge() - ageLastImproved < context.speciation().stagnationDropOffAge();
    }

    public Organism<T> restart() {
        Organism<T> representativeOrganismNew = context.random().nextItem(organisms);

        representativeOrganism = representativeOrganismNew;
        organisms.clear();
        organisms.add(representativeOrganismNew);
        isOrganismsSorted = true;
        sharedFitness = 0f;
        maximumSharedFitness = 0f;

        return representativeOrganismNew;
    }
}
