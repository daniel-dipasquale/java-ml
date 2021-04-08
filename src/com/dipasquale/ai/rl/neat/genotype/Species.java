package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.population.Population;
import com.dipasquale.common.Pair;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Species implements Serializable {
    @Serial
    private static final long serialVersionUID = -186124256671311508L;
    @Getter
    private final String id;
    private final Population population;
    private Organism representativeOrganism;
    private List<Organism> organisms;
    private List<Organism> organismsReadOnly;
    private boolean isOrganismsSorted;
    @Getter
    private float sharedFitness;
    private float maximumSharedFitness;
    private final int birthGeneration;
    private int ageLastImproved;

    public Species(final Context context, final Population population, final Organism representativeOrganism) {
        List<Organism> organisms = Lists.newArrayList(representativeOrganism);

        this.id = context.general().createSpeciesId();
        this.population = population;
        this.representativeOrganism = representativeOrganism;
        representativeOrganism.setMostCompatibleSpecies(this);
        this.organisms = organisms;
        this.organismsReadOnly = Collections.unmodifiableList(organisms);
        this.isOrganismsSorted = true;
        this.sharedFitness = 0f;
        this.maximumSharedFitness = 0f;
        this.birthGeneration = population.getGeneration();
        this.ageLastImproved = 0;
    }

    public Organism getRepresentative() {
        return representativeOrganism;
    }

    public List<Organism> getOrganisms() {
        return organismsReadOnly;
    }

    private void setOrganisms(final List<Organism> newOrganisms) {
        organisms = newOrganisms;
        organismsReadOnly = Collections.unmodifiableList(newOrganisms);
    }

    private int getAge() {
        return population.getGeneration() - birthGeneration;
    }

    public boolean addIfCompatible(final Context.Speciation speciation, final Organism organism) {
        if (organisms.size() < speciation.maximumGenomes() && organism.isCompatible(speciation, this)) {
            organism.setMostCompatibleSpecies(this);
            organisms.add(organism);
            isOrganismsSorted = false;

            return true;
        }

        return false;
    }

    public void add(final Context.Speciation speciation, final Organism organism) {
        if (organisms.size() < speciation.maximumGenomes()) {
            organism.setMostCompatibleSpecies(this);
            organisms.add(organism);
            isOrganismsSorted = false;
        }
    }

    private float updateFitness(final OrganismFitness organismFitness) {
        float fitnessTotal = organisms.stream()
                .map(organismFitness::getOrUpdate)
                .reduce(0f, Float::sum);

        float sharedFitnessNew = fitnessTotal / organisms.size();

        sharedFitness = sharedFitnessNew;

        if (Float.compare(sharedFitnessNew, maximumSharedFitness) > 0) {
            maximumSharedFitness = sharedFitnessNew;
            ageLastImproved = getAge();
        }

        return sharedFitness;
    }

    public float updateSharedFitness() {
        return updateFitness(Organism::getFitness);
    }

    public float updateFitness(final Context.GeneralSupport general) {
        return updateFitness(o -> o.updateFitness(general));
    }

    private void ensureOrganismsIsSorted() {
        if (!isOrganismsSorted) {
            isOrganismsSorted = true;
            Collections.sort(organisms);
        }
    }

    public List<Organism> removeUnfitToReproduce(final Context.Speciation speciation) {
        int size = organisms.size();

        if (size > 1) {
            int keep = speciation.getFitCountToReproduce(size);
            int remove = size - keep;

            if (remove > 0) {
                ensureOrganismsIsSorted();

                List<Organism> organismsRemoved = organisms.subList(0, remove);

                setOrganisms(organisms.subList(remove, size));

                return organismsRemoved;
            }
        }

        return ImmutableList.of();
    }

    public List<OrganismFactory> getOrganismsToBirth(final Context context, final int count) {
        List<OrganismFactory> organismsToBirth = new ArrayList<>();
        int size = organisms.size();

        for (int i = 0; i < count; i++) {
            boolean shouldMateAndMutate = context.crossOver().shouldMateAndMutate();
            boolean shouldMate = shouldMateAndMutate || context.crossOver().shouldMateOnly();
            boolean shouldMutate = shouldMateAndMutate || !shouldMate && context.crossOver().shouldMutateOnly();

            if (size > 1 && shouldMate) {
                Pair<Organism> organismPair = context.random().nextUniquePair(organisms);

                organismsToBirth.add(new OrganismFactoryMating(organismPair.getItem1(), organismPair.getItem2(), shouldMutate));
            } else {
                Organism organism = context.random().nextItem(organisms);

                organismsToBirth.add(new OrganismFactoryMutation(organism));
            }
        }

        return organismsToBirth;
    }

    public OrganismFactory getOrganismToBirth(final Context.Random random, final Species other) {
        if (organisms.size() == 0 || other.getOrganisms().size() == 0) {
            return null;
        }

        Organism organism1 = random.nextItem(organisms);
        Organism organism2 = random.nextItem(other.organisms);

        return new OrganismFactoryMating(organism1, organism2, false);
    }

    public Organism selectMostElite() {
        ensureOrganismsIsSorted();

        return organisms.get(organisms.size() - 1);
    }

    public List<Organism> selectMostElites(final Context.Speciation speciation) {
        int size = organisms.size();
        int select = speciation.getEliteCountToPreserve(size);

        if (select == 0) {
            return ImmutableList.of();
        }

        ensureOrganismsIsSorted();

        return organisms.subList(size - select, size);
    }

    public boolean shouldSurvive(final Context.Speciation speciation) {
        return getAge() - ageLastImproved < speciation.stagnationDropOffAge();
    }

    public List<Organism> restart(final Context.Random random) {
        int index = random.nextIndex(organisms.size());
        Organism representativeOrganismNew = organisms.remove(index);
        List<Organism> organismsOld = organisms;

        representativeOrganism = representativeOrganismNew;
        setOrganisms(Lists.newArrayList(representativeOrganismNew));
        isOrganismsSorted = true;
        sharedFitness = 0f;

        return organismsOld;
    }

    @FunctionalInterface
    private interface OrganismFitness {
        float getOrUpdate(Organism organism);
    }
}
