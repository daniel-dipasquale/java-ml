package com.dipasquale.ai.rl.neat.species;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.population.Population;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Species {
    private final Context context;
    @Getter
    private final String id;
    private final Population population;
    private Organism representativeOrganism;
    private List<Organism> organisms;
    private boolean isOrganismsSorted;
    @Getter
    private float sharedFitness;
    private float maximumSharedFitness;
    private final int birthGeneration;
    private int ageLastImproved;

    public Species(final Context context, final Population population, final Organism representativeOrganism) {
        representativeOrganism.setMostCompatibleSpecies(this);

        this.context = context;
        this.id = context.general().createSpeciesId();
        this.population = population;
        this.representativeOrganism = representativeOrganism;
        this.organisms = Lists.newArrayList(representativeOrganism);
        this.isOrganismsSorted = true;
        this.sharedFitness = 0f;
        this.maximumSharedFitness = 0f;
        this.birthGeneration = population.getGeneration();
        this.ageLastImproved = 0;
    }

    public boolean addIfCompatible(final Organism organism) {
        if (organisms.size() < context.speciation().maximumGenomes() && organism.isCompatible(this)) {
            organism.setMostCompatibleSpecies(this);
            organisms.add(organism);
            isOrganismsSorted = false;

            return true;
        }

        return false;
    }

    public void add(final Organism organism) {
        if (organisms.size() < context.speciation().maximumGenomes()) {
            organism.setMostCompatibleSpecies(this);
            organisms.add(organism);
            isOrganismsSorted = false;
        }
    }

    public Organism getRepresentative() {
        return representativeOrganism;
    }

    public int size() {
        return organisms.size();
    }

    private int getAge() {
        return population.getGeneration() - birthGeneration;
    }

    public float updateFitness() {
        float fitnessTotal = 0f;

        for (Organism organism : organisms) {
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

    public List<Organism> removeUnfitToReproduce() {
        int size = organisms.size();

        if (size > 1) {
            int keep = context.speciation().getFitCountToReproduce(size);
            int remove = size - keep;

            if (remove > 0) {
                ensureOrganismsIsSorted();

                List<Organism> organismsRemoved = organisms.subList(0, remove);

                organisms = organisms.subList(remove, size);

                return organismsRemoved;
            }
        }

        return ImmutableList.of();
    }

    public List<Organism> reproduceOutcast(final int count) {
        List<Organism> organismsAdded = new ArrayList<>();
        int size = organisms.size();

        for (int i = 0; i < count; i++) {
            boolean shouldMateAndMutate = context.crossOver().shouldMateAndMutate();
            boolean shouldMate = shouldMateAndMutate || context.crossOver().shouldMateOnly();
            boolean shouldMutate = shouldMateAndMutate || !shouldMate && context.crossOver().shouldMutateOnly();

            if (size > 1 && shouldMate) {
                Organism organism1;
                Organism organism2;

                if (size == 2) {
                    organism1 = organisms.get(0);
                    organism2 = organisms.get(1);
                } else {
                    organism1 = context.random().nextItem(organisms);
                    organism2 = context.random().nextItem(organisms);
                }

                if (organism1 != organism2) {
                    Organism organismNew = organism1.mate(organism2);

                    if (shouldMutate) {
                        organismNew.mutate();
                    }

                    organismNew.freeze();
                    organismsAdded.add(organismNew);
                }
            }

            if (organismsAdded.size() <= i) {
                Organism organism = context.random().nextItem(organisms);
                Organism organismNew = organism.createCopy();

                organismNew.mutate();
                organismNew.freeze();
                organismsAdded.add(organismNew);
            }
        }

        return organismsAdded;
    }

    public Organism reproduceOutcast(final Species other) {
        if (organisms.size() == 0 || other.size() == 0) {
            return null;
        }

        Organism organism1 = context.random().nextItem(organisms);
        Organism organism2 = context.random().nextItem(other.organisms);
        Organism organismNew = organism1.mate(organism2);

        organismNew.freeze();

        return organismNew;
    }

    public Organism selectMostElite() {
        ensureOrganismsIsSorted();

        return organisms.get(organisms.size() - 1);
    }

    public List<Organism> selectMostElites() {
        int size = organisms.size();
        int select = context.speciation().getEliteCountToPreserve(size);

        if (select == 0) {
            return ImmutableList.of();
        }

        ensureOrganismsIsSorted();

        return organisms.subList(size - select, size);
    }

    public boolean shouldSurvive() {
        return getAge() - ageLastImproved < context.speciation().stagnationDropOffAge();
    }

    public List<Organism> restart() {
        List<Organism> organismsOld = organisms;
        Organism representativeOrganismNew = context.random().nextItem(organisms);

        representativeOrganism = representativeOrganismNew;
        organisms = Lists.newArrayList(representativeOrganismNew);
        isOrganismsSorted = true;
        sharedFitness = 0f;

        return organismsOld;
    }
}
